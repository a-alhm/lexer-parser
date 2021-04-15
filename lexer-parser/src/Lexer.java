import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Lexer is a tool that takes a String as input and emits a list of Tokens.
 */
public class Lexer {
    public static final String INT="INT";
    public static final String FLOAT="FLOAT";
    public static final String IDENTIFER="ID";
    public static final String OPERATOR="OP";
    public static final String ASSIGNMENT="ASSIGN";
    public static final String EXPRASSIGNMENT="EXPR";
    public static final String EOFTOKEN="EOF";
    public String buffer;


    public Lexer(String fileName) {
        getInputFromFile(fileName);
    }

    public Lexer() {
        buffer = "";
    }

    public void getInputFromFile(String fileName)  {
        try {
            Path filePath = Paths.get(fileName);
            byte[] allBytes = Files.readAllBytes(filePath);
            buffer = new String (allBytes);
        } catch (IOException e) {
            System.out.println ("Файл не найден.");
            Scanner scanner = new Scanner(System.in);
            buffer = scanner.nextLine();
        }
    }

    public void getInputFromString(String s) {
        buffer = s;
    }

    public Token getNextToken(int ref) {
        Token t;
        t = getOperator(ref);
        if (t != null) {
            return t;
        }
        t = getAssignment(ref);
        if (t != null) {
            return t;
        }
        t = getExpressionAssignment(ref);
        if (t != null) {
            return t;
        }
        t = getIdentifier(ref);
        if (t != null) {
            return t;
        }
        t = getNumber(ref);
        return t;
    }

    public List<Token> getAllTokens() {

        List<Token> allToken = new ArrayList<>();
        int index = 0;
        while (index < buffer.length()){
            Token token = getNextToken(index);
            if (token == null){
                index++;
            } else {
                index += token.length();
                allToken.add(token);
            }
        }
        allToken.add(new Token(EOFTOKEN, "eof"));
        return allToken;
    }


    public Token getOperator(int ref) {
        if (buffer.charAt(ref) == '+' || buffer.charAt(ref) == '-' || buffer.charAt(ref) == '*' ||
            buffer.charAt(ref) == '/' || buffer.charAt(ref) == '^'){
            return new Token(OPERATOR, Character.toString(buffer.charAt(ref)));
        } else {
            return null;
        }
    }


    public Token getExpressionAssignment(int ref){
        if (buffer.charAt(ref) == '#') {
            return new Token(EXPRASSIGNMENT, "#");
        } else {
            return null;
        }
    }


    public Token getAssignment(int ref){
        if (buffer.charAt(ref) == '=') {
            return new Token(ASSIGNMENT, "=");
        } else {
            return null;
        }
    }

    public Token getIdentifier(int ref) {
        StringBuilder temp = new StringBuilder();
        if (Character.isLetter(buffer.charAt(ref))) {
            while (ref < buffer.length() && Character.isLetterOrDigit(buffer.charAt(ref))) {
                temp.append(buffer.charAt(ref));
                ref++;
            }
        } else {
            return null;
        }
        return new Token(IDENTIFER, temp.toString());
    }


    public Token getNumber(int ref) {
        StringBuilder temp = new StringBuilder();
        int decimalCount = 0;
        if (Character.isDigit(buffer.charAt(ref))) {

            while (ref < buffer.length() && Character.isDigit(buffer.charAt(ref)) || buffer.charAt(ref)=='.') {
                temp.append(buffer.charAt(ref));
                if (buffer.charAt(ref) == '.') {
                    decimalCount++;
                }
                if (decimalCount > 1) {
                    throw new IllegalArgumentException("error: float value  ");
                }
                ref++;
                if(ref >= buffer.length())
                    break;
            }
        } else {
            return null;
        }

        if (decimalCount == 1) {
            return new Token(FLOAT, temp.toString());
        } else {
            return new Token(INT, temp.toString());
        }
    }
}



