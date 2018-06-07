package interpreter;

import java.util.ArrayList;

public class AssignStatement implements Statement {
    private final String name;
    private final Expression value;
    private final ArrayList<Variables> variables;
    public AssignStatement(String name, Expression value, ArrayList<Variables> variables) {
        this.name = name;
        this.value = value;
        this.variables=variables;
    }

    public void execute() {
        variables.add(new Variables(name, value.evaluate().toString()));
    }


}
