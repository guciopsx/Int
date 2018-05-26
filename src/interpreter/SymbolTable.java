package interpreter;

import java.util.Hashtable;

public class SymbolTable {
    private Hashtable<String, String> table;
    public SymbolTable(){
        table = new Hashtable<String, String>();
    }

    public void add(String key, String value){
        table.put(key, value);
    }

    public String look(String key){
        if(table.containsKey(key))return table.get(key);
        else return null;
    }
}
