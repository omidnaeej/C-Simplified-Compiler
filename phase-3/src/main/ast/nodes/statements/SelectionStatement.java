package main.ast.nodes.statements;

import main.ast.nodes.Node;
import main.ast.nodes.expression.Expression;
import main.visitor.IVisitor;

public class SelectionStatement extends Node {
    private Expression expression;
    private Statement if_statement;
    private Statement else_statement;
    private int else_line;

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Statement getIf_statement() {
        return if_statement;
    }

    public void setIf_statement(Statement if_statement) {
        this.if_statement = if_statement;
    }

    public Statement getElse_statement() {
        return else_statement;
    }

    public void setElse_statement(Statement else_statement) {
        this.else_statement = else_statement;
    }

    public int getElse_line() {
        return else_line;
    }

    public void setElse_line(int else_line) {
        this.else_line = else_line;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
