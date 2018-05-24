package interpreter;

import java.util.ArrayList;

public class Lexer extends Exception{
    private ArrayList<Token> tokeny = new ArrayList<>();
    private int wskaznik;

    Lexer(String input) throws NieznanySymbol{
        //input = input.replaceAll("\\s+","").trim();
        while(wskaznik<input.length()){
            if(input.charAt(wskaznik)=='"'){
                wskaznik++;
                tokeny.add(new Token("STRING", scanString(input)));
            }else if(Character.isWhitespace(input.charAt(wskaznik))){
                wskaznik++;
            }else if(input.charAt(wskaznik)=='+' || input.charAt(wskaznik)=='-' || input.charAt(wskaznik)=='*' || input.charAt(wskaznik)=='/'){
                tokeny.add(new Token("OPERATOR", String.valueOf(input.charAt(wskaznik))));
                wskaznik++;
            }else if(Character.isDigit(input.charAt(wskaznik))){
                tokeny.add(new Token("LICZBA", scanNumber(input)));
            }else if(Character.isAlphabetic(input.charAt(wskaznik))){
                tokeny.add(new Token("SYMBOL", scanSymbol(input)));
            }else if(input.charAt(wskaznik)=='=' || input.charAt(wskaznik)=='(' || input.charAt(wskaznik)==')' || input.charAt(wskaznik)==';'
                    || input.charAt(wskaznik)=='{' || input.charAt(wskaznik)=='}' || input.charAt(wskaznik)=='$' || input.charAt(wskaznik)=='!'
                    || input.charAt(wskaznik)==':'){
                tokeny.add(new Token(String.valueOf(input.charAt(wskaznik)), null));
                wskaznik++;
            }else {
                //else throw new NieznanySymbol("Nieznany symbol");
                tokeny.add(new Token(String.valueOf(input.charAt(wskaznik)), null));
                wskaznik++;
            }
        }
    }

    private String scanString(String input){
        String text = "";
        while(wskaznik<input.length() && input.charAt(wskaznik)!='"'){
            text+=input.charAt(wskaznik);
            wskaznik++;
        }
        wskaznik++;
        return text;
    }

    private String scanSymbol(String input){
        String text = "";
        while(wskaznik<input.length() && !Character.isWhitespace(input.charAt(wskaznik)) && input.charAt(wskaznik)!='='
                && input.charAt(wskaznik)!='(' && input.charAt(wskaznik)!=')' && input.charAt(wskaznik)!=';'
                && input.charAt(wskaznik)!='{' && input.charAt(wskaznik)!='}' && input.charAt(wskaznik)!='$'
                && input.charAt(wskaznik)!='!' && input.charAt(wskaznik)!='+' && input.charAt(wskaznik)!='-'
                && input.charAt(wskaznik)!='*' && input.charAt(wskaznik)!='/' && input.charAt(wskaznik)!='<'
                && input.charAt(wskaznik)!='>' && input.charAt(wskaznik)!=':') {
            text+=input.charAt(wskaznik);
            wskaznik++;
        }
        return text;
    }

    private String scanNumber(String input){
        String text = "";
        while(wskaznik<input.length() && (Character.isDigit(input.charAt(wskaznik)) || input.charAt(wskaznik)=='.') ){
            text+=input.charAt(wskaznik);
            wskaznik++;
        }
        return text;
    }

    public ArrayList<Token> getTokeny() {
        return tokeny;
    }

}
