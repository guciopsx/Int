package interpreter;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.awt.desktop.SystemSleepEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

public class Parser extends Exception{
    private int licznik=0;
    private Controller controller;
    private ArrayList<Token> tokens;
    private Hashtable<String, Variable> var;
    private Input input;

    Parser(ArrayList<Token> tokens, Controller controller, Input input){
        this.controller = controller;
        this.tokens = tokens;
        this.input = input;
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
        while(!token.getNazwa().equals("LINENUMBER") && !token.getNazwa().equals("KEYWORD") && !token.getNazwa().equals("COMMA") && !token.getNazwa().equals("SEMICOLON") && liczonp!=tokens.size()
                && !token.getNazwa().equals("LESS") && !token.getNazwa().equals("GREATER") && !token.getNazwa().equals("EQUAL") && !token.getNazwa().equals("LE")
                && !token.getNazwa().equals("GE") && !token.getNazwa().equals("NE")  ){
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
                    while(!stos.isEmpty() && (stos.peek().equals("^") && stos.peek().length()>1)){
                        onp += stos.pop() + ",";
                    }
                    stos.push( String.valueOf(token.getNazwa()));
                    } else if(token.getNazwa().equals("^")){
                    //while(!stos.isEmpty() && stos.peek().length()>1){
                     //   onp += stos.pop() + ",";
                    //}
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
        Platform.runLater(new Runnable() {
            @Override public void run() {
                controller.varTextArea.clear();
            }
        });

        for(String name: var.keySet()){
            if(var.get(name).isType())
                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        controller.varTextArea.appendText("Zmienna: " + name.toString() + " Wartość: " + var.get(name).getStringValue()+"\n");
                    }
                });

