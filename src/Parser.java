import java.util.ArrayList;
import java.util.Objects;

public class Parser {

    private final ArrayList<Token> tokens;
    private final int len;

    private int iterator;
    private int curLine;
    private Token curToken;

    public boolean correctCode;

    public Parser(ArrayList<Token> tokens, int len) {
        this.tokens = tokens;
        this.len = len;
        curLine = 0;
        iterator = 0;
        curToken = tokens.get(iterator);
        correctCode = true;
    }

    public void TERMINAL(String tokenType) throws ParserException {
        if (!Objects.equals(curToken.getType(), tokenType)) {
            correctCode = false;
            throw new ParserException(curLine, iterator, curToken, tokenType);
        }
    }

    public void terminalCheck(String tokenType) {
        try {
            TERMINAL(tokenType);
        } catch (ParserException e) {
            e.getInfo(curLine, iterator, e.current, e.expected);
            curToken = tokens.get(--iterator);
        }
        curToken = tokens.get(++iterator);
    }

    public boolean body_condition() {
        return switch (curToken.getType()) {
            case "VAR", "IF", "FOR", "WHILE", "DO", "PRINT" -> true;
            default -> false;
        };
    }

    public boolean body_condition_do_while() {
        return switch (curToken.getType()) {
            case "VAR", "IF", "FOR", "DO", "PRINT" -> true;
            default -> false;
        };
    }

    public void lang() throws ParserException {
        for (int i = 0; i < len; i++) {
            curLine++;
            expr();
        }
    }

    public void expr() {
        body();
        terminalCheck("ENDL");
    }

    public void body() {
        switch (curToken.getType()) {
            case "VAR" -> {
                if (Objects.equals(tokens.get(iterator + 1).getType(), "POINT")) {
                    list_op();
                } else {
                    expr_assign();
                }
            }
            case "IF" -> if_op();
            case "WHILE" -> while_op();
            case "DO" -> do_while_op();
            case "FOR" -> for_op();
            case "PRINT" -> print();
            case "LIST" -> list_initialize();
            default -> terminalCheck("VAR");
        }
    }

    public void expr_value() {
        switch (curToken.getType()) {
            case "VAR", "DIGIT" -> value();
            case "L_BC" -> infinity();
            default -> terminalCheck("VAR");
        }
        while ("OP".equals(curToken.getType())) {
            terminalCheck("OP");
            value();
        }
    }

    public void value() {
        switch (curToken.getType()) {
            case "DIGIT" -> terminalCheck("DIGIT");
            case "L_BC" -> infinity();
            default -> terminalCheck("VAR");
        }
    }

    public void infinity() {
        terminalCheck("L_BC");
        expr_value();
        terminalCheck("R_BC");
    }

    public void condition() {
        terminalCheck("VAR");
        terminalCheck("COMPARE_OP");
        expr_value();
    }

    public void condition_in_br() {
        terminalCheck("L_BC");
        condition();
        terminalCheck("R_BC");
    }

    public void if_op() {
        terminalCheck("IF");
        condition_in_br();
        do {
            body();
        } while (body_condition());
        if ("ELSE".equals(curToken.getType())) {
            else_op();
        }
    }

    public void else_op() {
        terminalCheck("ELSE");
        do {
            expr();
        } while (body_condition());
    }

    public void while_op() {
        terminalCheck("WHILE");
        condition_in_br();
        do {
            body();
        } while (body_condition());
    }

    public void do_while_op() {
        terminalCheck("DO");
        do {
            body();
        } while (body_condition_do_while());
        terminalCheck("WHILE");
        condition_in_br();
    }

    public void for_op() {
        terminalCheck("FOR");
        terminalCheck("L_BC");
        assign();
        terminalCheck("DIV");
        condition();
        terminalCheck("DIV");
        assign();
        terminalCheck("R_BC");
        do {
            body();
        } while (body_condition());
    }

    public void assign() {
        terminalCheck("VAR");
        terminalCheck("ASSIGN_OP");
        expr_value();
    }

    public void expr_assign() {
        assign();
        while ("DIV".equals(curToken.getType())) {
            terminalCheck("DIV");
            assign();
        }
    }

    public void print() {
        terminalCheck("PRINT");
        if ("L_BC".equals(curToken.getType())) {
            terminalCheck("L_BC");
            if ("DIGIT".equals(curToken.getType())) {
                terminalCheck("DIGIT");
            } else {
                terminalCheck("VAR");
            }
            terminalCheck("R_BC");
        }
    }

    public void list_initialize() {
        terminalCheck("LIST");
        terminalCheck("VAR");
    }

    public void list_op() {
        terminalCheck("VAR");
        terminalCheck("POINT");
        switch (curToken.getType()) {
            case "REMOVE" -> terminalCheck("REMOVE");
            case "CLEAR" -> terminalCheck("CLEAR");
            case "SIZE" -> terminalCheck("SIZE");
            case "GET" -> terminalCheck("GET");
            case "ISEMPTY" -> terminalCheck("ISEMPTY");
            case "CONTAINS" -> terminalCheck("CONTAINS");
            default -> terminalCheck("ADD");
        }
        terminalCheck("L_BC");
        if ("DIGIT".equals(curToken.getType())) {
            terminalCheck("DIGIT");
        }
        terminalCheck("R_BC");
    }
}