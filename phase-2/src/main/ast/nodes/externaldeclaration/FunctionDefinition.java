package main.ast.nodes.externaldeclaration;

import main.ast.nodes.Node;
import main.ast.nodes.declarations.DeclarationSpecifiers;
import main.ast.nodes.declarations.*;
import main.ast.nodes.statements.*;
import main.symbolTable.SymbolTable;
import main.visitor.IVisitor;

public class FunctionDefinition extends ExternalDeclaration {
//    private SymbolTable symbolTable;
//
//    public SymbolTable getSymbolTable() {
//        return symbolTable;
//    }
//
//    public void setSymbolTable(SymbolTable symbol_table) {
//        this.symbolTable = symbol_table;
//    }

    public FunctionDefinition() {}
    private DeclarationSpecifiers declarationSpecifiers;
    private Declarator declarator;
    private DeclarationList declarationList;
    private CompoundStatement compoundStatement;

    public Declarator getDeclarator() {
        return declarator;
    }

    public void setDeclarator(Declarator declarator) {
        this.declarator = declarator;
    }

    public DeclarationList getDeclarationList() {
        return declarationList;
    }

    public void setDeclarationList(DeclarationList declarationList) {
        this.declarationList = declarationList;
    }

    public CompoundStatement getCompoundStatement() {
        return compoundStatement;
    }

    public void setCompoundStatement(CompoundStatement compoundStatement) {
        this.compoundStatement = compoundStatement;
    }

    public DeclarationSpecifiers getDeclarationSpecifiers() {
        return declarationSpecifiers;
    }

    public void setDeclarationSpecifiers(DeclarationSpecifiers declarationSpecifiers) {
        this.declarationSpecifiers = declarationSpecifiers;
    }


    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
