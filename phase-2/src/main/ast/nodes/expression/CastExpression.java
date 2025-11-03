package main.ast.nodes.expression;

import main.ast.nodes.Node;
import main.ast.nodes.TypeName;
import main.visitor.IVisitor;

public class CastExpression extends Node {
    private TypeName typeName;
    private CastExpression castExpression;
    private Expression expression;
    private String digitSequence;

    public TypeName getTypeName() {
        return typeName;
    }

    public void setTypeName(TypeName typeName) {
        this.typeName = typeName;
    }

    public CastExpression getCastExpression() {
        return castExpression;
    }

    public void setCastExpression(CastExpression castExpression) {
        this.castExpression = castExpression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public String getDigitSequence() {
        return digitSequence;
    }

    public void setDigitSequence(String digitSequence) {
        this.digitSequence = digitSequence;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
