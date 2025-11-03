package main.visitor;

import main.ast.nodes.*;
import main.ast.nodes.declarations.*;
import main.ast.nodes.expression.ArgumentExpressionList;
import main.ast.nodes.expression.CastExpression;
import main.ast.nodes.expression.Expression;
import main.ast.nodes.externaldeclaration.Declaration;
import main.ast.nodes.externaldeclaration.ExternalDeclaration;
import main.ast.nodes.externaldeclaration.FunctionDefinition;
import main.ast.nodes.statements.*;
import main.symbolTable.SymbolTable;
import main.symbolTable.exceptions.ItemAlreadyExistsException;
import main.symbolTable.exceptions.ItemNotFoundException;
import main.symbolTable.item.FuncDefSymbolTableItem;
import main.symbolTable.item.SymbolTableItem;
import main.symbolTable.item.VarDecSymbolTableItem;

import java.util.*;
import java.util.stream.Collectors;

public class MyTypeChecker extends Visitor<Void> {
    private boolean detectError = false;
    private String type;
    private boolean tempo_add = false;
    private ArrayList<String> tempo_var = new ArrayList<>();
    private ArrayList<String> func_arg_type = new ArrayList<>();
    ArrayList<String> cFunctions = new ArrayList<>(Arrays.asList(
            // Standard I/O functions
            "printf", "scanf", "fgets", "fputs",

            // String manipulation functions
            "strlen", "strcpy", "strcat", "strcmp", "strtok",

            // Memory management functions
            "malloc", "calloc", "realloc", "free",

            // File handling functions
            "fopen", "fclose", "fread", "fwrite",

            // Mathematical functions
            "sqrt", "pow", "abs", "ceil", "floor",

            // Character functions
            "toupper", "tolower", "isdigit", "isalpha",

            // Time functions
            "time", "clock",

            // Miscellaneous functions
            "exit", "system"
    ));

    private int func_arg_count;

    public boolean isDetectError() {
        return detectError;
    }

    public void setDetectError(boolean detectError) {
        this.detectError = detectError;
    }

    // Helper: given a root expression, collect all noncomma leaves into out
    private void collectArgs(Expression expr, List<Expression> out) {
        if ("Comma operator".equals(expr.getExpressionType())) {
            // left and right are subexpressions
            List<Expression> kids = expr.getExpressions();
            collectArgs(kids.get(0), out);
            collectArgs(kids.get(1), out);
        } else {
            // a real argument (identifier, constant, call, etc.)
            out.add(expr);
        }
    }

    private static final Map<String,Integer> TYPE_RANK = Map.ofEntries(
            Map.entry("char",               0),
            Map.entry("short",              0),
            Map.entry("int",                1),
            Map.entry("unsigned int",       2),
            Map.entry("long int",           3),
            Map.entry("long",               3),
            Map.entry("unsigned long int",  4),
            Map.entry("unsigned long",      4),
            Map.entry("float",              5),
            Map.entry("double",             6),
            Map.entry("long double",        7)
    );

    private boolean isAssignable(String have, String want) {
        if (have.equals(want)) {
            return true;
        }
        Integer hi = TYPE_RANK.get(have);
        Integer wi = TYPE_RANK.get(want);
//        System.out.println(have +" "+hi);
//        System.out.println(want +" "+wi);
        return hi != null && wi != null && hi <= wi;
    }

    private Deque<String> returnTypeStack = new ArrayDeque<>();


