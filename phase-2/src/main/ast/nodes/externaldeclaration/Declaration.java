package main.ast.nodes.externaldeclaration;

import main.ast.nodes.declarations.*;
import main.visitor.IVisitor;

import java.util.ArrayList;

public class Declaration extends ExternalDeclaration{
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
