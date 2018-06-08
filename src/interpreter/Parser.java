package interpreter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

public class Parser extends Exception{
    private int licznik=0;
    private Controller controller;
    private ArrayList<Token> tokens;
    private Hashtable<String, Variable> var;

    Parser(ArrayList<Token> tokens, Controller controller){
        this.controller = controller;
        this.tokens = tokens;
    }

    void error() throws SyntaxError{
        throw new SyntaxError("Błąd składni");
    }

    public String wyrazenie(){
        String onp = "";
        Stack<String> stos = new Stack<String>();
        int liczonp = licznik;
        boolean unary = true;

        Token token;
        token = tokens.get(liczonp);
        while(!token.getNazwa().equals("LINENUMBER") && !token.getNazwa().equals("COMMA") && !token.getNazwa().equals("SEMICOLON") && liczonp!=tokens.size() ){
            if (token.getNazwa().equals("NUMBER")) {
                onp += token.getWartosc() + ",";
                liczonp++;
                unary = false;
            } else if (token.getNazwa().equals("LPARE")) {
                unary = true;
                stos.push(token.getNazwa());
                liczonp++;
            } else if(token.getNazwa().equals("TEXT")) {
                onp += token.getWartosc() + ",";
                liczonp++;
                unary=false;
            } else if(unary==true && token.getNazwa().equals("-")){
                stos.push("neg");
                liczonp++;
            } else if(unary==false && (token.getNazwa().equals("+") ||
                    token.getNazwa().equals("-") ||
                    token.getNazwa().equals("*") ||
                    token.getNazwa().equals("/") ||
                    token.getNazwa().equals("^") )){
                if(token.getNazwa().equals("+") || token.getNazwa().equals("-") ){
                    while(!stos.isEmpty() && (stos.peek().equals("*") || stos.peek().equals("/") || stos.peek().equals("^") && stos.peek().length()>1)){
                        onp += stos.pop() + ",";
                    }
                    stos.push( String.valueOf(token.getNazwa()));
                    }else if(token.getNazwa().equals("*") || token.getNazwa().equals("/")){
                    while(!stos.isEmpty() && (stos.peek().equals("^") || stos.peek().length()>1)){
                        onp += stos.pop() + ",";
                    }
                    stos.push( String.valueOf(token.getNazwa()));
                    } else if(token.getNazwa().equals("^")){
                    while(!stos.isEmpty() && stos.peek().length()>1){
                        onp += stos.pop() + ",";
                    }
                    stos.push(String.valueOf(token.getNazwa()));
                }
                unary=true;
                liczonp++;
            }else if(token.getNazwa().equals("RPARE")) {
                while (!stos.peek().equals("LPARE")) {
                        onp += stos.pop() + ",";
                    }
                    stos.pop();

                liczonp++;
            }
            if(liczonp!=tokens.size())token = tokens.get(liczonp);
        }

        while (!stos.empty()) {
            onp += stos.pop() + ",";
        }
        if(token.getNazwa().equals("LINENUMBER") || token.getNazwa().equals("COMMA") || token.getNazwa().equals("SEMICOLON"))liczonp--;
        licznik=liczonp;

        return onp;
    }

    private double evaluateONP(String onp) {
        int wskaznikONP = 0;
        String[]ONP = onp.split(",");
        Stack<Double> wartosc = new Stack<Double>();

        while (wskaznikONP<ONP.length) {

            if (scanString(ONP[wskaznikONP]) == 1) {
                wartosc.push(Double.parseDouble(ONP[wskaznikONP]));
                wskaznikONP++;
            } else if (scanString(ONP[wskaznikONP]) == 2) {
                if (ONP[wskaznikONP].equals("sqrt")) {
                    double a = wartosc.pop();
                    wartosc.push(Math.sqrt(a));
                    wskaznikONP++;
                } else if (ONP[wskaznikONP].equals("neg")) {
                    double a = -wartosc.pop();
                    wartosc.push(a);
                    wskaznikONP++;
                } else if (ONP[wskaznikONP].equals("sin")) {
                    double a = -wartosc.pop();
                    wartosc.push(Math.sin(a));
                    wskaznikONP++;
                } else if (ONP[wskaznikONP].equals("cos")) {
                    double a = -wartosc.pop();
                    wartosc.push(Math.cos(a));
                    wskaznikONP++;
                } else if (ONP[wskaznikONP].equals("tg") || ONP[wskaznikONP].equals("tan")) {
                    double a = -wartosc.pop();
                    wartosc.push(Math.tan(a));
                    wskaznikONP++;
                } else if (ONP[wskaznikONP].equals("ctg") || ONP[wskaznikONP].equals("cot")) {
                    double a = -wartosc.pop();
                    wartosc.push(Math.cos(a) / Math.sin(a));
                    wskaznikONP++;
                } else if(var.containsKey(ONP[wskaznikONP])) {
                    wartosc.push(var.get(ONP[wskaznikONP]).getDoubleValue());
                    wskaznikONP++;
                }

            } else if (scanString(ONP[wskaznikONP]) == 3)

            {
                if (ONP[wskaznikONP].equals("+")) {
                    double a = wartosc.pop();
                    double b = wartosc.pop();
                    wartosc.push(b + a);
                    wskaznikONP++;
                } else if (ONP[wskaznikONP].equals("*")) {
                    double a = wartosc.pop();
                    double b = wartosc.pop();
                    wartosc.push(b * a);
                    wskaznikONP++;
                } else if (ONP[wskaznikONP].equals("-")) {
                    double a = wartosc.pop();
                    double b = wartosc.pop();
                    wartosc.push(b - a);
                    wskaznikONP++;
                } else if (ONP[wskaznikONP].equals("/")) {
                    double a = wartosc.pop();
                    double b = wartosc.pop();
                    wartosc.push(b / a);
                    wskaznikONP++;
                } else if (ONP[wskaznikONP].equals("^")) {
                    double a = wartosc.pop();
                    double b = wartosc.pop();
                    wartosc.push(Math.pow(b, a));
                    wskaznikONP++;
                }
            }
        }
        return wartosc.pop();
    }

