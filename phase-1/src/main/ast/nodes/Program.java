package main.ast.nodes;

import main.visitor.IVisitor;

public class Program extends Node{
    public Program() {}

    private TranslationUnit translationUnit;

    public TranslationUnit getTranslationUnit() {
        return translationUnit;
    }

    public void setTranslationUnit(TranslationUnit translationUnit) {
        this.translationUnit = translationUnit;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
