
package main.ast.nodes;

import main.ast.nodes.expression.Expression;
import main.visitor.IVisitor;

public class Designator extends Node{
    public Designator() {}
    private Expression expression;
    private String identifier;

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
