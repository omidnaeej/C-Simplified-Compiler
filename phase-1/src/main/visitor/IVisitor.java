package main.visitor;

import main.ast.nodes.externaldeclaration.*;
import main.ast.nodes.declarations.*;
import main.ast.nodes.*;
import main.ast.nodes.statements.*;
import main.ast.nodes.expression.*;

//import main.ast.nodes.Stmt.Assign;
//import main.ast.nodes.Stmt.FuncCallStmt;
//import main.ast.nodes.Stmt.IfStmt;
//import main.ast.nodes.Stmt.VarDec;
//import main.ast.nodes.Stmt.Return;
//import main.ast.nodes.declaration.FuncDec;
//import main.ast.nodes.declaration.Main;
//import main.ast.nodes.expr.BinaryExpr;
//import main.ast.nodes.expr.Identifier;
//import main.ast.nodes.expr.primitives.*;
//import main.ast.nodes.expr.UnaryExpr;
//import main.ast.nodes.expr.FuncCallExpr;


public interface IVisitor<T> {

    T visit(Program program);

    T visit(TranslationUnit translationUnit);
    T visit(Declaration declaration);

    T visit(FunctionDefinition functionDefinition);
    T visit(EmptyDeclaration emptyDeclaration);
    T visit(DeclarationSpecifiers declarationSpecifiers);

    T visit(Declarator declarator);
    T visit(DeclarationList declarationList);

    T visit(CompoundStatement compoundStatement);

    T visit(DeclarationSpecifier declarationSpecifier);

    T visit(InitDeclaratorList initDeclaratorList);

    T visit(TypeSpecifier typeSpecifier);

    T visit(InitDeclarator initDeclarator);

    T visit(DirectDeclarator directDeclarator);

    T visit(Pointer pointer);

    T visit(Initializer initializer);

    T visit(Expression expression);

    T visit(IdentifierList identifierList);

    T visit(ParameterList parameterList);

    T visit(ParameterDeclaration parameterDeclaration);

    T visit(AbstractDeclarator abstractDeclarator);

    T visit(InitializerList initializerList);

    T visit(DirectAbstractDeclarator directAbstractDeclarator);

    T visit(Designation designation);

    T visit(Designator designator);

    T visit(BlockItem blockItem);

    T visit(Statement statement);

    T visit(ExpressionStatement expressionStatement);

    T visit(IterationStatement iterationStatement);

    T visit(SelectionStatement selectionStatement);

    T visit(JumpStatement jumpStatement);

    T visit(ForCondition forCondition);

    T visit(ForDeclaration forDeclaration);

    T visit(ForExpression forExpression);

    T visit(TypeName typeName);

    T visit(SpecifierQualifierList specifierQualifierList);

    T visit(CastExpression castExpression);

    T visit(AssignmentOperator assignmentOperator);
    T visit(UnaryOperator unaryOperator);

    T visit(ArgumentExpressionList argumentExpressionList);
}
