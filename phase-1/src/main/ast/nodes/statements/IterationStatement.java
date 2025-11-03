package main.ast.nodes.statements;

import main.ast.nodes.ForCondition;
import main.ast.nodes.Node;
import main.ast.nodes.expression.Expression;
import main.visitor.IVisitor;

public class IterationStatement extends Node {
    private Expression expression;
    private Statement statement;
    private ForCondition forCondition;
    private String loopType;

    public String getLoopType() {
        return loopType;
    }

    public void setLoopType(String type) {
        if (type != null) {
            this.loopType = type;
        }
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public ForCondition getForCondition() {
        return forCondition;
    }

    public void setForCondition(ForCondition forCondition) {
        this.forCondition = forCondition;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
