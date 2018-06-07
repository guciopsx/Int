package interpreter;

import java.util.HashMap;

public class Variables {
    private final String name;
    private final double doubleValue;
    private final String stringValue;
    public Variables(String name, double doubleValue){
        this.name=name;
        this.doubleValue=doubleValue;
        stringValue=null;
    }

    public Variables(String name, String stringValue){
        this.name=name;
        this.stringValue=stringValue;
        doubleValue=0.0;
    }

    public String getName() {
        return name;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public String getStringValue() {
        return stringValue;
    }
}
