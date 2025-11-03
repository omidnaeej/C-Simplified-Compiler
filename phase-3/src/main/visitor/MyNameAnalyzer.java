package main.visitor;

import main.ast.nodes.*;
import main.ast.nodes.declarations.*;
import main.ast.nodes.expression.ArgumentExpressionList;
import main.ast.nodes.expression.CastExpression;
import main.ast.nodes.expression.Expression;
import main.ast.nodes.externaldeclaration.*;
import main.ast.nodes.statements.*;
import main.symbolTable.SymbolTable;
import main.symbolTable.exceptions.ItemAlreadyExistsException;
import main.symbolTable.exceptions.ItemNotFoundException;
import main.symbolTable.item.FuncDefSymbolTableItem;
import main.symbolTable.item.SymbolTableItem;
import main.symbolTable.item.VarDecSymbolTableItem;

import java.util.ArrayList;
import java.util.Arrays;

public class MyNameAnalyzer extends Visitor<Void> {
    private boolean detectError = false;
    private String type;
    private boolean tempo_add = false;
    private ArrayList<String> tempo_var = new ArrayList<>();
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

    private int countArgsTree(Expression expr) {
        if ("Comma operator".equals(expr.getExpressionType())) {
            Expression left  = expr.getExpressions().get(0);
            Expression right = expr.getExpressions().get(1);
            return countArgsTree(left) + countArgsTree(right);
        } else {
            return 1;
        }
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

    public Void visit(InitDeclarator initDeclarator) {
        initDeclarator.getDeclarator().accept(this);
        if (initDeclarator.getInitializer() != null) {
            initDeclarator.getInitializer().accept(this);
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
        if (jumpStatement.getExpression() != null) {
            jumpStatement.getExpression().accept(this);
        }
        return null;
    }

    public Void visit(FunctionDefinition functionDefinition) {
        FuncDefSymbolTableItem func_def_item = new FuncDefSymbolTableItem(functionDefinition);
        try {
            SymbolTable.top.put(func_def_item);
        } catch (ItemAlreadyExistsException e) {
//            System.out.println("Redefinition of function \""
//                    + functionDefinition.getDeclarator().getDirectDeclarator().getNestedDirectDeclarator().getIdentifier()
//                    + "\" in line " + functionDefinition.getLine());
        }

        if (functionDefinition.getDeclarator().getDirectDeclarator().getParameterList() != null) {
            functionDefinition.getDeclarator().getDirectDeclarator().getParameterList().accept(this);
        }

        if (functionDefinition.getDeclarationList() != null) {
            functionDefinition.getDeclarationList().accept(this);
        }
        if (functionDefinition.getCompoundStatement() != null) {
            functionDefinition.getCompoundStatement().accept(this);
        }

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


        // Collect expressions and statements from else-if chain
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

//    private Type getArgumentTypeFromSymbolTable(Expression argExpr) {
//        // Check if it's a variable reference
//        if ("Variable".equals(argExpr.getExpressionType())) {
//            String varName = argExpr.getIdentifier();
//
//            // Look up the variable in the symbol table to retrieve its type
//            SymbolTableItem varItem = SymbolTable.top.getItem(varName);
//            if (varItem instanceof VarDecSymbolTableItem) {
//                return ((VarDecSymbolTableItem) varItem).getType(); // Assuming VariableSymbolTableItem has a method getType
//            }
//        }
//        // Handle other expression types (e.g., literals) by retrieving their types directly
//        else if ("Literal".equals(argExpr.getExpressionType())) {
//            return argExpr.getType(); // If it's a literal, we can directly return its type
//        }
//
//        // Add more cases as necessary for different expression types (arrays, function calls, etc.)
//
//        return null; // Return null if type cannot be determined
//    }

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
            Expression calleeExpr = expression.getExpressions().get(0);
            String funcName = calleeExpr.getIdentifier();

            int argCount = 0;
            ArgumentExpressionList argList = expression.getArgumentExpressionList();
            if (argList != null && !argList.getExpressions().isEmpty()) {
                if (argList.getExpressions().size() > 1) {
                    argCount = argList.getExpressions().size();
                }
                else {
                    Expression maybeTree = argList.getExpressions().get(0);
                    if ("Comma operator".equals(maybeTree.getExpressionType())) {
                        argCount = countArgsTree(maybeTree);
                    } else {
                        argCount = 1;
                    }
                }
            }

            this.func_arg_count = argCount;
            String lookupKey = FuncDefSymbolTableItem.START_KEY + funcName;
            try {
                SymbolTable.top.getItem(lookupKey, argCount);
            } catch (ItemNotFoundException e) {
                if (!cFunctions.contains(funcName)) {
                    System.out.printf(
                            "Line:%d-> %s not declared%n",
                            expression.getLine(),
                            funcName
                    );
                    this.detectError = true;
                }
            }

//            expression.getExpressions().get(0).accept(this);
            if (expression.getArgumentExpressionList() != null) {
                expression.getArgumentExpressionList().accept(this);
            }
            return null;
        }
//        else if ("Function call".equals(expression.getExpressionType())) {
//            // Get the callee expression and the function name
//            Expression calleeExpr = expression.getExpressions().get(0);
//            String funcName = calleeExpr.getIdentifier();
//
//            // Initialize argument count
//            int argCount = 0;
//            ArgumentExpressionList argList = expression.getArgumentExpressionList();
//
//            // Determine the number of arguments
//            if (argList != null && !argList.getExpressions().isEmpty()) {
//                if (argList.getExpressions().size() > 1) {
//                    argCount = argList.getExpressions().size();
//                } else {
//                    Expression maybeTree = argList.getExpressions().get(0);
//                    if ("Comma operator".equals(maybeTree.getExpressionType())) {
//                        argCount = countArgsTree(maybeTree);
//                    } else {
//                        argCount = 1;
//                    }
//                }
//            }
//
//            this.func_arg_count = argCount;
//
//            // Prepare the lookup key for the function in the symbol table
//            StringBuilder lookupKeyBuilder = new StringBuilder(FuncDefSymbolTableItem.START_KEY + funcName);
//
//            // Get the argument types by checking the symbol table
//            List<Type> argumentTypes = new ArrayList<>();
//            if (argList != null) {
//                for (Expression argExpr : argList.getExpressions()) {
//                    // Retrieve the type of the argument expression from the symbol table
//                    Type argType = getArgumentTypeFromSymbolTable(argExpr);
//                    argumentTypes.add(argType);
//                }
//            }
//
//            // Append the parameter types to the lookup key
//            for (Type argType : argumentTypes) {
//                lookupKeyBuilder.append("_").append(argType.toString());
//            }
//
//            String lookupKey = lookupKeyBuilder.toString();
//
//            // Look up the function in the symbol table with the new key format
//            try {
//                SymbolTable.top.getItem(lookupKey);
//            } catch (ItemNotFoundException e) {
//                if (!cFunctions.contains(funcName)) {
//                    System.out.printf(
//                            "Line:%d-> %s not declared%n",
//                            expression.getLine(),
//                            funcName
//                    );
//                    this.detectError = true;
//                }
//            }
//
//            // Recursively accept the argument list for further analysis
//            if (expression.getArgumentExpressionList() != null) {
//                expression.getArgumentExpressionList().accept(this);
//            }
//
//            return null;
//        }
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

        else if (expression.getIdentifier() != null) {
            String id = expression.getIdentifier();

            try {
                // Try to find the variable
                SymbolTable.top.getItem(VarDecSymbolTableItem.START_KEY + id);
            } catch (ItemNotFoundException e1) {
                if (! this.tempo_var.contains(id)) {
                    try {
                        // If not a variable, try to find a function
                        String lookupKey = FuncDefSymbolTableItem.START_KEY + id + "_" + func_arg_count;
//                    System.out.println(lookupKey);

                        SymbolTable.top.getItem(lookupKey, func_arg_count);
                    } catch (ItemNotFoundException e2) {
                        // If neither found and not a C function, print error
                        if (!cFunctions.contains(id)) {
                            System.out.println("Line:" + expression.getLine() + "-> " + id + " not declared");
                            this.detectError = true;
                        }
                    }
                }
            }


        }
        else if (expression.getExpressionType().equals("Assignment")) {
            expression.getExpressions().get(0).accept(this);
            expression.getAssignmentOperator().accept(this);
            expression.getExpressions().get(1).accept(this);
        }
        else {
            for (Expression exp : expression.getExpressions()) {
                exp.accept(this);
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
        if (! typeSpecifier.isIdentifier()) {
            type = typeSpecifier.getType();
        } else {
            String id = typeSpecifier.getType();
            if (this.tempo_add) {
                this.tempo_var.add(id);
            } else {
                VarDecSymbolTableItem var_dec_item = new VarDecSymbolTableItem(id, type);
                try {
                    SymbolTable.top.put(var_dec_item);
//                    System.out.println(id + " defined at line " + typeSpecifier.getLine());
                } catch (ItemAlreadyExistsException e) {
//                    System.out.println("Redeclaration of variable \"" + id +"\" in line " + typeSpecifier.getLine());
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