package main.symbolTable.item;

public class VarDecSymbolTableItem extends SymbolTableItem{
    public static final String START_KEY = "VarDec_";

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public VarDecSymbolTableItem(String id) {
        this.id = id;
    }

    @Override
    public String getKey() {
        return START_KEY + id;
    }

}
