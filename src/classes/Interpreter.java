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
