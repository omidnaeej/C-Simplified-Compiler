package main.symbolTable.item;

import main.ast.nodes.declarations.DeclarationSpecifier;
import main.ast.nodes.externaldeclaration.FunctionDefinition;
import main.ast.nodes.ParameterDeclaration;

import java.util.ArrayList;
import java.util.List;

public class FuncDefSymbolTableItem extends SymbolTableItem {
    public static final String START_KEY = "FuncDec_";
    private String name;
    private String returnType;

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private final FunctionDefinition functionDefinition;
    private final List<ParameterDeclaration> parameterDeclarations;

    public FuncDefSymbolTableItem(FunctionDefinition functionDefinition) {
        this.functionDefinition = functionDefinition;

        this.setName(functionDefinition.getDeclarator().getDirectDeclarator().getNestedDirectDeclarator().getIdentifier());

        StringBuilder declaredReturn = new StringBuilder();
        List<DeclarationSpecifier> declaredSpcs = functionDefinition.getDeclarationSpecifiers().getDecSpecifiers();
        for (int i = 0; i < declaredSpcs.size(); i++) {
            declaredReturn.append(declaredSpcs.get(i)
                    .getTypeSpecifier()
                    .getType());
            if (i < declaredSpcs.size() - 1) {
                declaredReturn.append(' ');
            }
        }
        this.setReturnType(declaredReturn.toString());

        // Get the list of parameters (null if none)
        if (functionDefinition.getDeclarator()
                .getDirectDeclarator()
                .getParameterList() != null) {
            this.parameterDeclarations = functionDefinition.getDeclarator()
                    .getDirectDeclarator()
                    .getParameterList()
                    .getParameterDeclarations();
        } else {
            this.parameterDeclarations = null;
        }
    }

    public FunctionDefinition getFunctionDefinition() {
        return functionDefinition;
    }

    public List<ParameterDeclaration> getParameterDeclarations() {
        return parameterDeclarations;
    }

    public ArrayList<String> getParameterTypes() {
        ArrayList<String> ans = new ArrayList<>();

        if (parameterDeclarations != null) {
            for (ParameterDeclaration param : parameterDeclarations) {
                DeclarationSpecifier declarationSpecifier = param.getDeclarationSpecifiers().getDecSpecifiers().get(0);
                ans.add(declarationSpecifier.getTypeSpecifier().getType());
            }
        }
        return ans;
    }

    @Override
    public String getKey() {
        // e.g. "FuncDec_myFunc_float_int"
        StringBuilder keyBuilder = new StringBuilder(START_KEY);

        // Get the function name
        String funcName = functionDefinition.getDeclarator()
                .getDirectDeclarator()
                .getNestedDirectDeclarator()
                .getIdentifier();
        keyBuilder.append(funcName);

        // Append the types of parameters
        if (parameterDeclarations != null) {
            for (ParameterDeclaration param : parameterDeclarations) {
                DeclarationSpecifier declarationSpecifier = param.getDeclarationSpecifiers().getDecSpecifiers().get(0);
                keyBuilder.append("_").append(declarationSpecifier.getTypeSpecifier().getType());
            }
        }

        return keyBuilder.toString();
    }

    public boolean matches(String name, int argCount) {
        String declaredName = functionDefinition.getDeclarator()
                .getDirectDeclarator()
                .getNestedDirectDeclarator()
                .getIdentifier();
        return declaredName.equals(name) && parameterDeclarations.size() == argCount;
    }
}
