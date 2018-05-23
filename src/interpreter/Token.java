/*package interpreter;

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
}*/

package interpreter;

import javafx.beans.property.SimpleStringProperty;

public class Token {
    private SimpleStringProperty nazwa;
    private SimpleStringProperty wartosc;
    Token(String nazwa, String wartosc){
        this.nazwa = new SimpleStringProperty(nazwa);
        this.wartosc = new SimpleStringProperty(wartosc);
    }

    public String getNazwa(){
        return nazwa.get();
    }

    public String getWartosc(){
        return wartosc.get();
    }
}

