package interpreter;

public class Variable {
    private final String stringValue;
    private final double doubleValue;
    private final boolean type;
    Variable(String stringValue){
        this.stringValue=stringValue;
        doubleValue=0.0;
        type = true;
    }
    Variable(Double doubleValue){
        this.doubleValue=doubleValue;
        stringValue=null;
        type = false;
    }

    public String getStringValue() {
        return stringValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public boolean isType() {
        return type;
    }
}
