package classes;

import java.util.*;

public class Interpreter {
    private Map<String, Integer> variables = new HashMap<>();
    private Scanner scanner = new Scanner(System.in); // Scanner for user input

    public void execute(List<ASTNode> ast) {
        for (ASTNode node : ast) {
            switch (node.type) {
                case READ:
                    System.out.print("Enter a value for " + node.variable + ": ");
                    try {
                        int value = scanner.nextInt();
                        variables.put(node.variable, value);
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter an integer.");
                        scanner.next(); // Clear invalid input
                        variables.put(node.variable, 0); // Default value
                    }
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
        Stack<Integer> values = new Stack<>();
        Stack<String> operators = new Stack<>();

        Map<String, Integer> precedence = Map.of("+", 1, "-", 1, "*", 2, "/", 2);

        for (String part : parts) {
            if (part.matches("\\d+")) { // Number
                values.push(Integer.parseInt(part));
            } else if (variables.containsKey(part)) { // Variable
                values.push(variables.get(part));
            } else if (part.matches("[+\\-*/]")) { // Operator
                while (!operators.isEmpty() && precedence.get(operators.peek()) >= precedence.get(part)) {
                    processOperation(values, operators.pop());
                }
                operators.push(part);
            } else {
                throw new IllegalArgumentException("Invalid token: " + part);
            }
        }

        while (!operators.isEmpty()) {
            processOperation(values, operators.pop());
        }

        return values.pop();
    }

    private void processOperation(Stack<Integer> values, String operator) {
        if (values.size() < 2) {
            throw new IllegalArgumentException("Insufficient values for operation: " + operator);
        }
        int right = values.pop();
        int left = values.pop();

        int result = switch (operator) {
            case "+" -> left + right;
            case "-" -> left - right;
            case "*" -> left * right;
            case "/" -> {
                if (right == 0) throw new ArithmeticException("Division by 0 is not valid");
                yield left / right;
            }
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
        values.push(result);
    }

}
