package main.ast.nodes;

import main.ast.nodes.declarations.AbstractDeclarator;
import main.visitor.IVisitor;

public class TypeName extends Node{
    public TypeName() {}
    private AbstractDeclarator abstractDeclarator;
    private SpecifierQualifierList specifierQualifierList;

    public AbstractDeclarator getAbstractDeclarator() {
        return abstractDeclarator;
    }

    public void setAbstractDeclarator(AbstractDeclarator abstractDeclarator) {
        this.abstractDeclarator = abstractDeclarator;
    }

    public SpecifierQualifierList getSpecifierQualifierList() {
        return specifierQualifierList;
    }

    public void setSpecifierQualifierList(SpecifierQualifierList specifierQualifierList) {
        this.specifierQualifierList = specifierQualifierList;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
