import java.io.Reader;

public class Lexer {
    private final Parser parser;
    private final DoubleBuffer db;
    public int lineNumber;
    public int column;
    public int curLine;
    public int curCol;
    
    public Lexer(Reader reader, Parser parser) throws Exception {
        // init the lexer with our reader and a parser reference
        this.parser = parser;
        this.db = new DoubleBuffer(reader);
        lineNumber = 1;
        column = 1;
    }
    
    // simple fail function
    public int failLex() {
        return -1;
    }
    
    // get the next token from input
    public int getToken() throws Exception {
        while (true) {
            int cInt = db.getNextChar();
            if (cInt == (char) -1)
                return 0;
            
            char c = (char) cInt;
            
            // handle newline
            if (c == '\n') {
                lineNumber++;
                column = 1;
                continue;
            }
            // skip whitespace characters
            if (c == ' ' || c == '\t' || c == '\r') {
                column++;
                continue;
            }
            
            curLine = lineNumber;
            curCol = column;
            
            switch (c) {
                case '+':
                    parser.yylval = new ParserVal("+");
                    column++;
                    return Parser.OP;
                case '-':
                    parser.yylval = new ParserVal("-");
                    column++;
                    return Parser.OP;
                case '*':
                    parser.yylval = new ParserVal("*");
                    column++;
                    return Parser.OP;
                case '/':
                    parser.yylval = new ParserVal("/");
                    column++;
                    return Parser.OP;
                case '(':
                    parser.yylval = new ParserVal("(");
                    column++;
                    return Parser.LPAREN;
                case ')':
                    parser.yylval = new ParserVal(")");
                    column++;
                    return Parser.RPAREN;
                case '{':
                    parser.yylval = new ParserVal("{");
                    column++;
                    return Parser.LBRACE;
                case '}':
                    parser.yylval = new ParserVal("}");
                    column++;
                    return Parser.RBRACE;
                case ';':
                    parser.yylval = new ParserVal(";");
                    column++;
                    return Parser.SEMI;
                case ',':
                    parser.yylval = new ParserVal(",");
                    column++;
                    return Parser.COMMA;
                default:
                    break;
            }
            
            if (c == '<') {
                int nextInt = db.getNextChar();
                if (nextInt == (char) -1) {
                    parser.yylval = new ParserVal("<");
                    column++;
                    return Parser.RELOP;
                }
                char nextCh = (char) nextInt;
                if (nextCh == '-') {
                    parser.yylval = new ParserVal("<-");
                    column += 2;
                    return Parser.ASSIGN;
                } else if (nextCh == '=') {
                    parser.yylval = new ParserVal("<=");
                    column += 2;
                    return Parser.LE;
                } else if (nextCh == '>') {
                    parser.yylval = new ParserVal("<>");
                    column += 2;
                    return Parser.NEQ;
                } else {
                    db.goBack();
                    parser.yylval = new ParserVal("<");
                    column++;
                    return Parser.RELOP;
                }
            }
            if (c == '>') {
                int nextInt = db.getNextChar();
                if (nextInt == (char) -1) {
                    parser.yylval = new ParserVal(">");
                    column++;
                    return Parser.RELOP;
                }
                char nextCh = (char) nextInt;
                if (nextCh == '=') {
                    parser.yylval = new ParserVal(">=");
                    column += 2;
                    return Parser.GE;
                } else {
                    db.goBack();
                    parser.yylval = new ParserVal(">");
                    column++;
                    return Parser.RELOP;
                }
            }
            if (c == '=') {
                parser.yylval = new ParserVal("=");
                column++;
                return Parser.RELOP;
            }
            
            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                column++;
                boolean isFloat = false;
                while (true) {
                    int peekInt = db.getNextChar();
                    if (peekInt == (char) -1)
                        break;
                    char peek = (char) peekInt;
                    if (Character.isDigit(peek)) {
                        sb.append(peek);
                        column++;
                    } else if (peek == '.' && !isFloat) {
                        int afterDotInt = db.getNextChar();
                        if (afterDotInt == (char) -1) {
                            parser.yylval = new ParserVal(sb.toString() + ".");
                            return failLex();
                        }
                        char afterDot = (char) afterDotInt;
                        if (!Character.isDigit(afterDot)) {
                            db.goBack();
                            parser.yylval = new ParserVal(sb.toString() + ".");
                            return failLex();
                        } else {
                            isFloat = true;
                            sb.append('.');
                            column++;
                            sb.append(afterDot);
                            column++;
                        }
                    } else {
                        db.goBack();
                        break;
                    }
                }
                parser.yylval = new ParserVal(sb.toString());
                return Parser.NUM;
            }
            
            if (Character.isLetter(c)) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                column++;
                while (true) {
                    int peekInt = db.getNextChar();
                    if (peekInt == (char) -1)
                        break;
                    char peek = (char) peekInt;
                    if (Character.isLetterOrDigit(peek) || peek == '_') {
                        sb.append(peek);
                        column++;
                    } else {
                        db.goBack();
                        break;
                    }
                }
                String idStr = sb.toString();
                if (idStr.equals("int")) {
                    parser.yylval = new ParserVal("int");
                    return Parser.INT;
                } else if (idStr.equals("print")) {
                    parser.yylval = new ParserVal("print");
                    return Parser.PRINT;
                } else if (idStr.equals("if")) {
                    parser.yylval = new ParserVal("if");
                    return Parser.IF;
                } else if (idStr.equals("else")) {
                    parser.yylval = new ParserVal("else");
                    return Parser.ELSE;
                } else if (idStr.equals("while")) {
                    parser.yylval = new ParserVal("while");
                    return Parser.WHILE;
                } else if (idStr.equals("void")) {
                    parser.yylval = new ParserVal("void");
                    return Parser.VOID;
                } else {
                    parser.yylval = new ParserVal(idStr);
                    return Parser.ID;
                }
            }
            
            // unexpected char here
            parser.yylval = new ParserVal("Unexpected: " + c);
            column++;
            return failLex();
        }
    }
}