    private int scanString(String input){
        if(Character.isDigit(input.charAt(0))){
            return 1;
        }else if(Character.isAlphabetic(input.charAt(0))) {
            return 2;
        } else return 3;
    }

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
        licznik=0;
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
                            controller.konsolaTextArea.appendText(token.getWartosc());
                            licznik++;
                        } else if(token.getNazwa().equals("TEXT")){
                            //System.out.println(variables.indexOf(token.getWartosc()));
                            //Variables zmienna = variables.get(variables.indexOf(token.getWartosc()));
                            String symbolName = token.getWartosc();

                            licznik++;
                            if(licznik!=tokens.size()){
                                token = tokens.get(licznik);
                                if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                        || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                    licznik--;
                                    token = tokens.get(licznik);

                                    controller.konsolaTextArea.appendText(String.valueOf(evaluateONP(wyrazenie())));
                                } else {
                                    licznik--;
                                    token = tokens.get(licznik);
                                    if(var.get(token.getWartosc()).isType())controller.konsolaTextArea.appendText(var.get(token.getWartosc()).getStringValue());
                                    else controller.konsolaTextArea.appendText(String.valueOf(var.get(token.getWartosc()).getDoubleValue()));
                                }
                            } else {
                                licznik--;
                                token = tokens.get(licznik);
                                if(var.get(token.getWartosc()).isType())controller.konsolaTextArea.appendText(var.get(token.getWartosc()).getStringValue());
                                else controller.konsolaTextArea.appendText(String.valueOf(var.get(token.getWartosc()).getDoubleValue()));
                            }
                            licznik++;
                            //if(var.get(token.getWartosc()).isType())controller.konsolaTextArea.appendText(var.get(token.getWartosc()).getStringValue());
                            //else controller.konsolaTextArea.appendText(String.valueOf(var.get(token.getWartosc()).getDoubleValue()));
                            //licznik++;
                        } else if (token.getNazwa().equals("NUMBER")){
                            licznik++;
                            if(licznik!=tokens.size()){
                                token = tokens.get(licznik);
                                if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                        || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                    licznik--;
                                    token = tokens.get(licznik);

                                    controller.konsolaTextArea.appendText(String.valueOf(evaluateONP(wyrazenie())));
                                } else {
                                    licznik--;
                                    token = tokens.get(licznik);
                                    controller.konsolaTextArea.appendText(token.getWartosc());
                                }
                            } else {
                                licznik--;
                                token = tokens.get(licznik);
                                controller.konsolaTextArea.appendText(token.getWartosc());
                            }
                            licznik++;
                            //controller.konsolaTextArea.appendText(token.getWartosc());
                            //licznik++;
                        }
                        boolean morePrint = true;
                            while (morePrint){
                                token = tokens.get(licznik);
                                if(token.getNazwa().equals("COMMA")){
                                    licznik++;
                                    token = tokens.get(licznik);
                                    if(token.getNazwa().equals("STRING")){
                                        controller.konsolaTextArea.appendText("\t" + token.getWartosc());
                                        licznik++;
                                    } else if(token.getNazwa().equals("TEXT")){
                                        //System.out.println(variables.indexOf(token.getWartosc()));
                                        //Variables zmienna = variables.get(variables.indexOf(token.getWartosc()));
                                        if(var.get(token.getWartosc()).isType())controller.konsolaTextArea.appendText("\t" + var.get(token.getWartosc()).getStringValue());
                                        else controller.konsolaTextArea.appendText(String.valueOf("\t" + var.get(token.getWartosc()).getDoubleValue()));
                                        licznik++;
                                    } else if (token.getNazwa().equals("NUMBER")){
                                        controller.konsolaTextArea.appendText("\t" + token.getWartosc());
                                        licznik++;
                                    }
                                }else if(token.getNazwa().equals("SEMICOLON")){
                                    licznik++;
                                    token = tokens.get(licznik);
                                    if(token.getNazwa().equals("STRING")){
                                        controller.konsolaTextArea.appendText(token.getWartosc());
                                        licznik++;
                                    } else if(token.getNazwa().equals("TEXT")){
                                        //System.out.println(variables.indexOf(token.getWartosc()));
                                        //Variables zmienna = variables.get(variables.indexOf(token.getWartosc()));
                                        if(var.get(token.getWartosc()).isType())controller.konsolaTextArea.appendText(var.get(token.getWartosc()).getStringValue());
                                        else controller.konsolaTextArea.appendText(String.valueOf(var.get(token.getWartosc()).getDoubleValue()));
                                        licznik++;
                                    } else if (token.getNazwa().equals("NUMBER")){
                                        controller.konsolaTextArea.appendText(token.getWartosc());
                                        licznik++;
                                    }
                                } else morePrint = false;
                            }

                        controller.konsolaTextArea.appendText("\n");

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
                            token = tokens.get(licznik);
                        }
                        controller.konsolaTextArea.appendText("?");

                        controller.konsolaTextArea.setEditable(true);
                        String oldValue, newValue;
                        oldValue = controller.konsolaTextArea.getText();
                        newValue = controller.konsolaTextArea.getText().replace(oldValue, "");

                        while (newValue.equals("") || newValue.charAt(newValue.length() - 1) != '\n') {
                            newValue = controller.konsolaTextArea.getText().replace(oldValue, "");
                        }
                        newValue.replace("\n", "");
                        controller.konsolaTextArea.setEditable(false);
                        try {
                            double value = Double.parseDouble(newValue);
                            var.put(token.getWartosc(), new Variable(value));
                        } catch (NumberFormatException e) {
                            var.put(token.getWartosc(), new Variable(newValue));

                        }
                        printVar();
                        licznik++;
                        controller.konsolaTextArea.appendText("\n");

                    } else if(token.getWartosc().equals("INT")){
                        licznik++;
                        System.out.println(evaluateONP(wyrazenie()));
                    }
                } else if(token.getNazwa().equals("TEXT")){
                    String symbolName = token.getWartosc();
                    licznik++;
                    token=tokens.get(licznik);
                    if(token.getNazwa().equals("EQUAL")){
                        licznik++;
                        token=tokens.get(licznik);
                        if(token.getNazwa().equals("NUMBER")){
                            //var.put(symbolName, new Variable(Double.parseDouble(token.getWartosc())));
                            //printVar();

                            licznik++;
                            if(licznik!=tokens.size()){
                                token = tokens.get(licznik);
                                if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                        || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                    licznik--;
                                    token = tokens.get(licznik);

                                    var.put(symbolName, new Variable(evaluateONP(wyrazenie())));
                                    printVar();
                                } else {
                                    licznik--;
                                    token = tokens.get(licznik);
                                    var.put(symbolName, new Variable(Double.parseDouble(token.getWartosc())));
                                    printVar();
                                }
                            } else {
                                licznik--;
                                token = tokens.get(licznik);
                                var.put(symbolName, new Variable(Double.parseDouble(token.getWartosc())));
                                printVar();
                            }
                            licznik++;

                        } else if(token.getNazwa().equals("TEXT")){
                            //var.put(symbolName, new Variable(Double.parseDouble(token.getWartosc())));
                            //printVar();

                            licznik++;
                            if(licznik!=tokens.size()){
                                token = tokens.get(licznik);
                                if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                        || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                    licznik--;
                                    token = tokens.get(licznik);

                                    var.put(symbolName, new Variable(evaluateONP(wyrazenie())));
                                    printVar();
                                } else {
                                    licznik--;
                                    token = tokens.get(licznik);
                                    var.put(symbolName, new Variable(var.get(token.getWartosc()).getDoubleValue()));
                                    printVar();
                                }
                            } else {
                                licznik--;
                                token = tokens.get(licznik);
                                var.put(symbolName, new Variable(var.get(token.getWartosc()).getDoubleValue()));
                                printVar();
                            }
                            licznik++;

                        }else if(token.getNazwa().equals("STRING")){
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
