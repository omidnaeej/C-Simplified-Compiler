package main.ast.nodes;

import main.ast.nodes.expression.Expression;
import main.visitor.IVisitor;

import java.util.ArrayList;
import java.util.List;

public class Initializer extends Node{
    private Expression expression;
    private InitializerList initializerList;

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public InitializerList getInitializerList() {
        return initializerList;
    }

    public void setInitializerList(InitializerList initializerList) {
        this.initializerList = initializerList;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}

