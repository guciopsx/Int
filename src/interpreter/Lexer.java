package interpreter;

import java.util.ArrayList;

public class Lexer extends Exception{
    private ArrayList<Token> tokeny = new ArrayList<>();

    Lexer(String input){
        for(int i=0; i<input.length();i++){
            tokeny.add(new Token(String.valueOf(input.charAt(i)),null));
        }
    }

    public ArrayList<Token> getTokeny() {
        return tokeny;
    }

}