    public String findExpressionType(Expression expression) {
        if ("Function call".equals(expression.getExpressionType())) {
            String funcName = expression.getExpressions().get(0).getIdentifier();

            List<Expression> flatArgs = new ArrayList<>();
            ArgumentExpressionList argList = expression.getArgumentExpressionList();
            if (argList != null) {
                for (Expression subtree : argList.getExpressions())
                    collectArgs(subtree, flatArgs);
            }
            List<String> rawTypes = flatArgs.stream()
                    .map(this::findExpressionType)
                    .collect(Collectors.toList());

            FuncDefSymbolTableItem chosen = null;
            for (SymbolTableItem it : SymbolTable.top.getAllItems()) {
                if (it instanceof FuncDefSymbolTableItem f
                        && f.getName().equals(funcName)
                        && f.getParameterTypes().size() == rawTypes.size()) {
                    boolean ok = true;
                    for (int i = 0; i < rawTypes.size(); i++) {
                        if (!isAssignable(rawTypes.get(i), f.getParameterTypes().get(i))) {
                            ok = false;
                            break;
                        }
                    }
                    if (ok) { chosen = f; break; }
                }
            }

            // 4) return its declared return type (or "unknown")
            return (chosen != null)
                    ? chosen.getReturnType()
                    : "unknown";
        }

        // Case 1: Identifier (variable)
        if (expression.getIdentifier() != null) {
            String id = expression.getIdentifier();
            try {
                VarDecSymbolTableItem varItem =
                        (VarDecSymbolTableItem) SymbolTable.top.getItem(
                                VarDecSymbolTableItem.START_KEY + id);
                return varItem.getType();
            } catch (ItemNotFoundException e) {
                detectError = true;
                return "unknown";
            }
        }

        // Case 2: Constant (literal)
        if (expression.getConstant() != null) {
            String constant = expression.getConstant();

            // char literal: single-quoted, exactly three chars: 'l'
            if (constant.length() == 3 &&
                    constant.charAt(0) == '\'' &&
                    constant.charAt(2) == '\'') {
                return "char";
            }

            if (constant.length() == 3 &&
                    constant.charAt(0) == '\"' &&
                    constant.charAt(2) == '\"') {
                return "char";
            }

            try {
                Integer.parseInt(constant);
                return "int";
            } catch (NumberFormatException e1) {
                try {
                    Double.parseDouble(constant);
                    return "float";
                } catch (NumberFormatException e2) {
                    return "unknown";
                }
            }
        }

        // Case 3: Any two operand expression -> treat as binary
        List<Expression> children = expression.getExpressions();
        if (children != null && children.size() == 2) {
            String leftType  = findExpressionType(children.get(0));
            String rightType = findExpressionType(children.get(1));

            // if one side unknown, use the other
            if ("unknown".equals(leftType))  return rightType;
            if ("unknown".equals(rightType)) return leftType;
            if (leftType.equals(rightType)) return leftType;

            // else promote via TYPE_RANK
            Integer lr = TYPE_RANK.get(leftType);
            Integer rr = TYPE_RANK.get(rightType);
            if (lr != null && rr != null) {
                return (lr >= rr) ? leftType : rightType;
            }

            return "unknown";
        }

        if (expression.getExpressionType() == "String"
                && expression.getStringLiterals().get(0).length()==3) return "char";

        return "unknown";
    }

    private boolean isIntegral(String t) {
        Integer r = TYPE_RANK.get(t);
        return r != null && r <= TYPE_RANK.get("long");
    }

    private void reportTypeError(Expression expr) {
        System.out.printf("Line:%d -> type mismatch in expression%n",
                expr.getLine());
        detectError = true;
    }

    @Override
    public Void visit(Program program) {
        if (program.getTranslationUnit() != null) {
            program.getTranslationUnit().accept(this);
        }
        return null;
    }

    public Void visit(TranslationUnit translationUnit) {
        SymbolTable.top = new SymbolTable();
        SymbolTable.root = SymbolTable.top;

        translationUnit.setSymbolTable(SymbolTable.top);

        for (ExternalDeclaration externalDeclaration : translationUnit.getExternalDeclarations()) {
            externalDeclaration.accept(this);
        }
        return null;
    }

