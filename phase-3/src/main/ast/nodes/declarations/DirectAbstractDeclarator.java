package main.ast.nodes.declarations;

import main.ast.nodes.IdentifierList;
import main.ast.nodes.Node;
import main.ast.nodes.ParameterList;
import main.ast.nodes.Pointer;
import main.ast.nodes.expression.Expression;
import main.visitor.IVisitor;

import java.util.ArrayList;

public class DirectAbstractDeclarator extends Node {
    private Expression expression;
    private AbstractDeclarator abstractDeclarator;
    private ParameterList parameterList;

    private DirectAbstractDeclarator directAbstractDeclarator;

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public AbstractDeclarator getAbstractDeclarator() {
        return abstractDeclarator;
    }

    public void setAbstractDeclarator(AbstractDeclarator abstractDeclarator) {
        this.abstractDeclarator = abstractDeclarator;
    }

    public ParameterList getParameterList() {
        return parameterList;
    }

    public void setParameterList(ParameterList parameterList) {
        this.parameterList = parameterList;
    }

    public DirectAbstractDeclarator getDirectAbstractDeclarator() {
        return directAbstractDeclarator;
    }

    public void setDirectAbstractDeclarator(DirectAbstractDeclarator directAbstractDeclarator) {
        this.directAbstractDeclarator = directAbstractDeclarator;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
