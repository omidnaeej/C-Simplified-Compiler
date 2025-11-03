package main.ast.nodes;

import main.visitor.IVisitor;

public class TypeSpecifier extends Node{
    private String type;
    private boolean identifier;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIdentifier() {
        return identifier;
    }

    public void setIdentifier(boolean identifier) {
        this.identifier = identifier;
    }

    public TypeSpecifier() {}


    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}

