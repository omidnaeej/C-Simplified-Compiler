package main.visitor;

import main.ast.nodes.*;
import main.ast.nodes.declarations.*;
import main.ast.nodes.expression.ArgumentExpressionList;
import main.ast.nodes.expression.CastExpression;
import main.ast.nodes.expression.Expression;
import main.ast.nodes.externaldeclaration.*;
import main.ast.nodes.statements.*;

import java.util.ArrayList;
import java.util.Arrays;

public class MyASTVisitor extends Visitor<Void> {

    private int countNestedStatements(Statement statement) {
        if (statement == null) return 0;

        if (statement.getStmtType() == "compoundStatement") {
            int count = 0;
            CompoundStatement compound = statement.getCompoundStatement();
            if (compound.getBlockItems() != null) {
                for (BlockItem blockItem : compound.getBlockItems()) {
                    if (blockItem.getStatement() != null) {
                        Statement stmt = blockItem.getStatement();
                        switch (stmt.getStmtType()) {
                            case "selectionStatement":
                                count += 1;
//                                while (stmt.getSelectionStatement().getElse_statement() != null) {
//                                    count++;
//                                    stmt = stmt.getSelectionStatement().getElse_statement();
//                                    if (stmt.getSelectionStatement() == null) break;
//                                }
                                break;
                            default:
                                count += 1;  // counts as 1
                                break;
                        }
                    }
                    if (blockItem.getDeclaration() != null) count++;
                }
            }
            return count;
        }
        return 1; // Single statement counts as 1
    }

    @Override
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
        int count = 0;
        if (functionDefinition.getCompoundStatement() != null) {
            for (BlockItem blockItem : functionDefinition.getCompoundStatement().getBlockItems()) {
                Statement stmt = blockItem.getStatement();
                if (stmt != null) {
                    switch (stmt.getStmtType()) {
                        case "selectionStatement":
                            count += 1;
                            //                            nestedSelStmts.add(stmt.getSelectionStatement());
//                            while (stmt.getSelectionStatement().getElse_statement() != null) {
//                                count++;
//                                stmt = stmt.getSelectionStatement().getElse_statement();
//                                if (stmt.getSelectionStatement() == null) break;
//                            }
                            break;
                        default:
                            count += 1;  // counts as 1
                            break;
                    }
                }
                if (blockItem.getDeclaration() != null) count++;
            }
        }
        int paramCount = 0;
        if (functionDefinition.getDeclarator().getDirectDeclarator().getParameterList() != null) {
            paramCount = functionDefinition.getDeclarator()
                    .getDirectDeclarator()
                    .getParameterList()
                    .getParameterDeclarations()
                    .size();
        }

        String stmtstr = ": Stmt function " + functionDefinition.getDeclarator().getDirectDeclarator().getNestedDirectDeclarator().getIdentifier() + " = " + count;
        System.out.println("Line " + functionDefinition.getLine() + stmtstr + " " + paramCount);

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
            expression.getExpressions().get(0).accept(this);
            if (expression.getArgumentExpressionList() != null) {
                expression.getArgumentExpressionList().accept(this);
            }
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