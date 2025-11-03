package main.ast.nodes;

import main.visitor.IVisitor;

import java.util.ArrayList;

public class ParameterList extends Node{
    public ParameterList() {}
    private ArrayList<ParameterDeclaration> parameterDeclarations = new ArrayList<>();

    public ArrayList<ParameterDeclaration> getParameterDeclarations() {
        return parameterDeclarations;
    }

    public void addParameterDeclaration(ParameterDeclaration parameterDeclaration) {
        this.parameterDeclarations.add(parameterDeclaration);
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
