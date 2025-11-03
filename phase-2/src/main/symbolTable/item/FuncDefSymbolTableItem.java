package main.symbolTable.item;

import main.ast.nodes.externaldeclaration.FunctionDefinition;

public class FuncDefSymbolTableItem extends SymbolTableItem {
    public static final String START_KEY = "FuncDec_";

    private final FunctionDefinition functionDefinition;
    private final int paramCount;

    public FuncDefSymbolTableItem(FunctionDefinition functionDefinition) {
        this.functionDefinition = functionDefinition;
        // compute number of declared parameters (0 if none)
        if (functionDefinition.getDeclarator()
                .getDirectDeclarator()
                .getParameterList() != null) {
            this.paramCount = functionDefinition.getDeclarator()
                    .getDirectDeclarator()
                    .getParameterList()
                    .getParameterDeclarations()
                    .size();
        } else {
            this.paramCount = 0;
        }
    }

    public FunctionDefinition getFunctionDefinition() {
        return functionDefinition;
    }

    public int getParamCount() {
        return paramCount;
    }

    @Override
    public String getKey() {
        // e.g. "FuncDec_myFunc_2"
        String name = functionDefinition.getDeclarator()
                .getDirectDeclarator()
                .getNestedDirectDeclarator()
                .getIdentifier();
        return START_KEY + name + "_" + paramCount;
    }

    /**
     * Helper to see if this item matches a call of given name+arity.
     */
    public boolean matches(String name, int argCount) {
        String declaredName = functionDefinition.getDeclarator()
                .getDirectDeclarator()
                .getNestedDirectDeclarator()
                .getIdentifier();
        return declaredName.equals(name) && this.paramCount == argCount;
    }
}
