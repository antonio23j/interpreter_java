package classes;

public class ASTNode {
    enum Type { ASSIGNMENT, READ, DISPLAY, EXPRESSION }
    Type type;
    String variable;
    String expression;

    ASTNode(Type type, String variable, String expression) {
        this.type = type;
        this.variable = variable;
        this.expression = expression;
    }
}