    public Void visit(InitDeclaratorList initDeclaratorList) {
        for (InitDeclarator initDeclarator : initDeclaratorList.getInitDeclarators()) {
            initDeclarator.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(InitDeclarator initDeclarator) {
        initDeclarator.getDeclarator().accept(this);

        Expression initExpr = initDeclarator.getInitializer() != null
                ? initDeclarator.getInitializer().getExpression()
                : null;
        if (initExpr != null) {
            initExpr.accept(this);

            String rhsType = findExpressionType(initExpr);

            // look up LHS type, catching the exception
            String lhsType;
            try {
                String varName = initDeclarator
                        .getDeclarator()
                        .getDirectDeclarator()
                        .getIdentifier();
                VarDecSymbolTableItem varItem = (VarDecSymbolTableItem)
                        SymbolTable.top.getItem(
                                VarDecSymbolTableItem.START_KEY + varName
                        );
                lhsType = varItem.getType();
            } catch (ItemNotFoundException e) {
                // this really shouldn't happen for a just declared var,
                // but we'll mark an error and treat the LHS as unknown
                detectError = true;
                lhsType = "unknown";
            }

            // check assignability
            if (!isAssignable(rhsType, lhsType)) {
                boolean isIntLiteral = initExpr.getConstant() != null
                        && initExpr.getConstant().matches("\\d+");
                if (!(isIntLiteral && isIntegral(lhsType))) {
                    reportTypeError(initExpr);
                }
            }
        }
        return null;
    }


    public Void visit(Initializer initializer) {
        if (initializer.getExpression() != null) {
            initializer.getExpression().accept(this);
        }
        if (initializer.getInitializerList() != null) {
            initializer.getInitializerList().accept(this);
        }
        return null;
    }

    public Void visit(InitializerList initializerList) {
        if (initializerList == null) {
            return null;
        }
        ArrayList<Designation> designations = initializerList.getDesignations();
        ArrayList<Initializer> initializers = initializerList.getInitializers();

        if (designations != null && initializers != null) {
            for (int i = 0; i < designations.size(); i++) {
                designations.get(i).accept(this);
                initializers.get(i).accept(this);
            }
        }
        return null;
    }

    public Void visit(Designation designation) {
        for (Designator designator : designation.getDesignators()) {
            designator.accept(this);
        }
        return null;
    }

    public Void visit(Designator designator) {
        if (designator.getExpression() != null) {
            designator.getExpression().accept(this);
        }
        return null;
    }

    public Void visit(Declarator declarator) {
        declarator.getDirectDeclarator().accept(this);
        return null;
    }

    public Void visit(DirectDeclarator directDeclarator) {
        if (directDeclarator.getIdentifier() != null) {
            String id = directDeclarator.getIdentifier();

            if (this.tempo_add) {
                this.tempo_var.add(id);
            } else {
                VarDecSymbolTableItem var_dec_item = new VarDecSymbolTableItem(id, type);
                try {
                    SymbolTable.top.put(var_dec_item);
                } catch (ItemAlreadyExistsException e) {
//                    System.out.println("Redeclaration of variable \"" + id +"\" in line " + typeSpecifier.getLine());
                }
            }
        }

        if (directDeclarator.getDeclarator()!=null) {
            directDeclarator.getDeclarator().accept(this);
        } else if (directDeclarator.getNestedDirectDeclarator()!=null) {
            directDeclarator.getNestedDirectDeclarator().accept(this);
            if (directDeclarator.getExpression() != null) {
                directDeclarator.getExpression().accept(this);
            } else if (directDeclarator.getParameterList() != null) {
                directDeclarator.getParameterList().accept(this);
            } else if (directDeclarator.getIdentifierList() != null) {
                directDeclarator.getIdentifierList().accept(this);
            }
        }
        return null;
    }

    public Void visit(ParameterList parameterList) {
        for (ParameterDeclaration parameterDeclaration : parameterList.getParameterDeclarations()) {
            parameterDeclaration.accept(this);
        }
        return null;
    }

    public Void visit(ParameterDeclaration parameterDeclaration) {
        this.tempo_add = true;
        parameterDeclaration.getDeclarationSpecifiers().accept(this);

        if (parameterDeclaration.getDeclarator() != null) {
            parameterDeclaration.getDeclarator().accept(this);
        }
        if (parameterDeclaration.getAbstractDeclarator() != null) {
            parameterDeclaration.getAbstractDeclarator().accept(this);
        }
        return null;
    }

    public Void visit(TypeName typeName) {
        typeName.getSpecifierQualifierList().accept(this);
        if (typeName.getAbstractDeclarator() != null) {
            typeName.getAbstractDeclarator().accept(this);
        }
        return null;
    }

    public Void visit(AbstractDeclarator abstractDeclarator) {
        if (abstractDeclarator.getDirectAbstractDeclarator() != null) {
            abstractDeclarator.getDirectAbstractDeclarator().accept(this);
        }
        return null;
    }

    public Void visit(DirectAbstractDeclarator directAbstractDeclarator) {
        if (directAbstractDeclarator.getDirectAbstractDeclarator() != null) {
            directAbstractDeclarator.getDirectAbstractDeclarator().accept(this);
            if (directAbstractDeclarator.getExpression() != null) {
                directAbstractDeclarator.getExpression().accept(this);
            }
            if (directAbstractDeclarator.getParameterList() != null) {
                directAbstractDeclarator.getParameterList().accept(this);
            }
        }
        else if (directAbstractDeclarator.getExpression() != null) {
            directAbstractDeclarator.getExpression().accept(this);
        }
        else if (directAbstractDeclarator.getAbstractDeclarator() != null) {
            directAbstractDeclarator.getAbstractDeclarator().accept(this);
        }
        else if (directAbstractDeclarator.getParameterList() != null) {
            directAbstractDeclarator.getParameterList().accept(this);
        }
        return null;
    }

    public Void visit(DeclarationSpecifiers declarationSpecifiers) {
        for (DeclarationSpecifier declarationSpecifier : declarationSpecifiers.getDecSpecifiers()) {
            declarationSpecifier.accept(this);
        }
        return null;
    }

    public Void visit(Statement statement) {
        String type = statement.getStmtType();

        switch (type) {
            case "compoundStatement":
                statement.getCompoundStatement().accept(this);
                break;
            case "selectionStatement":
                statement.getSelectionStatement().accept(this);
                break;
            case "iterationStatement":
                statement.getIterationStatement().accept(this);
                break;
            case "expressionStatement":
                statement.getExpressionStatement().accept(this);
                break;
            default:
                statement.getJumpStatement().accept(this);
                break;
        }
        return null;
    }

    public Void visit(JumpStatement jumpStatement) {
        // Only care about return statements:
        if (Objects.equals(jumpStatement.getJumpType(), "return")) {
            String currentReturnType = returnTypeStack.peek();

            if (jumpStatement.getExpression() != null) {
                // get and check the returned expression type
                Expression retExpr = jumpStatement.getExpression();
                String have = findExpressionType(retExpr);
                // allow promotions (e.g. byte->int, float->double)
                String want = currentReturnType;

//                System.out.println(have+" should be "+want);
                if (!isAssignable(have, want)) {
                    System.out.printf("Line:%d -> return type mismatch\n", jumpStatement.getLine());
                    detectError = true;
                }
                // still visit inside to catch nested errors
                retExpr.accept(this);
            } else {
                // void functions may return void; non void may not omit an expression
                if (!"void".equals(currentReturnType)) {
                    System.out.printf("Line:%d -> return type mismatch\n", jumpStatement.getLine());
                    detectError = true;
                }
            }
        } else {
            // for break/continue etc, just recurse into any subexpression
            if (jumpStatement.getExpression() != null)
                jumpStatement.getExpression().accept(this);
        }
        return null;
    }

    public Void visit(FunctionDefinition functionDefinition) {
        FuncDefSymbolTableItem func_def_item = new FuncDefSymbolTableItem(functionDefinition);
        func_arg_type = new ArrayList<>();

        StringBuilder declaredReturn = new StringBuilder();
        List<DeclarationSpecifier> declaredSpcs = functionDefinition.getDeclarationSpecifiers().getDecSpecifiers();
        for (int i = 0; i < declaredSpcs.size(); i++) {
            declaredReturn.append(declaredSpcs.get(i)
                            .getTypeSpecifier()
                            .getType());
            if (i < declaredSpcs.size() - 1) {
                declaredReturn.append(' ');
            }
        }
        returnTypeStack.push(declaredReturn.toString());

        if (functionDefinition.getDeclarator().getDirectDeclarator().getParameterList() != null) {
            functionDefinition.getDeclarator().getDirectDeclarator().getParameterList().accept(this);
        }

        if (functionDefinition.getDeclarationList() != null) {
            functionDefinition.getDeclarationList().accept(this);
        }

        try {
            SymbolTable.top.put(func_def_item);
        } catch (ItemAlreadyExistsException e) {
//            System.out.println("Redefinition of function \""
//                    + functionDefinition.getDeclarator().getDirectDeclarator().getNestedDirectDeclarator().getIdentifier()
//                    + "\" in line " + functionDefinition.getLine());
        }

        if (functionDefinition.getCompoundStatement() != null) {
            functionDefinition.getCompoundStatement().accept(this);
        }

        returnTypeStack.pop();
//        SymbolTable.pop();

        return null;
    }

    public Void visit(CompoundStatement compoundStatement) {
        SymbolTable stmt_symbol_table = new SymbolTable(SymbolTable.top);
        compoundStatement.setSymbolTable(stmt_symbol_table);
        SymbolTable.push(stmt_symbol_table);

        if (this.tempo_add) {
            for (String id: this.tempo_var) {
                VarDecSymbolTableItem var_dec_item = new VarDecSymbolTableItem(id, type);
                try {
                    SymbolTable.top.put(var_dec_item);
                } catch (ItemAlreadyExistsException e) {
//                    System.out.println("Redeclaration of variable \"" + id +"\" in line ?" );
                }
            }
            this.tempo_var.clear();
            this.tempo_add = false;
        }

        for (BlockItem blockItem : compoundStatement.getBlockItems()) {
            blockItem.accept(this);
        }

        SymbolTable.pop();

        return null;
    }

    public Void visit(BlockItem blockItem) {
        if (blockItem.getDeclaration() != null) {
            blockItem.getDeclaration().accept(this);
        }
        if (blockItem.getStatement() != null) {
            blockItem.getStatement().accept(this);
        }
        return null;
    }

    public Void visit(Declaration declaration) {
        declaration.getDeclarationSpecifiers().accept(this);
        if (declaration.getInitDeclaratorList() != null) {
            declaration.getInitDeclaratorList().accept(this);
        }
        return null;
    }

    public Void visit(IterationStatement iterationStatement) {
        if (iterationStatement.getLoopType() == "for"){
            iterationStatement.getForCondition().accept(this);
            iterationStatement.getStatement().accept(this);
        }
        else if (iterationStatement.getLoopType() == "while"){
            iterationStatement.getExpression().accept(this);
            iterationStatement.getStatement().accept(this);
        }
        else {
            iterationStatement.getStatement().accept(this);
            iterationStatement.getExpression().accept(this);
        }
        return null;
    }

    public Void visit(SelectionStatement selectionStatement) {
        // Visit IF condition expression
        if (selectionStatement.getExpression() != null)
            selectionStatement.getExpression().accept(this);


        // Collect expressions and statements from elseif chain
        ArrayList<Expression> collectedExprs = new ArrayList<>();
        ArrayList<Statement> collectedStmts = new ArrayList<>();

        Statement elseStmt = selectionStatement.getElse_statement();
        while (elseStmt != null && "selectionStatement".equals(elseStmt.getStmtType())) {
            SelectionStatement elseIf = elseStmt.getSelectionStatement();

            if (elseIf.getExpression() != null)
                collectedExprs.add(elseIf.getExpression());

            collectedStmts.add(elseIf.getIf_statement());
            selectionStatement = selectionStatement.getElse_statement().getSelectionStatement();
            elseStmt = elseIf.getElse_statement();
        }

        // Handle final ELSE
        if (elseStmt != null) {
            collectedStmts.add(elseStmt);
        }

        selectionStatement.getIf_statement().accept(this);
        for (Expression e : collectedExprs) {
            e.accept(this);
        }
        for (Statement s : collectedStmts) {
            s.accept(this);
        }

        return null;
    }

    public Void visit(ExpressionStatement expressionStatement) {
        if (expressionStatement.getExpression() != null) {
            expressionStatement.getExpression().accept(this);
        }
        return null;
    }

    public Void visit(ForCondition forCondition) {
        if (forCondition.getForDeclaration() != null) {
            forCondition.getForDeclaration().accept(this);
        }
        if (forCondition.getExpression() != null) {
            forCondition.getExpression().accept(this);
        }
        if (forCondition.getForExpression() != null) {
            forCondition.getForExpression().accept(this);
        }
        if (forCondition.getForExpression2() != null) {
            forCondition.getForExpression2().accept(this);
        }
        return null;
    }

    public Void visit(ForDeclaration forDeclaration) {
        this.tempo_add = true;

        forDeclaration.getDeclarationSpecifiers().accept(this);
        if (forDeclaration.getInitDeclaratorList() != null) {
            forDeclaration.getInitDeclaratorList().accept(this);
        }
        return null;
    }

    public Void visit(ForExpression forExpression) {
        for (Expression exp : forExpression.getExpressions()) {
            exp.accept(this);
        }
        return null;
    }

    public Void visit(CastExpression castExpression) {
        if (castExpression.getTypeName() != null) {
            castExpression.getTypeName().accept(this);
            castExpression.getCastExpression().accept(this);
        } else if (castExpression.getExpression() != null) {
            castExpression.getExpression().accept(this);
        }
        return null;
    }

    public Void visit(Expression expression) {
        if (expression.getExpressionType().equals("(expression)")) {
            expression.getExpressions().get(0).accept(this);
        }
        else if (expression.getExpressionType().equals("initializing typeName")) {
            expression.getTypeName().accept(this);
            expression.getInitializerList().accept(this);
        }
        else if (expression.getExpressionType().equals("Array indexing")) {
            for (Expression exp : expression.getExpressions()) {
                exp.accept(this);
            }
        }

        else if ("Function call".equals(expression.getExpressionType())) {
            // 1) get callee name
            Expression calleeExpr = expression.getExpressions().get(0);
            String funcName = calleeExpr.getIdentifier();

            // 2) flatten the commaoperator tree into a simple list
            ArgumentExpressionList argList = expression.getArgumentExpressionList();
            List<Expression> flatArgs = new ArrayList<>();
            if (argList != null) {
                for (Expression subtree : argList.getExpressions()) {
                    collectArgs(subtree, flatArgs);
                }
            }
            int argCount = flatArgs.size();

            // 3) compute each raw type
            List<String> rawTypes = new ArrayList<>();
            for (Expression arg : flatArgs) {
                String rt;
                if ("Identifier".equals(arg.getExpressionType())) {
                    try {
                        VarDecSymbolTableItem v = (VarDecSymbolTableItem)
                                SymbolTable.top.getItem(
                                        VarDecSymbolTableItem.START_KEY + arg.getIdentifier()
                                );
                        rt = v.getType();
                    } catch (ItemNotFoundException ex) {
                        rt = "unknown"; detectError = true;
                        System.out.printf("Line:%d -> '%s' not declared%n",
                                arg.getLine(), arg.getIdentifier());
                    }
                }
                else if (arg.getConstant() != null) {
                    String c = arg.getConstant();
                    try { Integer.parseInt(c); rt = "int"; }
                    catch(Exception x1) {
                        try { Double.parseDouble(c); rt = "float"; }
                        catch(Exception x2)           { rt = "unknown"; }
                    }
                }
                else {
                    rt = findExpressionType(arg);
                }
                rawTypes.add(rt);
            }

            // 4) collect all same name, same arity overloads:
            List<FuncDefSymbolTableItem> candidates = new ArrayList<>();
            for (SymbolTableItem it : SymbolTable.top.getAllItems()) {
                if (it instanceof FuncDefSymbolTableItem) {
                    FuncDefSymbolTableItem f = (FuncDefSymbolTableItem)it;
                    if (f.getName().equals(funcName)
                            && f.getParameterTypes().size() == rawTypes.size()) {
                        candidates.add(f);
                    }
                }
            }

            // 5) pick the first whose parameters are all assignable
            FuncDefSymbolTableItem chosen = null;
            for (FuncDefSymbolTableItem f : candidates) {
                List<String> want = f.getParameterTypes();
                boolean ok = true;
                for (int i = 0; i < want.size(); i++) {
                    if (!isAssignable(rawTypes.get(i), want.get(i))) {
                        ok = false;
                        break;
                    }
                }
                if (ok) {
                    chosen = f;
                    break;
                }
            }

            // 6) error if none matched
            if (chosen == null) {
                if (!cFunctions.contains(funcName)) {
                    System.out.printf("Line:%d -> argument type mismatch%n", expression.getLine());
                }
            }
            else {
                // 7) (optional) recheck & report any narrowing
                List<String> want = chosen.getParameterTypes();
                for (int i = 0; i < want.size(); i++) {
                    if (!isAssignable(rawTypes.get(i), want.get(i))) {
                        System.out.printf("Line:%d -> argument type mismatch%n", expression.getLine());
                    }
                }
                // you can also store chosen.getReturnType() if needed
            }

            // 8) recurse into args
            if (expression.getExpressions() != null) {
                for (Expression exp: expression.getExpressions()) exp.accept(this);
            }
            if (argList != null) argList.accept(this);
            return null;
        }


        else if (expression.getExpressionType().contains("Postfix")) {
            expression.getExpressions().get(0).accept(this);
        }

        else if (expression.getExpressionType().equals("(expression) with Prefix operators")) {
            expression.getExpressions().get(0).accept(this);
        }

        else if (expression.getExpressionType().equals("initializing typeName witfh Prefix operators")) {
            expression.getTypeName().accept(this);
            expression.getInitializerList().accept(this);
        }

        else if (expression.getExpressionType().contains("Prefix")) {
            try {
                expression.getExpressions().get(0).accept(this);
            } catch (Exception exception) {

            }
        }

        else if (expression.getExpressionType().equals("Unary Operator with Cast expression")) {
            expression.getUnaryOperator().accept(this);
            expression.getCastExpression().accept(this);
        }

        else if (expression.getExpressionType().equals("Sizeof")) {
            expression.getTypeName().accept(this);
        }

        else if (expression.getExpressionType().equals("Cast expression")) {
            expression.getTypeName().accept(this);
            expression.getCastExpression().accept(this);
        }

        else if (expression.getExpressionType().equals("Multiplicative") ||
                expression.getExpressionType().equals("Additive") ||
                expression.getExpressionType().equals("Shift") ||
                expression.getExpressionType().equals("Relational") ||
                expression.getExpressionType().equals("Equality") ||
                expression.getExpressionType().contains("Bitwise") ||
                expression.getExpressionType().equals("Logical")) {

                Expression left  = expression.getExpressions().get(0);
                Expression right = expression.getExpressions().get(1);

                left.accept(this);
                right.accept(this);

                String lType = findExpressionType(left);
                String rType = findExpressionType(right);

                String op = expression.getOperatorType();

                switch (op) {
                    case "+": case "-": case "*": case "/": case "%":
                        if (!(TYPE_RANK.containsKey(lType) && TYPE_RANK.containsKey(rType))) {
                            reportTypeError(expression);
                            return null;
                        }
                        break;

                    case "<<": case ">>": case ">>>":
                        if (!(isIntegral(lType) && isIntegral(rType))) {
                            reportTypeError(expression);
                        }
                        break;

                    case "&": case "|": case "^":
                        if (!(isIntegral(lType) && isIntegral(rType))) {
                            reportTypeError(expression);
                        }
                        break;

                    case "&&": case "||":
                        if (!("int".equals(lType) && "int".equals(rType))) {
                            reportTypeError(expression);
                        }
                        break;

                    case "<": case ">": case "<=": case ">=": case "==": case "!=":
                        if (!(TYPE_RANK.containsKey(lType) && TYPE_RANK.containsKey(rType))) {
                            reportTypeError(expression);
                        }
                        break;

                    default:
                        // other operators
                        break;
                }

                if (op.equals("&&")||op.equals("||")||op.equals("==")||op.equals("!=")
                        ||op.equals("<")||op.equals(">")||op.equals("<=")||op.equals(">=")) {
                    this.type = "int";  // or "boolean" if you support it
                } else {
                    // numeric promotion: pick the wider of lType/rType
                    Integer lr = TYPE_RANK.get(lType), rr = TYPE_RANK.get(rType);
                    this.type = (lr >= rr) ? lType : rType;
                }

                return null;
            }


        else if (expression.getExpressionType().equals("Conditional operator")) {
            for (Expression expression1: expression.getExpressions()) expression1.accept(this);
        }

        else if (expression.getIdentifier() != null) {
            String id = expression.getIdentifier();

            try {
                // Try to find the variable
                SymbolTable.top.getItem(VarDecSymbolTableItem.START_KEY + id);
            } catch (ItemNotFoundException e1) {
                if (! this.tempo_var.contains(id)) {
                    try {
                        // If not a variable, try to find a function
                        String lookupKey = FuncDefSymbolTableItem.START_KEY + id;
//                    System.out.println(lookupKey);

                        SymbolTable.top.getItem(lookupKey, func_arg_count);
                    } catch (ItemNotFoundException e2) {
                        // If neither found and not a C function, print error
                        if (!cFunctions.contains(id)) {
//                            System.out.println("Line:" + expression.getLine() + "-> " + id + " not declared");
                            this.detectError = true;
                        }
                    }
                }
            }


        }

        else if (expression.getExpressionType().equals("Assignment")) {
            Expression lhs = expression.getExpressions().get(0);
            Expression rhs = expression.getExpressions().get(1);

            lhs.accept(this);
            rhs.accept(this);

            String leftType  = findExpressionType(lhs);
            String rightType = findExpressionType(rhs);

            if (!isAssignable(rightType, leftType)) {
                System.out.printf(
                        "Line:%d -> type mismatch in expression%n",
                        expression.getLine()
                );
                detectError = true;
            }
            return null;
        }

        else {
            if (expression.getExpressions() != null) {
                for (Expression exp : expression.getExpressions()) exp.accept(this);
            }
        }
        return null;
    }

    public Void visit(DeclarationList declarationList) {
        for (Declaration declaration: declarationList.getDeclarations()) {
            declaration.accept(this);
        }
        return null;
    }

    public Void visit(ArgumentExpressionList argumentExpressionList) {
        for (Expression expression: argumentExpressionList.getExpressions()) {
            expression.accept(this);
        }
        return null;
    }

    public Void visit(DeclarationSpecifier declarationSpecifier) {
        if (declarationSpecifier.getTypeSpecifier() != null) {
            declarationSpecifier.getTypeSpecifier().accept(this);
        }
        return null;
    }

    public Void visit(TypeSpecifier typeSpecifier) {
//        System.out.println(typeSpecifier.isIdentifier());
        if (!typeSpecifier.isIdentifier()) {
            type = typeSpecifier.getType();
        } else {
            String id = typeSpecifier.getType();
            if (this.tempo_add) {
                this.tempo_var.add(id);
            } else {
                // Ensure the type is passed correctly when creating VarDecSymbolTableItem
                VarDecSymbolTableItem var_dec_item = new VarDecSymbolTableItem(id, type);
                try {
                    SymbolTable.top.put(var_dec_item);
                    // Print or log the item insertion
                } catch (ItemAlreadyExistsException e) {
                    // Handle redeclaration
//                    System.out.println("Redeclaration of variable \"" + id + "\" at line " + typeSpecifier.getLine());
                }
            }
        }
        return null;
    }

    public Void visit(IdentifierList identifierList) {
        return null;
    }

    public Void visit(SpecifierQualifierList specifierQualifierList) {
        if (specifierQualifierList.getTypeSpecifier() != null) {
            specifierQualifierList.getTypeSpecifier().accept(this);
        }
        if (specifierQualifierList.getNestedSpecifierQualifierList() != null) {
            specifierQualifierList.getNestedSpecifierQualifierList().accept(this);
        }
        return null;
    }


}