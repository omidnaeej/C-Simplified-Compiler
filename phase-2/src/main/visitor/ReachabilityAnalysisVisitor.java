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

import java.util.*;

public class ReachabilityAnalysisVisitor extends Visitor<Void> {
    private final Program program;
    private final Map<String, FunctionDefinition> functionMap = new HashMap<>();
    private final Set<String> reachable = new HashSet<>();

    public ReachabilityAnalysisVisitor(Program program) {
        this.program = program;
    }

    public void run() {
        buildFunctionMap();
        markReachableFromMain();
        pruneUnreachableFunctions();
    }

    private void buildFunctionMap() {
        for (ExternalDeclaration ext : program.getTranslationUnit().getExternalDeclarations()) {
            if (ext instanceof FunctionDefinition) {
                FunctionDefinition fd = (FunctionDefinition) ext;
                String name = fd.getDeclarator()
                        .getDirectDeclarator()
                        .getNestedDirectDeclarator()
                        .getIdentifier();
                functionMap.put(name, fd);
            }
        }
    }

    private void markReachableFromMain() {
        Deque<String> worklist = new ArrayDeque<>();
        if (functionMap.containsKey("main")) {
            reachable.add("main");
            worklist.add("main");
        }

        CallCollector collector = new CallCollector();
        while (!worklist.isEmpty()) {
            String fn = worklist.removeFirst();
            FunctionDefinition fd = functionMap.get(fn);
            fd.getCompoundStatement().accept(collector);
            for (String callee : collector.getCalledFunctions()) {
                if (functionMap.containsKey(callee) && reachable.add(callee)) {
                    worklist.add(callee);
                }
            }
            collector.clear();
        }
//        System.out.println(">>> reachable = " + reachable);
    }

    private void pruneUnreachableFunctions() {
        ArrayList<ExternalDeclaration> kept = new ArrayList<>();
        for (ExternalDeclaration ext : program.getTranslationUnit().getExternalDeclarations()) {
            if (ext instanceof FunctionDefinition) {
                FunctionDefinition fd = (FunctionDefinition) ext;
                String name = fd.getDeclarator()
                        .getDirectDeclarator()
                        .getNestedDirectDeclarator()
                        .getIdentifier();
                if (!reachable.contains(name)) {
                    continue;
                }
            }
            kept.add(ext);
        }
        program.getTranslationUnit().setExternalDeclarations(kept);
    }

    private static class CallCollector extends Visitor<Void> {
        private final Set<String> calls = new HashSet<>();

        @Override
        public Void visit(Expression expression) {
            if ("Function call".equals(expression.getExpressionType())) {
                Expression designator = expression.getExpressions().get(0);
                if (designator.getIdentifier() != null) {
                    calls.add(designator.getIdentifier());
                }

                ArgumentExpressionList args = expression.getArgumentExpressionList();
                if (args != null) args.accept(this);
                return null;
            }
            return super.visit(expression);
        }

        public Void visit(Program program) {
            if (program.getTranslationUnit() != null) {
                program.getTranslationUnit().accept(this);
            }
            return null;
        }

        public Void visit(TranslationUnit translationUnit) {
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
            if (functionDefinition.getDeclarator().getDirectDeclarator().getParameterList() != null) {
                functionDefinition.getDeclarator().getDirectDeclarator().getParameterList().accept(this);
            }

            if (functionDefinition.getDeclarationList() != null) {
                functionDefinition.getDeclarationList().accept(this);
            }
            if (functionDefinition.getCompoundStatement() != null) {
                functionDefinition.getCompoundStatement().accept(this);
            }
            return null;
        }

        public Void visit(CompoundStatement compoundStatement) {
            for (BlockItem blockItem : compoundStatement.getBlockItems()) {
                blockItem.accept(this);
            }
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

        Set<String> getCalledFunctions() {
            return calls;
        }

        void clear() {
            calls.clear();
        }
    }
}