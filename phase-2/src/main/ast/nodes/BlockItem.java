
package main.ast.nodes;

import main.ast.nodes.expression.Expression;
import main.ast.nodes.externaldeclaration.Declaration;
import main.ast.nodes.statements.Statement;
import main.visitor.IVisitor;

public class BlockItem extends Node{
    public BlockItem() {}
    private Declaration declaration;
    private Statement statement;

    public Declaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(Declaration declaration) {
        this.declaration = declaration;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
