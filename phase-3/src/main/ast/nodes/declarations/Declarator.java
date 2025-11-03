package main.ast.nodes.declarations;

import main.ast.nodes.Node;
import main.ast.nodes.Pointer;
import main.visitor.IVisitor;

public class Declarator extends Node {
    private Pointer pointer;
    private DirectDeclarator directDeclarator;

    public Pointer getPointer() {
        return pointer;
    }

    public void setPointer(Pointer pointer) {
        this.pointer = pointer;
    }

    public DirectDeclarator getDirectDeclarator() {
        return directDeclarator;
    }

    public void setDirectDeclarator(DirectDeclarator directDeclarator) {
        this.directDeclarator = directDeclarator;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
