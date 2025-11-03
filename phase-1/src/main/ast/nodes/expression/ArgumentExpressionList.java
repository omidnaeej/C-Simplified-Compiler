package main.ast.nodes.expression;

import main.ast.nodes.Node;
import main.visitor.IVisitor;

import java.util.ArrayList;

public class ArgumentExpressionList extends Node {
    private ArrayList<Expression> expressions = new ArrayList<>();

    public ArrayList<Expression> getExpressions() {
        return expressions;
    }

    public void addExpression(Expression expression){
        this.expressions.add(expression);
    }
    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
