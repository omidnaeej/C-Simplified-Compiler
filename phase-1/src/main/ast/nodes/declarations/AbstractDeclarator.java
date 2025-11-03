package main.ast.nodes.declarations;

import main.ast.nodes.Node;
import main.ast.nodes.Pointer;
import main.visitor.IVisitor;

import java.util.ArrayList;

public class AbstractDeclarator extends Node {
    private ArrayList<Pointer> pointers = new ArrayList<>();
    private DirectAbstractDeclarator directAbstractDeclarator;

    public void addPointers(Pointer pointer) {
        this.pointers.add(pointer);
    }

    public DirectAbstractDeclarator getDirectAbstractDeclarator() {
        return directAbstractDeclarator;
    }

    public void setDirectAbstractDeclarator(DirectAbstractDeclarator directAbstractDeclarator) {
        this.directAbstractDeclarator = directAbstractDeclarator;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
