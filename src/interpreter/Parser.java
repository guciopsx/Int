package interpreter;

import java.util.ArrayList;

public class Parser extends Exception{
    private int wskaznik=0;
    Controller controller;
    ArrayList<Token> tokens;
    String stop;
    Token token;

    Parser(ArrayList<Token> tokens, Controller controller){
        this.controller = controller;
        this.tokens = tokens;
    }

    Parser(ArrayList<Token> tokens, Controller controller, String stop){
        this.controller = controller;
        this.tokens = tokens;
        this.stop = stop;
    }

    void error() throws SyntaxError{
        throw new SyntaxError("Błąd składni");
    }

    void eat(String tokenType){
        if (tokens.get(wskaznik).getNazwa().equals(tokenType)) {
            wskaznik++;
            if (wskaznik!=tokens.size())token = tokens.get(wskaznik);
        }
                else {
            System.out.println("blad");
            try {
                error();
            }
            catch (Exception e){
                System.out.println("blad");
            }
        }
    }

    int wyraz(){
        int wynik =  wspolczynnik();

        while (token.getNazwa().equals("*") || token.getNazwa().equals("/")){
            if (token.getNazwa().equals("*")){
                eat("*");
                wynik = wynik * wyraz();
            }
            else if (token.getNazwa().equals("/")){
                eat("/");
                wynik = wynik / wyraz();
            }
        }
        return wynik;
    }

    int wspolczynnik(){
        Token token = tokens.get(wskaznik);
        if(token.getNazwa().equals("NUMBER")){
            eat(token.getNazwa());
            return Integer.valueOf(token.getWartosc());
        }else if(token.getNazwa().equals("LPARE")){
            eat(token.getNazwa());
            int wynik = expr();
            eat("RPARE");
            return wynik;
        } return 0;
    }

    int expr(){
        while (!tokens.get(wskaznik).getNazwa().equals("NUMBER"))wskaznik++;
        token = tokens.get(wskaznik);
        int wynik = wyraz();
        while (token.getNazwa().equals("+") || token.getNazwa().equals("-")){
            if (token.getNazwa().equals("+")){
                eat("+");
                wynik = wynik + wyraz();
            }
            else if (token.getNazwa().equals("-")){
                eat("-");
                wynik = wynik - wyraz();
            }
        }
        return wynik;
    }
}
