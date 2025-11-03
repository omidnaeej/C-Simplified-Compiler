package main.symbolTable.item;

public class VarDecSymbolTableItem extends SymbolTableItem{
    public static final String START_KEY = "VarDec_";

    private String id;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public VarDecSymbolTableItem(String id, String type) {
        this.id = id;
        this.type= type;
    }

    @Override
    public String getKey() {
        return START_KEY + id;
    }

}
