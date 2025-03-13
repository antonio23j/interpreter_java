import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Lexer {
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "\\b(Read|Display)\\b|[a-zA-Z]\\w*|\\d+|[=+\\-*/;]"
    );

    public static List<String> tokenize(String code) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(code);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }
}

class ASTNode {
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

class Parser {
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
        if (index < tokens.size()) index++; // Skip ";"
        return expression.toString().trim();
    }
}

class Interpreter {
    private Map<String, Integer> variables = new HashMap<>();

    public void execute(List<ASTNode> ast) {
        for (ASTNode node : ast) {
            switch (node.type) {
                case READ:
                    variables.put(node.variable, 0); // Default value
                    break;
                case ASSIGNMENT:
                    try {
                        int value = evaluateExpression(node.expression);
                        variables.put(node.variable, value);
                    } catch (ArithmeticException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case DISPLAY:
                    if (variables.containsKey(node.variable)) {
                        System.out.println(variables.get(node.variable));
                    } else {
                        System.out.println("Error: Undefined variable " + node.variable);
                    }
                    break;
                case EXPRESSION:
                    try {
                        System.out.println(evaluateExpression(node.expression));
                    } catch (ArithmeticException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
            }
        }
    }

    private int evaluateExpression(String expression) {
        String[] parts = expression.split(" ");

        if (parts.length == 1) {
            try {
                return Integer.parseInt(parts[0]);
            } catch (NumberFormatException e) {
                return variables.getOrDefault(parts[0], 0);
            }
        }

        Stack<Integer> values = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (String part : parts) {
            if (part.matches("\\d+")) {
                values.push(Integer.parseInt(part));
            } else if (variables.containsKey(part)) {
                values.push(variables.get(part));
            } else if (part.matches("[+\\-*/]")) {
                operators.push(part);
            } else {
                throw new IllegalArgumentException("Invalid operator or token: " + part);
            }
        }

        while (!operators.isEmpty()) {
            int right = values.pop();
            int left = values.pop();
            String op = operators.pop();

            int result;
            switch (op) {
                case "+":
                    result = left + right;
                    break;
                case "-":
                    result = left - right;
                    break;
                case "*":
                    result = left * right;
                    break;
                case "/":
                    if (right == 0) {
                        throw new ArithmeticException("Division by 0 is not valid");
                    }
                    result = left / right;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid operator: " + op);
            }
            values.push(result);
        }

        return values.pop();
    }
}