package main.ast.nodes;

import main.ast.nodes.externaldeclaration.ExternalDeclaration;
import main.symbolTable.SymbolTable;
import main.visitor.IVisitor;

import java.util.ArrayList;

public class TranslationUnit extends Node{
    private SymbolTable symbolTable;
    private ArrayList<ExternalDeclaration> externalDeclarations = new ArrayList<>();

    public TranslationUnit() {}

    public void addExternalDeclaration(ExternalDeclaration externalDeclaration) {
        this.externalDeclarations.add(externalDeclaration);
    }

    public ArrayList<ExternalDeclaration> getExternalDeclarations() {
        return externalDeclarations;
    }

    public void setExternalDeclarations(ArrayList<ExternalDeclaration> externalDeclarations) {
        this.externalDeclarations = externalDeclarations;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }
}
