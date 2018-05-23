package interpreter;

public class Token {
    private String nazwa;
    private String wartosc;
    Token(String nazwa, String wartosc){
        this.nazwa = nazwa;
        this.wartosc = wartosc;
    }

    public String getNazwa(){
        return nazwa;
    }

    public String getWartosc(){
        return wartosc;
    }
}