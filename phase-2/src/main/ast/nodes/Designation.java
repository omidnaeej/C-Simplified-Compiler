
package main.ast.nodes;

import main.ast.nodes.externaldeclaration.ExternalDeclaration;
import main.visitor.IVisitor;

import java.util.ArrayList;

public class Designation extends Node{
    public Designation() {}
    private ArrayList<Designator> designators = new ArrayList<>();

    public ArrayList<Designator> getDesignators() {
        return designators;
    }

    public void addDesignator(Designator designator){
        this.designators.add(designator);
    }


    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
