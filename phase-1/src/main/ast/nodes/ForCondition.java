package main.ast.nodes;

import main.ast.nodes.expression.Expression;
import main.visitor.IVisitor;

public class ForCondition extends Node{
    private ForDeclaration forDeclaration;
    private Expression expression;
    private ForExpression forExpression;
    private ForExpression forExpression2;

    public ForDeclaration getForDeclaration() {
        return forDeclaration;
    }

    public void setForDeclaration(ForDeclaration forDeclaration) {
        this.forDeclaration = forDeclaration;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public ForExpression getForExpression() {
        return forExpression;
    }

    public void setForExpression(ForExpression forExpression) {
        this.forExpression = forExpression;
    }

    public ForExpression getForExpression2() {
        return forExpression2;
    }

    public void setForExpression2(ForExpression forExpression2) {
        this.forExpression2 = forExpression2;
    }


    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}

