package main.ast.nodes;

import main.ast.nodes.declarations.AbstractDeclarator;
import main.ast.nodes.declarations.DeclarationSpecifiers;
import main.ast.nodes.declarations.Declarator;
import main.visitor.IVisitor;

public class ParameterDeclaration extends Node{
    public ParameterDeclaration() {}
    private DeclarationSpecifiers declarationSpecifiers;
    private Declarator declarator;
    private AbstractDeclarator abstractDeclarator;

    public DeclarationSpecifiers getDeclarationSpecifiers() {
        return declarationSpecifiers;
    }

    public void setDeclarationSpecifiers(DeclarationSpecifiers declarationSpecifiers) {
        this.declarationSpecifiers = declarationSpecifiers;
    }

    public Declarator getDeclarator() {
        return declarator;
    }

    public void setDeclarator(Declarator declarator) {
        this.declarator = declarator;
    }

    public AbstractDeclarator getAbstractDeclarator() {
        return abstractDeclarator;
    }

    public void setAbstractDeclarator(AbstractDeclarator abstractDeclarator) {
        this.abstractDeclarator = abstractDeclarator;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
