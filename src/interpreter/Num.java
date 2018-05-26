package interpreter;

public class Num implements AST {
    Token token;
    String wartosc;
    Num(Token token){
        this.token=token;
        wartosc=this.token.getWartosc();
    }
}
