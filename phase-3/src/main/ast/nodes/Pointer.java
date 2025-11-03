package main.ast.nodes;

import main.visitor.IVisitor;

import java.util.ArrayList;
import java.util.List;

public class Pointer extends Node{
    private int level;
    private List<Boolean> constQualifiers;

    public Pointer() {
        this.level = 0;
        this.constQualifiers = new ArrayList<>();
    }

    public void addPointerLevel(boolean isConst) {
        this.level++;
        this.constQualifiers.add(isConst);
    }

    public int getLevel() { return level; }
    public List<Boolean> getConstQualifiers() { return constQualifiers; }
    public boolean isConstAtLevel(int level) {
        return level < constQualifiers.size() && constQualifiers.get(level);
    }
    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}

