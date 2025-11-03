package main.ast.nodes.declarations;

import main.ast.nodes.Initializer;
import main.ast.nodes.Node;
import main.visitor.IVisitor;

public class InitDeclarator extends Node {
    private Declarator declarator;
    private Initializer initializer;


    public Declarator getDeclarator() {
        return declarator;
    }

    public void setDeclarator(Declarator declarator) {
        this.declarator = declarator;
    }

    public Initializer getInitializer() {
        return initializer;
    }

    public void setInitializer(Initializer initializer) {
        this.initializer = initializer;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
