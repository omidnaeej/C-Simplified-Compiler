package main.ast.nodes.expression;

import main.ast.nodes.InitializerList;
import main.ast.nodes.Node;
import main.ast.nodes.TypeName;
import main.visitor.IVisitor;

import java.util.ArrayList;

public class Expression extends Node {
    private String expressionType;
    private String identifier;
    private String constant;
    private ArrayList<String> stringLiterals = new ArrayList<>();
    private ArrayList<String> prefixOperators = new ArrayList<>();

    private String operatorType;

    private ArrayList<Expression> expressions = new ArrayList<>();

    private TypeName typeName;
    private InitializerList initializerList;

    private ArgumentExpressionList argumentExpressionList;

    private UnaryOperator unaryOperator;
    private CastExpression castExpression;

    private AssignmentOperator assignmentOperator;

    public String getExpressionType() {
        return expressionType;
    }

    public void setExpressionType(String expressionType) {
        this.expressionType = expressionType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
    }

    public void addStringLiteral(String s) {
        this.stringLiterals.add(s);
    }
    public ArrayList<String> getStringLiterals() {
        return stringLiterals;
    }

    public void addprefixOperator(String s) {
        this.prefixOperators.add(s);
    }

    public ArrayList<String> getPrefixOperators() {
        return prefixOperators;
    }

    public String getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
    }

    public ArrayList<Expression> getExpressions() {
        return expressions;
    }

    public void addExpressions(Expression expressions) {
        this.expressions.add(expressions);
    }

    public TypeName getTypeName() {
        return typeName;
    }

    public void setTypeName(TypeName typeName) {
        this.typeName = typeName;
    }

    public InitializerList getInitializerList() {
        return initializerList;
    }

    public void setInitializerList(InitializerList initializerList) {
        this.initializerList = initializerList;
    }

    public ArgumentExpressionList getArgumentExpressionList() {
        return argumentExpressionList;
    }

    public void setArgumentExpressionList(ArgumentExpressionList argumentExpressionList) {
        this.argumentExpressionList = argumentExpressionList;
    }

    public UnaryOperator getUnaryOperator() {
        return unaryOperator;
    }

    public void setUnaryOperator(UnaryOperator unaryOperator) {
        this.unaryOperator = unaryOperator;
    }

    public CastExpression getCastExpression() {
        return castExpression;
    }

    public void setCastExpression(CastExpression castExpression) {
        this.castExpression = castExpression;
    }

    public AssignmentOperator getAssignmentOperator() {
        return assignmentOperator;
    }

    public void setAssignmentOperator(AssignmentOperator assignmentOperator) {
        this.assignmentOperator = assignmentOperator;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
