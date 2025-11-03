package main.ast.nodes;

import main.ast.nodes.externaldeclaration.ExternalDeclaration;
import main.visitor.IVisitor;

import java.util.ArrayList;

public class TranslationUnit extends Node{
    public TranslationUnit() {}

    private ArrayList<ExternalDeclaration> externalDeclarations = new ArrayList<>();

    public void addExternalDeclaration(ExternalDeclaration externalDeclaration) {
        this.externalDeclarations.add(externalDeclaration);
    }

    public ArrayList<ExternalDeclaration> getExternalDeclarations() {
        return externalDeclarations;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
