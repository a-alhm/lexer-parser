
import java.util.List;

public class LexerTester {
    public static void main(String[] args) {
        String fileName = "test.txt";
        Lexer lexer = new Lexer(fileName);
        System.out.println(lexer.buffer);

        List<Token> tokens = lexer.getAllTokens();
        for( Token t : tokens){
            System.out.println(t.type +" : "+ t.value);
        }

    }
}
