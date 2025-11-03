package main.ast.nodes.declarations;

import main.ast.nodes.IdentifierList;
import main.ast.nodes.Node;
import main.ast.nodes.ParameterList;
import main.ast.nodes.expression.Expression;
import main.visitor.IVisitor;

public class DirectDeclarator extends Node {
    private String identifier;

    private Declarator declarator;

    private DirectDeclarator nestedDirectDeclarator;
    private Expression expression;

    private ParameterList parameterList;
    private IdentifierList identifierList;


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Declarator getDeclarator() {
        return declarator;
    }

    public void setDeclarator(Declarator declarator) {
        this.declarator = declarator;
    }

    public DirectDeclarator getNestedDirectDeclarator() {
        return nestedDirectDeclarator;
    }

    public void setNestedDirectDeclarator(DirectDeclarator nestedDirectDeclarator) {
        this.nestedDirectDeclarator = nestedDirectDeclarator;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public ParameterList getParameterList() {
        return parameterList;
    }

    public void setParameterList(ParameterList parameterList) {
        this.parameterList = parameterList;
    }

    public IdentifierList getIdentifierList() {
        return identifierList;
    }

    public void setIdentifierList(IdentifierList identifierList) {
        this.identifierList = identifierList;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
