package main.ast.nodes.statements;

import main.ast.nodes.BlockItem;
import main.ast.nodes.Node;
import main.visitor.IVisitor;

import java.util.ArrayList;

public class Statement extends Node {
    private String stmtType;
    private CompoundStatement compoundStatement;
    private ExpressionStatement expressionStatement;
    private SelectionStatement selectionStatement;
    private IterationStatement iterationStatement;
    private JumpStatement jumpStatement;

    public String getStmtType() {
        return stmtType;
    }

    public void setStmtType(String stmtType) {
        this.stmtType = stmtType;
    }

    public CompoundStatement getCompoundStatement() {
        return compoundStatement;
    }

    public void setCompoundStatement(CompoundStatement compoundStatement) {
        this.compoundStatement = compoundStatement;
    }

    public ExpressionStatement getExpressionStatement() {
        return expressionStatement;
    }

    public void setExpressionStatement(ExpressionStatement expressionStatement) {
        this.expressionStatement = expressionStatement;
    }

    public SelectionStatement getSelectionStatement() {
        return selectionStatement;
    }

    public void setSelectionStatement(SelectionStatement selectionStatement) {
        this.selectionStatement = selectionStatement;
    }

    public IterationStatement getIterationStatement() {
        return iterationStatement;
    }

    public void setIterationStatement(IterationStatement iterationStatement) {
        this.iterationStatement = iterationStatement;
    }

    public JumpStatement getJumpStatement() {
        return jumpStatement;
    }

    public void setJumpStatement(JumpStatement jumpStatement) {
        this.jumpStatement = jumpStatement;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
