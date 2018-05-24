package interpreter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public final class Symbols {
    private final List<String> keywords = new ArrayList<String>() {{
        add("ABS");
        add("AND");
        add("ASC");
        add("CHR");
        add("CLR");
        add("COS");
        add("DEF");
        add("DIM");
        add("END");
        add("FN");
        add("FOR");
        add("GOSUB");
        add("GOTO");
        add("IF");
        add("INPUT");
        add("INT");
        add("LEFT");
        add("LEN");
        add("LET");
        add("LOG");
        add("MID");
        add("NEXT");
        add("NOT");
        add("ON");
        add("OR");
        add("PRINT");
        add("REM");
        add("RETURN");
        add("RIGHT");
        add("RND");
        add("SGN");
        add("SIN");
        add("SQR");
        add("STEP");
        add("STOP");
        add("STR");
        add("TAN");
        add("THEN");
        add("TO");
        add("VAL");
    }};

    private final Hashtable<Character, String> operators = new Hashtable<Character, String>(){{
        put('+',"PLUS");
        put('-',"MINUS");
        put('/',"DIV");
        put('*',"MUL");
    }};

    private final Hashtable<Character, String> special = new Hashtable<Character, String>(){{
        put('$',"DOLAR");
        put('=',"EQUAL");
        put('(',"LPARE");
        put(')',"RPARE");
        put('{',"LBRAC");
        put(']',"RBRAC");
        put('<',"LESS");
        put('>',"MORE");
        put(';',"SEMICOLON");
        put(':',"PUNCTUATION");
        put('!',"EXCLAMATION");
    }};

    public boolean isKeyword(String text){
        if(keywords.contains(text)) return true;
        return false;
    }

    public String operators(Character c){
        return operators.get(c);
    }

    public String special(Character c){
        return special.get(c);
    }

}
