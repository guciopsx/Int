package interpreter;

import java.util.ArrayList;
import java.util.Hashtable;

public class Parser extends Exception{
    private int wskaznik=0;
    private Controller controller;
    private ArrayList<Token> tokens;
    //ArrayList<Variables> variables;
    private Hashtable<String, Variable> var;
    private Token token;

    Parser(ArrayList<Token> tokens, Controller controller){
        this.controller = controller;
        this.tokens = tokens;
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
            Token token = tokens.get(wskaznik);
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
        int wynik = wyraz();
        while (token.getNazwa().equals("+") || token.getNazwa().equals("-")){
            token = tokens.get(wskaznik);
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

    /*private Token get(int offset){
        if (position + offset >= tokens.size()){
            return new Token("EOF","");
        } return tokens.get(position + offset);
    }

    private Token last(int offset) {
        return tokens.get(position - offset);
    }

    private Token consume(String name){
        if(!match(name)) throw new Error("Zły token " + name);
        return last(1);
    }

    private boolean match(String name){
        if(get(0).getNazwa().equals("SYMBOL")) return false;
        if(!get(0).getWartosc().equals(name))return false;
        position++;
        return true;
    }*/
    public Hashtable<String, Variable> getVar(){
        return var;
    }

    private void printVar(){
        controller.varTextArea.clear();
        for(String name: var.keySet()){
            if(var.get(name).isType()) controller.varTextArea.appendText("Zmienna: " + name.toString() + " Wartość: " + var.get(name).getStringValue()+"\n");
            else controller.varTextArea.appendText("Zmienna: " + name.toString() + " Wartość: " + var.get(name).getDoubleValue()+"\n");
        }
    }

    public void parse(){
        Hashtable<Integer, Integer> etykiety = new Hashtable<Integer, Integer>();
        //variables = new ArrayList<Variables>();
        var = new Hashtable<String, Variable>();
        int licznik=0;
        Token token;
        while(licznik<tokens.size()){
            token=tokens.get(licznik);
            if(token.getNazwa().equals("LINENUMBER")) {
                etykiety.put(Integer.valueOf(token.getWartosc()), licznik);
                licznik++;
                token=tokens.get(licznik);
                if (token.getNazwa().equals("KEYWORD")) {
                    if (token.getWartosc().equals("PRINT")) {
                        licznik++;
                        token = tokens.get(licznik);
                        if(token.getNazwa().equals("STRING")){
                            controller.konsolaTextArea.appendText(token.getWartosc() + "\n");
                            licznik++;
                        } else if(token.getNazwa().equals("TEXT")){
                            //System.out.println(variables.indexOf(token.getWartosc()));
                            //Variables zmienna = variables.get(variables.indexOf(token.getWartosc()));
                            if(var.get(token.getWartosc()).isType())controller.konsolaTextArea.appendText(var.get(token.getWartosc()).getStringValue()+"\n");
                            else controller.konsolaTextArea.appendText(String.valueOf(var.get(token.getWartosc()).getDoubleValue())+"\n");
                            licznik++;
                        }

                    } else if (token.getWartosc().equals("GOTO")) {
                        licznik++;
                        token = tokens.get(licznik);
                        licznik = etykiety.get(Integer.valueOf(token.getWartosc()));
                    } else if (token.getWartosc().equals("INPUT")) {
                        licznik++;
                        token = tokens.get(licznik);
                        if (token.getNazwa().equals("STRING")) {
                            controller.konsolaTextArea.appendText(token.getWartosc());
                            licznik++;
                        }
                        controller.konsolaTextArea.appendText("?");

                        controller.konsolaTextArea.setEditable(true);
                        String oldValue, newValue;
                        oldValue = controller.konsolaTextArea.getText();
                        newValue = controller.konsolaTextArea.getText().replace(oldValue, "");

                        while (newValue.equals("") || newValue.charAt(newValue.length() - 1) != '\n') {
                            newValue = controller.konsolaTextArea.getText().replace(oldValue, "");
                        }
                        controller.konsolaTextArea.setEditable(false);
                        System.out.println(newValue);
                        licznik++;
                    }
                } else if(token.getNazwa().equals("TEXT")){
                    String symbolName = token.getWartosc();
                    licznik++;
                    token=tokens.get(licznik);
                    if(token.getNazwa().equals("EQUAL")){
                        licznik++;
                        token=tokens.get(licznik);
                        if(token.getNazwa().equals("NUMBER")){
                            var.put(symbolName, new Variable(Double.parseDouble(token.getWartosc())));
                            printVar();

                            licznik++;
                        } else if(token.getNazwa().equals("STRING")){
                            //variables.add(new Variables(symbolName, token.getWartosc()));
                            var.put(symbolName, new Variable(token.getWartosc()));
                            printVar();
                            licznik++;
                        }
                    }

                }
            }
            else {
                //throw new Exception("Błędna linia");
            }
        }
    }
}
