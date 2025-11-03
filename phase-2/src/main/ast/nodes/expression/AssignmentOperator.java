package main.ast.nodes.expression;

import main.ast.nodes.Node;
import main.visitor.IVisitor;

public class AssignmentOperator extends Node {
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
