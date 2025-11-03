package main.ast.nodes;

import main.visitor.IVisitor;

import java.util.ArrayList;

public class IdentifierList extends Node{
    public IdentifierList() {}
    private ArrayList<String> identifiers = new ArrayList<>();

    public void addIdentifiers(String id){
        this.identifiers.add(id);
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
