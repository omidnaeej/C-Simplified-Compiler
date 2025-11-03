package main.ast.nodes;

import main.ast.nodes.declarations.DeclarationSpecifiers;
import main.ast.nodes.declarations.InitDeclaratorList;
import main.ast.nodes.expression.Expression;
import main.visitor.IVisitor;

public class ForDeclaration extends Node{
    private DeclarationSpecifiers declarationSpecifiers;
    private InitDeclaratorList initDeclaratorList;

    public DeclarationSpecifiers getDeclarationSpecifiers() {
        return declarationSpecifiers;
    }

    public void setDeclarationSpecifiers(DeclarationSpecifiers declarationSpecifiers) {
        this.declarationSpecifiers = declarationSpecifiers;
    }

    public InitDeclaratorList getInitDeclaratorList() {
        return initDeclaratorList;
    }

    public void setInitDeclaratorList(InitDeclaratorList initDeclaratorList) {
        this.initDeclaratorList = initDeclaratorList;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}

