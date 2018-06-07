package interpreter;

public class PrintStatement implements Statement {
    private final Expression expression;
    private final Controller controller;
    public PrintStatement(Expression expression, Controller controller){
        this.expression=expression;
        this.controller=controller;
    }

    public void execute(){
        controller.konsolaTextArea.appendText(expression.evaluate().toString()+"\n");
    }
}
