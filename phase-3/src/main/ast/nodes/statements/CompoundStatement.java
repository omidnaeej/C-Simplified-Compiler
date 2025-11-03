package main.ast.nodes.statements;

import main.ast.nodes.BlockItem;
import main.ast.nodes.Node;
import main.symbolTable.SymbolTable;
import main.visitor.IVisitor;

import java.util.ArrayList;

public class CompoundStatement extends Node {
    private SymbolTable symbolTable;

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    private ArrayList<BlockItem> blockItems = new ArrayList<>();

    public void addBlockItem(BlockItem blockItem){
        this.blockItems.add(blockItem);
    }

    public void setBlockItems(ArrayList<BlockItem> blockItems) {
        this.blockItems = blockItems;
    }

    public ArrayList<BlockItem> getBlockItems() {
        return blockItems;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
