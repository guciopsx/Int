package interpreter;

import java.util.ArrayList;

public class Lexer extends Exception{
    private ArrayList<Token> tokeny = new ArrayList<>();
    private int wskaznik;

    Lexer(String input){
        for(wskaznik=0; wskaznik<input.length(); wskaznik++){
            if(input.charAt(wskaznik)=='"'){
                wskaznik++;
                tokeny.add(new Token("String", scanString(input)));
            }
            tokeny.add(new Token(String.valueOf(input.charAt(wskaznik)),null));
        }
    }

    private String scanString(String input){
        String text = "";
        while(input.charAt(wskaznik)!='"' && wskaznik<input.length()){
            text+=input.charAt(wskaznik);
            wskaznik++;
        }
        wskaznik++;
        return text;
    }

    public ArrayList<Token> getTokeny() {
        return tokeny;
    }

}
