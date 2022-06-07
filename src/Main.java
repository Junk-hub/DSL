import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static String getCode() {
        String code = "";
        File file = new File("TestCode.txt");

        try (FileReader reader = new FileReader(file)) {
            int c;
            while ((c = reader.read()) != -1) {
                code = code.concat(Character.toString((char) c));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return code;
    }

    public static void main(String[] args) {
        String testCode = getCode();
        Lexer lexer = new Lexer(testCode);
        System.out.println(lexer);

        System.out.printf("\n/****SOURCE CODE****/\n%s\n\n", testCode);
        int len = 0;

        for (Token token: lexer.getTokens()) {
            if ("ENDL".equals(token.getType())) {
                len++;
            }
        }

        Parser parser = new Parser(lexer.getTokens(), len);
        try {
            parser.lang();
        } catch (ParserException | IndexOutOfBoundsException ignored) {}

        if (parser.correctCode) {
            Interpreter interpreter = new Interpreter(lexer.getTokens());
            System.out.println(interpreter);
        }
    }
}