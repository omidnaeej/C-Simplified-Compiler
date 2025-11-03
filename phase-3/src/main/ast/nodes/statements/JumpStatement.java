package main.ast.nodes.statements;

import main.ast.nodes.Node;
import main.ast.nodes.expression.Expression;
import main.visitor.IVisitor;

public class JumpStatement extends Node {
    private Expression expression;
    private String jumpType;

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public String getJumpType() {
        return jumpType;
    }

    public void setJumpType(String jumpType) {
        this.jumpType = jumpType;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
