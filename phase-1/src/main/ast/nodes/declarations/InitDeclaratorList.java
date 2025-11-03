package main.ast.nodes.declarations;

import main.ast.nodes.Node;
import main.visitor.IVisitor;

import java.util.ArrayList;

public class InitDeclaratorList extends Node {
    private ArrayList<InitDeclarator> initDeclarators = new ArrayList<>();

    public ArrayList<InitDeclarator> getInitDeclarators() {
        return initDeclarators;
    }

    public void addInitDeclarator(InitDeclarator initDeclarator) {
        this.initDeclarators.add(initDeclarator);
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
