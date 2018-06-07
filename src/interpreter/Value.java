package interpreter;

public interface Value extends Expression {
    String toString();

    double toNumber();
}
