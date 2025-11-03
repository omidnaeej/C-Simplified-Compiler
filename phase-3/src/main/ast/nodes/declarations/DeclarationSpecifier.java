package main.ast.nodes.declarations;

import main.ast.nodes.Node;
import main.ast.nodes.TypeSpecifier;
import main.visitor.IVisitor;

import java.util.List;
import java.util.ArrayList;

public class DeclarationSpecifier extends Node {
    private String type;
    private TypeSpecifier typeSpecifier;

//    public DeclarationSpecifier() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TypeSpecifier getTypeSpecifier() {
        return typeSpecifier;
    }

//    public void setTypeSpecifier(TypeSpecifier typeSpecifier) {
//        this.typeSpecifier = typeSpecifier;
//    }

    public void setTypeSpecifier(TypeSpecifier typeSpecifier) {
        if (typeSpecifier == null) {
            throw new IllegalArgumentException("TypeSpecifier cannot be null");
        }
        this.typeSpecifier = typeSpecifier;
    }


//    private String storageClass; // "typedef", "extern", "static", etc.
//    private List<String> qualifiers; // "const", "volatile", etc.

//    public DeclarationSpecifier() {
//        this.qualifiers = new ArrayList<>();
//    }

//    public void setStorageClass(String storageClass) {
//        this.storageClass = storageClass;
//    }

//    public void addQualifier(String qualifier) {
//        this.qualifiers.add(qualifier);
//    }


    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