            else Platform.runLater(new Runnable() {
                @Override public void run() {
                    controller.varTextArea.appendText("Zmienna: " + name.toString() + " Wartość: " + var.get(name).getDoubleValue()+"\n");
                }
            });
        }
    }

    private boolean evalexp(double left, String expression, double right){
        if(expression.equals("=")){
            if(left==right)return true;
            else return false;
        } else if(expression.equals("<")){
            if(left<right)return true;
            else return false;
        } else if(expression.equals(">")){
            if(left>right)return true;
            else return false;
        } else if(expression.equals("<=")){
            if(left<=right)return true;
            else return false;
        } else if(expression.equals(">=")){
            if(left>=right)return true;
            else return false;
        } else if(expression.equals("<>")){
            if(left!=right)return true;
            else return false;
        }
        return false;
    }

    public void parse(){
        Hashtable<Integer, Integer> etykiety = new Hashtable<Integer, Integer>();
        Hashtable<String, Integer> etykietyFor = new Hashtable<String, Integer>();
        //variables = new ArrayList<Variables>();
        var = new Hashtable<String, Variable>();
        licznik=0;
        Token token;

        while (licznik<tokens.size()){
            token=tokens.get(licznik);
            if(token.getNazwa().equals("LINENUMBER")) {
                etykiety.put(Integer.valueOf(token.getWartosc()), licznik);
            }
            licznik++;
        } licznik=0;


        while (licznik<tokens.size()){
            token=tokens.get(licznik);
            if(token.getWartosc()!=null && token.getWartosc().equals("FOR")) {
                licznik++;
                token=tokens.get(licznik);
                if(token.getNazwa().equals("TEXT")) {
                    String symbolFor = token.getWartosc();
                    while (!token.getNazwa().equals("LINENUMBER")){
                        licznik++;
                        token = tokens.get(licznik);
                    }
                    etykietyFor.put(symbolFor, Integer.valueOf(token.getWartosc()));
                }
            }
            licznik++;
        } licznik=0;

        while(licznik<tokens.size()){
            token=tokens.get(licznik);
            if(token.getNazwa().equals("LINENUMBER")) {
                //etykiety.put(Integer.valueOf(token.getWartosc()), licznik);
                licznik++;
                token=tokens.get(licznik);
                if (token.getNazwa().equals("KEYWORD")) {
                    if (token.getWartosc().equals("PRINT")) {
                        licznik++;
                        token = tokens.get(licznik);
                        if(token.getNazwa().equals("STRING")){
                            ui(token.getWartosc());
                            licznik++;
                        } else if(token.getNazwa().equals("TEXT")){
                            //System.out.println(variables.indexOf(token.getWartosc()));
                            //Variables zmienna = variables.get(variables.indexOf(token.getWartosc()));

                            licznik++;
                            if(licznik!=tokens.size()){
                                token = tokens.get(licznik);
                                if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                        || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                    licznik--;
                                    token = tokens.get(licznik);

                                    //ui(String.valueOf(evaluateONP(wyrazenie())));
                                    ui(String.valueOf(evaluateONP(wyrazenie())));
                                } else {
                                    licznik--;
                                    token = tokens.get(licznik);
                                    //if(var.get(token.getWartosc()).isType())ui(var.get(token.getWartosc()).getStringValue());
                                    //else ui(String.valueOf(var.get(token.getWartosc()).getDoubleValue()));
                                    if(var.get(token.getWartosc()).isType())ui(var.get(token.getWartosc()).getStringValue());
                                    else ui(String.valueOf(var.get(token.getWartosc()).getDoubleValue()));
                                }
                            } else {
                                licznik--;
                                token = tokens.get(licznik);
                                if(var.get(token.getWartosc()).isType())ui(var.get(token.getWartosc()).getStringValue());
                                else ui(String.valueOf(var.get(token.getWartosc()).getDoubleValue()));
                            }
                            licznik++;
                            //if(var.get(token.getWartosc()).isType())ui(var.get(token.getWartosc()).getStringValue());
                            //else ui(String.valueOf(var.get(token.getWartosc()).getDoubleValue()));
                            //licznik++;
                        } else if (token.getNazwa().equals("NUMBER")){
                            licznik++;
                            if(licznik!=tokens.size()){
                                token = tokens.get(licznik);
                                if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                        || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                    licznik--;
                                    token = tokens.get(licznik);

                                    ui(String.valueOf(evaluateONP(wyrazenie())));
                                } else {
                                    licznik--;
                                    token = tokens.get(licznik);
                                    ui(token.getWartosc());
                                }
                            } else {
                                licznik--;
                                token = tokens.get(licznik);
                                ui(token.getWartosc());
                            }
                            licznik++;
                            //ui(token.getWartosc());
                            //licznik++;
                        } else if (token.getNazwa().equals("LPARE")){
                            ui(String.valueOf(evaluateONP(wyrazenie())));
                            licznik++;
                        }
                        boolean morePrint = true;
                            while (morePrint){
                                token = tokens.get(licznik);
                                if(token.getNazwa().equals("COMMA")){
                                    licznik++;
                                    token = tokens.get(licznik);
                                    if(token.getNazwa().equals("STRING")){
                                        ui("\t"+token.getWartosc());
                                        licznik++;
                                    } else if(token.getNazwa().equals("TEXT")){
                                        //System.out.println(variables.indexOf(token.getWartosc()));
                                        //Variables zmienna = variables.get(variables.indexOf(token.getWartosc()));
                                        licznik++;
                                        if(licznik!=tokens.size()){
                                            token = tokens.get(licznik);
                                            if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                                    || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                                licznik--;
                                                token = tokens.get(licznik);

                                                ui("\t" + String.valueOf(evaluateONP(wyrazenie())));
                                            } else {
                                                licznik--;
                                                token = tokens.get(licznik);
                                                if(var.get(token.getWartosc()).isType())ui("\t" + var.get(token.getWartosc()).getStringValue());
                                                else ui(String.valueOf("\t" + var.get(token.getWartosc()).getDoubleValue()));
                                            }
                                        } else {
                                            licznik--;
                                            token = tokens.get(licznik);
                                            if(var.get(token.getWartosc()).isType())ui("\t" + var.get(token.getWartosc()).getStringValue());
                                            else ui(String.valueOf("\t" + var.get(token.getWartosc()).getDoubleValue()));
                                        }
                                        licznik++;
                                        //if(var.get(token.getWartosc()).isType())ui(var.get(token.getWartosc()).getStringValue());
                                        //else ui(String.valueOf(var.get(token.getWartosc()).getDoubleValue()));
                                        //licznik++;
                                    } else if (token.getNazwa().equals("NUMBER")){
                                        licznik++;
                                        if(licznik!=tokens.size()){
                                            token = tokens.get(licznik);
                                            if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                                    || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                                licznik--;
                                                token = tokens.get(licznik);

                                                ui("\t" + String.valueOf(evaluateONP(wyrazenie())));
                                            } else {
                                                licznik--;
                                                token = tokens.get(licznik);
                                                ui("\t" + token.getWartosc());
                                            }
                                        } else {
                                            licznik--;
                                            token = tokens.get(licznik);
                                            ui("\t" + token.getWartosc());
                                        }
                                        licznik++;
                                        //ui(token.getWartosc());
                                        //licznik++;
                                    } else if (token.getNazwa().equals("LPARE")){
                                        ui("\t" + String.valueOf(evaluateONP(wyrazenie())));
                                        licznik++;
                                    }
                                }else if(token.getNazwa().equals("SEMICOLON")){
                                    licznik++;
                                    token = tokens.get(licznik);
                                    if(token.getNazwa().equals("STRING")){
                                        ui(token.getWartosc());
                                        licznik++;
                                    } else if(token.getNazwa().equals("TEXT")){
                                        //System.out.println(variables.indexOf(token.getWartosc()));
                                        //Variables zmienna = variables.get(variables.indexOf(token.getWartosc()));

                                        licznik++;
                                        if(licznik!=tokens.size()){
                                            token = tokens.get(licznik);
                                            if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                                    || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                                licznik--;
                                                token = tokens.get(licznik);

                                                ui(String.valueOf(evaluateONP(wyrazenie())));
                                            } else {
                                                licznik--;
                                                token = tokens.get(licznik);
                                                if(var.get(token.getWartosc()).isType())ui(var.get(token.getWartosc()).getStringValue());
                                                else ui(String.valueOf(var.get(token.getWartosc()).getDoubleValue()));
                                            }
                                        } else {
                                            licznik--;
                                            token = tokens.get(licznik);
                                            if(var.get(token.getWartosc()).isType())ui(var.get(token.getWartosc()).getStringValue());
                                            else ui(String.valueOf(var.get(token.getWartosc()).getDoubleValue()));
                                        }
                                        licznik++;
                                        //if(var.get(token.getWartosc()).isType())ui(var.get(token.getWartosc()).getStringValue());
                                        //else ui(String.valueOf(var.get(token.getWartosc()).getDoubleValue()));
                                        //licznik++;
                                    } else if (token.getNazwa().equals("NUMBER")){
                                        licznik++;
                                        if(licznik!=tokens.size()){
                                            token = tokens.get(licznik);
                                            if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                                    || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                                licznik--;
                                                token = tokens.get(licznik);

                                                ui(String.valueOf(evaluateONP(wyrazenie())));
                                            } else {
                                                licznik--;
                                                token = tokens.get(licznik);
                                                ui(token.getWartosc());
                                            }
                                        } else {
                                            licznik--;
                                            token = tokens.get(licznik);
                                            ui(token.getWartosc());
                                        }
                                        licznik++;
                                        //ui(token.getWartosc());
                                        //licznik++;
                                    } else if (token.getNazwa().equals("LPARE")){
                                        ui(String.valueOf(evaluateONP(wyrazenie())));
                                        licznik++;
                                    }
                                } else morePrint = false;
                            }

                        ui("\n");

                    } else if (token.getWartosc().equals("GOTO")) {
                        licznik++;
                        token = tokens.get(licznik);
                        licznik = etykiety.get(Integer.valueOf(token.getWartosc()));
                    } else if (token.getWartosc().equals("IF")){
                        String expression = "";
                        licznik++;
                        token = tokens.get(licznik);
                        if(token.getNazwa().equals("TEXT")) {
                            licznik++;
                            token = tokens.get(licznik);
                            if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                    || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                licznik--;
                                token = tokens.get(licznik);
                                var.put("IFL", new Variable(evaluateONP(wyrazenie())));
                                printVar();

                            } else {
                                licznik--;
                                token = tokens.get(licznik);
                                var.put("IFL", new Variable(var.get(token.getWartosc()).getDoubleValue()));
                                printVar();
                                licznik++;
                            }

                            token = tokens.get(licznik);



                            if(token.getNazwa().equals("LESS"))expression="<";
                            else if(token.getNazwa().equals("GREATER"))expression=">";
                            else if(token.getNazwa().equals("EQUAL"))expression="=";
                            else if(token.getNazwa().equals("LE"))expression="<=";
                            else if(token.getNazwa().equals("GE"))expression=">=";
                            else if(token.getNazwa().equals("NE"))expression="<>";

                            licznik++;

                            token = tokens.get(licznik);

                            if(token.getNazwa().equals("TEXT")) {
                                licznik++;
                                token = tokens.get(licznik);
                                if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                        || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                    licznik--;
                                    token = tokens.get(licznik);
                                    var.put("IFR", new Variable(evaluateONP(wyrazenie())));
                                    printVar();

                                } else {
                                    licznik--;
                                    token = tokens.get(licznik);
                                    var.put("IFR", new Variable(var.get(token.getWartosc()).getDoubleValue()));
                                    printVar();
                                }
                            } else if(token.getNazwa().equals("NUMBER")){
                                licznik++;
                                token = tokens.get(licznik);
                                if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                        || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                    licznik--;
                                    token = tokens.get(licznik);
                                    var.put("IFR", new Variable(evaluateONP(wyrazenie())));
                                    printVar();

                                } else {
                                    licznik--;
                                    token = tokens.get(licznik);
                                    var.put("IFR", new Variable(Double.parseDouble(token.getWartosc())));
                                    printVar();
                                    licznik++;
                                }
                            } else if (token.getNazwa().equals("LPARE")){
                                var.put("IFR", new Variable(evaluateONP(wyrazenie())));
                                printVar();
                            }



                        } else if(token.getNazwa().equals("NUMBER")){
                            licznik++;
                            token = tokens.get(licznik);
                            if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                    || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                licznik--;
                                token = tokens.get(licznik);
                                var.put("IFL", new Variable(evaluateONP(wyrazenie())));
                                printVar();

                            } else {
                                licznik--;
                                token = tokens.get(licznik);
                                var.put("IFL", new Variable(Double.parseDouble(token.getWartosc())));
                                printVar();
                                licznik++;

                            }

                            token = tokens.get(licznik);



                            if(token.getNazwa().equals("LESS"))expression="<";
                            else if(token.getNazwa().equals("GREATER"))expression=">";
                            else if(token.getNazwa().equals("EQUAL"))expression="=";
                            else if(token.getNazwa().equals("LE"))expression="<=";
                            else if(token.getNazwa().equals("GE"))expression=">=";
                            else if(token.getNazwa().equals("NE"))expression="<>";

                            licznik++;

                            token = tokens.get(licznik);

                            if(token.getNazwa().equals("TEXT")) {
                                licznik++;
                                token = tokens.get(licznik);
                                if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                        || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                    licznik--;
                                    token = tokens.get(licznik);
                                    var.put("IFR", new Variable(evaluateONP(wyrazenie())));
                                    printVar();

                                } else {
                                    licznik--;
                                    token = tokens.get(licznik);
                                    var.put("IFR", new Variable(var.get(token.getWartosc()).getDoubleValue()));
                                    printVar();

                                }
                            } else if(token.getNazwa().equals("NUMBER")){
                                licznik++;
                                token = tokens.get(licznik);
                                if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                        || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                    licznik--;
                                    token = tokens.get(licznik);
                                    var.put("IFR", new Variable(evaluateONP(wyrazenie())));
                                    printVar();

                                } else {
                                    licznik--;
                                    token = tokens.get(licznik);
                                    var.put("IFR", new Variable(Double.parseDouble(token.getWartosc())));
                                    printVar();
                                    licznik++;
                                }
                            }else if (token.getNazwa().equals("LPARE")){
                                var.put("IFR", new Variable(evaluateONP(wyrazenie())));
                                printVar();
                            }
                        } else if(token.getNazwa().equals("LPARE")){

                                var.put("IFL", new Variable(evaluateONP(wyrazenie())));
                                printVar();

                            token = tokens.get(licznik);



                            if(token.getNazwa().equals("LESS"))expression="<";
                            else if(token.getNazwa().equals("GREATER"))expression=">";
                            else if(token.getNazwa().equals("EQUAL"))expression="=";
                            else if(token.getNazwa().equals("LE"))expression="<=";
                            else if(token.getNazwa().equals("GE"))expression=">=";
                            else if(token.getNazwa().equals("NE"))expression="<>";

                            licznik++;

                            token = tokens.get(licznik);

                            if(token.getNazwa().equals("TEXT")) {
                                licznik++;
                                token = tokens.get(licznik);
                                if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                        || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                    licznik--;
                                    token = tokens.get(licznik);
                                    var.put("IFR", new Variable(evaluateONP(wyrazenie())));
                                    printVar();

                                } else {
                                    licznik--;
                                    token = tokens.get(licznik);
                                    var.put("IFR", new Variable(var.get(token.getWartosc()).getDoubleValue()));
                                    printVar();

                                }
                            } else if(token.getNazwa().equals("NUMBER")){
                                licznik++;
                                token = tokens.get(licznik);
                                if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                        || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                    licznik--;
                                    token = tokens.get(licznik);
                                    var.put("IFR", new Variable(evaluateONP(wyrazenie())));
                                    printVar();

                                } else {
                                    licznik--;
                                    token = tokens.get(licznik);
                                    var.put("IFR", new Variable(Double.parseDouble(token.getWartosc())));
                                    printVar();
                                    licznik++;
                                }
                            }else if (token.getNazwa().equals("LPARE")){
                                var.put("IFR", new Variable(evaluateONP(wyrazenie())));
                                printVar();
                            }
                        }

                        token = tokens.get(licznik);
                        if(evalexp(var.get("IFL").getDoubleValue(),expression,var.get("IFR").getDoubleValue())){
                            if(token.getWartosc().equals("THEN")){
                                licznik++;
                                token = tokens.get(licznik);
                                if(token.getWartosc().equals("GOTO") && token.getNazwa().equals("KEYWORD")){
                                    licznik++;
                                    token = tokens.get(licznik);
                                    licznik = etykiety.get(Integer.valueOf(token.getWartosc()));

                                }else if(token.getNazwa().equals("NUMBER")){
                                    licznik = etykiety.get(Integer.valueOf(token.getWartosc()));
                                }else {
                                    if (token.getWartosc().equals("PRINT")) {
                                        licznik++;
                                        token = tokens.get(licznik);
                                        if(token.getNazwa().equals("STRING")){
                                            ui(token.getWartosc());
                                            licznik++;
                                        } else if(token.getNazwa().equals("TEXT")){
                                            //System.out.println(variables.indexOf(token.getWartosc()));
                                            //Variables zmienna = variables.get(variables.indexOf(token.getWartosc()));

                                            licznik++;
                                            if(licznik!=tokens.size()){
                                                token = tokens.get(licznik);
                                                if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                                        || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                                    licznik--;
                                                    token = tokens.get(licznik);

                                                    ui(String.valueOf(evaluateONP(wyrazenie())));
                                                } else {
                                                    licznik--;
                                                    token = tokens.get(licznik);
                                                    if(var.get(token.getWartosc()).isType())ui(var.get(token.getWartosc()).getStringValue());
                                                    else ui(String.valueOf(var.get(token.getWartosc()).getDoubleValue()));
                                                }
                                            } else {
                                                licznik--;
                                                token = tokens.get(licznik);
                                                if(var.get(token.getWartosc()).isType())ui(var.get(token.getWartosc()).getStringValue());
                                                else ui(String.valueOf(var.get(token.getWartosc()).getDoubleValue()));
                                            }
                                            licznik++;
                                            //if(var.get(token.getWartosc()).isType())ui(var.get(token.getWartosc()).getStringValue());
                                            //else ui(String.valueOf(var.get(token.getWartosc()).getDoubleValue()));
                                            //licznik++;
                                        } else if (token.getNazwa().equals("NUMBER")){
                                            licznik++;
                                            if(licznik!=tokens.size()){
                                                token = tokens.get(licznik);
                                                if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                                        || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                                    licznik--;
                                                    token = tokens.get(licznik);

                                                    ui(String.valueOf(evaluateONP(wyrazenie())));
                                                } else {
                                                    licznik--;
                                                    token = tokens.get(licznik);
                                                    ui(token.getWartosc());
                                                }
                                            } else {
                                                licznik--;
                                                token = tokens.get(licznik);
                                                ui(token.getWartosc());
                                            }
                                            licznik++;
                                            //ui(token.getWartosc());
                                            //licznik++;
                                        } else if (token.getNazwa().equals("LPARE")) {
                                            ui(String.valueOf(evaluateONP(wyrazenie())));
                                            licznik++;
                                        }

                                        boolean morePrint = true;
                                        while (morePrint){
                                            token = tokens.get(licznik);
                                            if(token.getNazwa().equals("COMMA")){
                                                licznik++;
                                                token = tokens.get(licznik);
                                                if(token.getNazwa().equals("STRING")){
                                                    ui("\t"+token.getWartosc());
                                                    licznik++;
                                                } else if(token.getNazwa().equals("TEXT")){
                                                    //System.out.println(variables.indexOf(token.getWartosc()));
                                                    //Variables zmienna = variables.get(variables.indexOf(token.getWartosc()));
                                                    licznik++;
                                                    if(licznik!=tokens.size()){
                                                        token = tokens.get(licznik);
                                                        if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                                                || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                                            licznik--;
                                                            token = tokens.get(licznik);

                                                            ui("\t" + String.valueOf(evaluateONP(wyrazenie())));
                                                        } else {
                                                            licznik--;
                                                            token = tokens.get(licznik);
                                                            if(var.get(token.getWartosc()).isType())ui("\t" + var.get(token.getWartosc()).getStringValue());
                                                            else ui(String.valueOf("\t" + var.get(token.getWartosc()).getDoubleValue()));
                                                        }
                                                    } else {
                                                        licznik--;
                                                        token = tokens.get(licznik);
                                                        if(var.get(token.getWartosc()).isType())ui("\t" + var.get(token.getWartosc()).getStringValue());
                                                        else ui(String.valueOf("\t" + var.get(token.getWartosc()).getDoubleValue()));
                                                    }
                                                    licznik++;
                                                    //if(var.get(token.getWartosc()).isType())ui(var.get(token.getWartosc()).getStringValue());
                                                    //else ui(String.valueOf(var.get(token.getWartosc()).getDoubleValue()));
                                                    //licznik++;
                                                } else if (token.getNazwa().equals("NUMBER")){
                                                    licznik++;
                                                    if(licznik!=tokens.size()){
                                                        token = tokens.get(licznik);
                                                        if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                                                || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                                            licznik--;
                                                            token = tokens.get(licznik);

                                                            ui("\t" + String.valueOf(evaluateONP(wyrazenie())));
                                                        } else {
                                                            licznik--;
                                                            token = tokens.get(licznik);
                                                            ui("\t" + token.getWartosc());
                                                        }
                                                    } else {
                                                        licznik--;
                                                        token = tokens.get(licznik);
                                                        ui("\t" + token.getWartosc());
                                                    }
                                                    licznik++;
                                                    //ui(token.getWartosc());
                                                    //licznik++;
                                                }else if (token.getNazwa().equals("LPARE")) {
                                                    ui("\t" + String.valueOf(evaluateONP(wyrazenie())));
                                                    licznik++;
                                                }
                                            }else if(token.getNazwa().equals("SEMICOLON")){
                                                licznik++;
                                                token = tokens.get(licznik);
                                                if(token.getNazwa().equals("STRING")){
                                                    ui(token.getWartosc());
                                                    licznik++;
                                                } else if(token.getNazwa().equals("TEXT")){
                                                    //System.out.println(variables.indexOf(token.getWartosc()));
                                                    //Variables zmienna = variables.get(variables.indexOf(token.getWartosc()));

                                                    licznik++;
                                                    if(licznik!=tokens.size()){
                                                        token = tokens.get(licznik);
                                                        if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                                                || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                                            licznik--;
                                                            token = tokens.get(licznik);

                                                            ui(String.valueOf(evaluateONP(wyrazenie())));
                                                        } else {
                                                            licznik--;
                                                            token = tokens.get(licznik);
                                                            if(var.get(token.getWartosc()).isType())ui(var.get(token.getWartosc()).getStringValue());
                                                            else ui(String.valueOf(var.get(token.getWartosc()).getDoubleValue()));
                                                        }
                                                    } else {
                                                        licznik--;
                                                        token = tokens.get(licznik);
                                                        if(var.get(token.getWartosc()).isType())ui(var.get(token.getWartosc()).getStringValue());
                                                        else ui(String.valueOf(var.get(token.getWartosc()).getDoubleValue()));
                                                    }
                                                    licznik++;
                                                    //if(var.get(token.getWartosc()).isType())ui(var.get(token.getWartosc()).getStringValue());
                                                    //else ui(String.valueOf(var.get(token.getWartosc()).getDoubleValue()));
                                                    //licznik++;
                                                } else if (token.getNazwa().equals("NUMBER")){
                                                    licznik++;
                                                    if(licznik!=tokens.size()){
                                                        token = tokens.get(licznik);
                                                        if(token.getNazwa().equals("+") || token.getNazwa().equals("-") || token.getNazwa().equals("*")
                                                                || token.getNazwa().equals("/") || token.getNazwa().equals("^")){
                                                            licznik--;
                                                            token = tokens.get(licznik);

                                                            ui(String.valueOf(evaluateONP(wyrazenie())));
                                                        } else {
                                                            licznik--;
                                                            token = tokens.get(licznik);
                                                            ui(token.getWartosc());
                                                        }
                                                    } else {
                                                        licznik--;
                                                        token = tokens.get(licznik);
                                                        ui(token.getWartosc());
                                                    }
                                                    licznik++;
                                                    //ui(token.getWartosc());
                                                    //licznik++;
                                                } else if (token.getNazwa().equals("LPARE")) {
                                                    ui(String.valueOf(evaluateONP(wyrazenie())));
                                                    licznik++;
                                                }
                                            } else morePrint = false;
                                        }

                                        ui("\n");

                                    } else if (token.getWartosc().equals("INPUT")) {
                                        int licznikInput = 1;
                                        licznik++;
                                        token = tokens.get(licznik);
                                        if (token.getNazwa().equals("STRING")) {
                                            ui(token.getWartosc());
                                            licznik++;
                                            token = tokens.get(licznik);
                                            if(token.getNazwa().equals("SEMICOLON")){
                                                licznik++;
                                                token = tokens.get(licznik);
                                            }
                                        }
                                        for(int i=0;i<licznikInput;i++) ui("?");

                                        uiFocus();

                                        input.clear();
                                        uiE(true);

                                        while (!input.getText().contains("\n")) {
                                        }

                                        input.rem();
                                        uiE(false);

                                        try {
                                            double value = Double.parseDouble(input.getText());
                                            var.put(token.getWartosc(), new Variable(value));
                                        } catch (NumberFormatException e) {
                                            var.put(token.getWartosc(), new Variable(input.getText().replace("\n", "")));

                                        }
                                        printVar();
                                        licznik++;
                                        //ui("\n");

                                        boolean moreInput = true;
                                        while (moreInput){
                                            token = tokens.get(licznik);
                                            if(token.getNazwa().equals("COMMA")){
                                                licznikInput++;
                                                licznik++;
                                                token = tokens.get(licznik);

                                                for(int i=0;i<licznikInput;i++) ui("?");

                                                uiFocus();

                                                input.clear();
                                                uiE(true);

                                                while (!input.getText().contains("\n")) {
                                                }

                                                input.rem();
                                                uiE(false);
                                                try {
                                                    double value = Double.parseDouble(input.getText());
                                                    var.put(token.getWartosc(), new Variable(value));
                                                } catch (NumberFormatException e) {
                                                    var.put(token.getWartosc(), new Variable(input.getText().replace("\n", "")));

                                                }
                                                printVar();
                                                licznik++;

                                            }

                                            else moreInput = false;
                                        }

                                    }
                                }
                            }

                        } else {
                            while (licznik<tokens.size() && !token.getNazwa().equals("LINENUMBER")){
                                licznik++;
                                token = tokens.get(licznik);
                            }
                        }


                    }else if (token.getWartosc().equals("INPUT")) {
                        int licznikInput = 1;
                        licznik++;
                        token = tokens.get(licznik);
                        if (token.getNazwa().equals("STRING")) {
                            ui(token.getWartosc());
                            licznik++;
                            token = tokens.get(licznik);
                            if(token.getNazwa().equals("SEMICOLON")){
                                licznik++;
                                token = tokens.get(licznik);
                            }
                        }
                        for(int i=0;i<licznikInput;i++) ui("?");

                        uiFocus();

                        input.clear();
                        uiE(true);

                        while (!input.getText().contains("\n")) {
                        }

                        input.rem();
                        uiE(false);

                        try {
                            double value = Double.parseDouble(input.getText());
                            var.put(token.getWartosc(), new Variable(value));
                        } catch (NumberFormatException e) {
                            var.put(token.getWartosc(), new Variable(input.getText().replace("\n", "")));

                        }
                        printVar();
                        licznik++;
                        //ui("\n");

                        boolean moreInput = true;
                        while (moreInput){
                            token = tokens.get(licznik);
                            if(token.getNazwa().equals("COMMA")){
                                licznikInput++;
                                licznik++;
                                token = tokens.get(licznik);

                                for(int i=0;i<licznikInput;i++) ui("?");

                                uiFocus();

                                input.clear();
                                uiE(true);

                                while (!input.getText().contains("\n")) {
                                }

                                input.rem();
                                uiE(false);
                                try {
                                    double value = Double.parseDouble(input.getText());
                                    var.put(token.getWartosc(), new Variable(value));
                                } catch (NumberFormatException e) {
                                    var.put(token.getWartosc(), new Variable(input.getText().replace("\n", "")));

                                }
                                printVar();
                                licznik++;

                            }

                            else moreInput = false;
                        }

                    } else if(token.getWartosc().equals("FOR")){
                        licznik++;
                        String symbolName = "";
                        token = tokens.get(licznik);
                        if(token.getNazwa().equals("TEXT")){
                            symbolName = token.getWartosc();
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

                                }
                            }
                        }

                        token = tokens.get(licznik);
                        if(token.getWartosc().equals("TO")){
                            licznik++;
                            token = tokens.get(licznik);
                            if(token.getNazwa().equals("NUMBER")){
                                var.put(symbolName + "-TO", new Variable(Double.parseDouble(token.getWartosc())));
                                printVar();
                            }
                        }
                        licznik++;
                        token = tokens.get(licznik);
                        if(token.getWartosc().equals("STEP")){
                            licznik++;
                            token = tokens.get(licznik);
                            if(token.getNazwa().equals("NUMBER")) {
                                var.put(symbolName + "-STEP", new Variable(Double.parseDouble(token.getWartosc())));
                                printVar();
                                licznik++;
                            } else if (token.getNazwa().equals("LPARE")){
                                var.put(symbolName + "-STEP", new Variable(evaluateONP(wyrazenie())));
                                printVar();
                                licznik++;
                            }
                        } else {
                            var.put(symbolName + "-STEP", new Variable(Double.parseDouble("1.0")));
                            printVar();
                        }


                    } else if(token.getWartosc().equals("NEXT")){
                        licznik++;
                        token = tokens.get(licznik);
                        if(token.getNazwa().equals("TEXT")){
                            double to, step, val;
                            val = var.get(token.getWartosc()).getDoubleValue();
                            to = var.get(token.getWartosc()+"-TO").getDoubleValue();
                            step = var.get(token.getWartosc()+"-STEP").getDoubleValue();
                            if (val < to && step > 0) {
                                var.put(token.getWartosc(),new Variable(val+step));
                                printVar();
                                licznik=etykiety.get(etykietyFor.get(token.getWartosc()));
                            }else if (val > to && step < 0){
                                var.put(token.getWartosc(),new Variable(val+step));
                                printVar();
                                licznik=etykiety.get(etykietyFor.get(token.getWartosc()));
                            }
                            else licznik++;
                        }

                    } else if(token.getWartosc().equals("STOP")) {
                        ui("Aby kontunować wciśnij enter");
                        uiFocus();
                        input.clear();
                        uiE(true);

                        while (!input.getText().contains("\n")) {
                        }

                        input.rem();
                        uiE(false);
                        licznik++;

                    } else if(token.getWartosc().equals("LET")){
                        licznik++;
                        token = tokens.get(licznik);
                        if(token.getNazwa().equals("TEXT")){
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
                                }else if(token.getNazwa().equals("LPARE")){
                                    var.put(symbolName, new Variable(evaluateONP(wyrazenie())));
                                    printVar();
                                    licznik++;
                                }else if(token.getNazwa().equals("NUMBER")){
                                    licznik++;
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
                                        licznik++;
                                    }
                                }
                            }

                        }
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
                        }else if(token.getNazwa().equals("LPARE")){
                            var.put(symbolName, new Variable(evaluateONP(wyrazenie())));
                            printVar();
                            licznik++;
                        }else if(token.getNazwa().equals("NUMBER")){
                            licznik++;
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
                                licznik++;
                            }
                        }
                    }

                }
            }
            else {
                //throw new Exception("Błędna linia");
            }
        }
    }

    private void ui(String text){
        Platform.runLater(new Runnable() {
            @Override public void run() {
                controller.konsolaTextArea.appendText(text);
            }
        });
    }

    private void uiE(Boolean edit){
        Platform.runLater(new Runnable() {
            @Override public void run() {
                controller.konsolaTextArea.setEditable(edit);
            }
        });
    }

    private void uiFocus(){
        Platform.runLater(new Runnable() {
            @Override public void run() {
                controller.konsolaTextArea.requestFocus();
            }
        });
    }
}
