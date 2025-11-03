package main.ast.nodes.declarations;

import main.ast.nodes.Node;
import main.ast.nodes.externaldeclaration.Declaration;
import main.visitor.IVisitor;

import java.util.ArrayList;

public class DeclarationList extends Node {
    private ArrayList<Declaration> declarations = new ArrayList<>();

    public void addDeclaration(Declaration declaration) {
        this.declarations.add(declaration);
    }

    public ArrayList<Declaration> getDeclarations() {
        return declarations;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
