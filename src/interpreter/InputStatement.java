package interpreter;

import javafx.scene.control.TextInputDialog;

import java.util.ArrayList;
import java.util.Optional;

public class InputStatement implements Statement {
    private final String text;
    private final ArrayList<Variables> variables;
    public InputStatement(String text, ArrayList<Variables> variables){
        this.text=text;
        this.variables=variables;
    }

    public void execute(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Text Input Dialog");
        dialog.setHeaderText(null);
        dialog.setContentText("Test:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                double value = Double.parseDouble(result.get());
                variables.add(new Variables(text, value));
            } catch (NumberFormatException e) {
                variables.add(new Variables(text, result.get()));
            }
        }
    }
}
