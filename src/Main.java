import classes.ASTNode;
import classes.Interpreter;
import classes.Lexer;
import classes.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            String filePath = "/home/antonio/IdeaProjects/code.txt";
            String code = new String(Files.readAllBytes(Paths.get(filePath)));

            List<String> tokens = Lexer.tokenize(code);

            Parser parser = new Parser(tokens);
            List<ASTNode> ast = parser.parse();

            Interpreter interpreter = new Interpreter();
            interpreter.execute(ast);

        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }

//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Enter an expression");
//        String expression = scanner.nextLine();
//        List<String> tokens = Lexer.tokenize(expression);
//
//            Parser parser = new Parser(tokens);
//            List<ASTNode> ast = parser.parse();
//
//            Interpreter interpreter = new Interpreter();
//            interpreter.execute(ast);
    }
}
