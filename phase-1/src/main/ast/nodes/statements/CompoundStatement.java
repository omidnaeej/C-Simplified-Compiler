package main.ast.nodes.statements;

import main.ast.nodes.BlockItem;
import main.ast.nodes.Node;
import main.visitor.IVisitor;

import java.util.ArrayList;

public class CompoundStatement extends Node {
    private ArrayList<BlockItem> blockItems = new ArrayList<>();

    public void addBlockItem(BlockItem blockItem){
        this.blockItems.add(blockItem);
    }

    public ArrayList<BlockItem> getBlockItems() {
        return blockItems;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
