package interpreter;

import javafx.scene.layout.BorderPane;

public class Input {
    private String text;
    public Input(String text){
        this.text = text;
    }

    public void append(String text){
        this.text += text;
    }

    public String getText(){
        return text;
    }

    public void rem() {
        text = text.substring(0, text.length() - 1);
    }

    public void clear(){
        text = "";
    }
}
