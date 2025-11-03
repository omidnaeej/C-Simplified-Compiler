package main.ast.nodes;

import main.ast.nodes.declarations.AbstractDeclarator;
import main.visitor.IVisitor;

public class SpecifierQualifierList extends Node{
    public SpecifierQualifierList() {}
    private TypeSpecifier typeSpecifier;
    private SpecifierQualifierList nestedSpecifierQualifierList;

    public TypeSpecifier getTypeSpecifier() {
        return typeSpecifier;
    }

    public void setTypeSpecifier(TypeSpecifier typeSpecifier) {
        this.typeSpecifier = typeSpecifier;
    }

    public SpecifierQualifierList getNestedSpecifierQualifierList() {
        return nestedSpecifierQualifierList;
    }

    public void setNestedSpecifierQualifierList(SpecifierQualifierList nestedSpecifierQualifierList) {
        this.nestedSpecifierQualifierList = nestedSpecifierQualifierList;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
