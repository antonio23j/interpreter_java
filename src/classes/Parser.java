package classes;
import java.util.*;


public class Parser {

    private List<String> tokens;
    private int index = 0;

    public Parser(List<String> tokens) {
        this.tokens = tokens;
    }

    public List<ASTNode> parse() {
        List<ASTNode> ast = new ArrayList<>();
        while (index < tokens.size()) {
            if (Character.isDigit(tokens.get(index).charAt(0))) {
                ast.add(new ASTNode(ASTNode.Type.EXPRESSION, null, parseExpression()));
            } else {
                ast.add(parseStatement());
            }
        }
        return ast;
    }

    private ASTNode parseStatement() {
        String token = tokens.get(index);

        if (token.equals("Read")) {
            index++; // Skip "Read"
            String varName = tokens.get(index++);
            index++; // Skip ";"
            return new ASTNode(ASTNode.Type.READ, varName, null);
        }

        else if (token.equals("Display")) {
            index++; // Skip "Display"
            String varName = tokens.get(index++);
            index++; // Skip ";"
            return new ASTNode(ASTNode.Type.DISPLAY, varName, null);
        }

        else {
            // Assignment: a = a + 5; or a = 5;
            String varName = tokens.get(index++);
            index++; // Skip "="
            String expression = parseExpression();
            return new ASTNode(ASTNode.Type.ASSIGNMENT, varName, expression);
        }
    }

    private String parseExpression() {
        StringBuilder expression = new StringBuilder();
        while (index < tokens.size() && !tokens.get(index).equals(";")) {
            expression.append(tokens.get(index++)).append(" ");
        }
        if (index < tokens.size())
            index++; // Skip ";"
        return expression.toString().trim();
    }
}
