package main.ast.nodes.declarations;

import main.ast.nodes.Node;
import main.visitor.IVisitor;
import main.ast.nodes.declarations.DeclarationSpecifier;
import java.util.ArrayList;

public class DeclarationSpecifiers extends Node {
    private ArrayList<DeclarationSpecifier> decSpecifiers = new ArrayList<>();

    public DeclarationSpecifiers() {}

    public ArrayList<DeclarationSpecifier> getDecSpecifiers() {
        return decSpecifiers;
    }

    public void addDeclarationSpecifier(DeclarationSpecifier declarationSpecifier) {
        this.decSpecifiers.add(declarationSpecifier);
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
