package interpreter;

import java.util.ArrayList;
import java.util.List;

public final class Symbols {
    private List<String> keywords = new ArrayList<String>() {{
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

    private List<String> operators = new ArrayList<String>() {{
        add("PLUS");
        add("MINUS");
        add("DIV");
        add("MUL");
    }};

    private List<String> special = new ArrayList<String>() {{
        add("DOLAR");
        add("EQUAL");
        add("LPARE");
        add("RPARE");
        add("LBRAC");
        add("RBRAC");
        add("SEMICOLON");
        add("PUNCTUATION");
        add("EXCLAMATION");
    }};

}
