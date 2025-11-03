package main.ast.nodes.externaldeclaration;

import main.visitor.IVisitor;

public class EmptyDeclaration extends ExternalDeclaration{
    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return null;
    }
}
