package main.visitor;

import main.ast.nodes.declarations.*;
import main.ast.nodes.externaldeclaration.*;
import main.ast.nodes.*;
import main.ast.nodes.statements.*;
import main.ast.nodes.expression.*;

public abstract class Visitor<T> implements IVisitor<T> {
    @Override
    public T visit(Program program) {
        return null;
    }

    public T visit(TranslationUnit translationUnit) {
        return null;
    }

    public T visit(FunctionDefinition functionDefinition) {
        return null;
    }

    public T visit(Declaration declaration) {
        return null;
    }

    public T visit(EmptyDeclaration emptyDeclaration) {
        return null;
    }

    public T visit(Declarator declarator) {
        return null;
    }
    public T visit(DeclarationList declarationList) {
        return null;
    }
    public T visit(CompoundStatement compoundStatement) {
        return null;
    }

    public T visit(DeclarationSpecifier declarationSpecifier) {
        return null;
    }

    public T visit(DeclarationSpecifiers declarationSpecifiers) {
        return null;
    }

    public T visit(InitDeclaratorList initDeclaratorList) {
        return null;
    }
    public T visit(TypeSpecifier typeSpecifier) {
        return null;
    }

    public T visit(InitDeclarator initDeclarator) {
        return null;
    }
    public T visit(DirectDeclarator directDeclarator) {
        return null;
    }

    public T visit(Pointer pointer) {
        return null;
    }

    public T visit(Initializer initializer) {
        return null;
    }

    public T visit(Expression expression) {
        return null;
    }

    public T visit(ParameterList parameterList) {
        return null;
    }

    public T visit(IdentifierList identifierList) {
        return null;
    }

    public T visit(ParameterDeclaration parameterDeclaration) {
        return null;
    }

    public T visit(AbstractDeclarator abstractDeclarator) {
        return null;
    }

    public T visit(InitializerList initializerList) {
        return null;
    }

    public T visit(DirectAbstractDeclarator directAbstractDeclarator) {
        return null;
    }

    public T visit(Designation designation) {
        return null;
    }

    public T visit(Designator designator) {
        return null;
    }

    public T visit(BlockItem blockItem) {
        return null;
    }

    public T visit(Statement statement) {
        return null;
    }

    public T visit(ExpressionStatement expressionStatement) {
        return null;
    }

    public T visit(IterationStatement iterationStatement) {
        return null;
    }

    public T visit(SelectionStatement selectionStatement) {
        return null;
    }

    public T visit(JumpStatement jumpStatement) {
        return null;
    }

    public T visit(ForCondition forCondition) {
        return null;
    }

    public T visit(ForDeclaration forDeclaration) {
        return null;
    }

    public T visit(ForExpression forExpression) {
        return null;
    }

    public T visit(TypeName typeName) {
        return null;
    }

    public T visit(SpecifierQualifierList specifierQualifierList) {
        return null;
    }

    public T visit(CastExpression castExpression) {
        return null;
    }

    public T visit(AssignmentOperator assignmentOperator) {
        return null;
    }

    public T visit(UnaryOperator unaryOperator) {
        return null;
    }

    public T visit(ArgumentExpressionList argumentExpressionList) {
        return null;
    }
}
