package main.ast.nodes;

import main.ast.nodes.expression.Expression;
import main.visitor.IVisitor;

import java.util.ArrayList;

public class InitializerList extends Node{
    private ArrayList<Designation> designations = new ArrayList<>();
    private ArrayList<Initializer> initializers = new ArrayList<>();

    public ArrayList<Designation> getDesignations() {
        return designations;
    }

    public ArrayList<Initializer> getInitializers() {
        return initializers;
    }

    public void addDesignation(Designation designation) {
        this.designations.add(designation);
    }

    public void addInitializer(Initializer initializer) {
        this.initializers.add(initializer);
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}

