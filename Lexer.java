import java.io.Reader;

public class Lexer {
    private final Parser yyparser;
    private final DoubleBuffer db;
    public int lineno;
    public int column;
    public int tokenLine;
    public int tokenCol;
    
    public Lexer(Reader reader, Parser yyparser) throws Exception {
        this.yyparser = yyparser;
        this.db = new DoubleBuffer(reader);
        lineno = 1;
        column = 1;
    }
    
    public int Fail() {
        return -1;
    }
    
    public int yylex() throws Exception {
        while (true) {
            int cInt = db.nextChar();
            if (cInt == (char)-1)
                return 0;
            
            char c = (char) cInt;
            
            if (c == '\n') {
                lineno++;
                column = 1;
                continue;
            }
            if (c == ' ' || c == '\t' || c == '\r') {
                column++;
                continue;
            }
            
            tokenLine = lineno;
            tokenCol = column;
            
            switch (c) {
                case '+':
                    yyparser.yylval = new ParserVal("+");
                    column++;
                    return Parser.OP;
                case '-':
                    yyparser.yylval = new ParserVal("-");
                    column++;
                    return Parser.OP;
                case '*':
                    yyparser.yylval = new ParserVal("*");
                    column++;
                    return Parser.OP;
                case '/':
                    yyparser.yylval = new ParserVal("/");
                    column++;
                    return Parser.OP;
                case '(':
                    yyparser.yylval = new ParserVal("(");
                    column++;
                    return Parser.LPAREN;
                case ')':
                    yyparser.yylval = new ParserVal(")");
                    column++;
                    return Parser.RPAREN;
                case '{':
                    yyparser.yylval = new ParserVal("{");
                    column++;
                    return Parser.LBRACE;
                case '}':
                    yyparser.yylval = new ParserVal("}");
                    column++;
                    return Parser.RBRACE;
                case ';':
                    yyparser.yylval = new ParserVal(";");
                    column++;
                    return Parser.SEMI;
                case ',':
                    yyparser.yylval = new ParserVal(",");
                    column++;
                    return Parser.COMMA;
                default:
                    break;
            }
            
            if (c == '<') {
                int nextInt = db.nextChar();
                if (nextInt == (char)-1) {
                    yyparser.yylval = new ParserVal("<");
                    column++;
                    return Parser.RELOP;
                }
                char nextChar = (char) nextInt;
                if (nextChar == '-') {
                    yyparser.yylval = new ParserVal("<-");
                    column += 2;
                    return Parser.ASSIGN;
                } else if (nextChar == '=') {
                    yyparser.yylval = new ParserVal("<=");
                    column += 2;
                    return Parser.LE;
                } else if (nextChar == '>') {
                    yyparser.yylval = new ParserVal("<>");
                    column += 2;
                    return Parser.NEQ;
                } else {
                    db.unread();
                    yyparser.yylval = new ParserVal("<");
                    column++;
                    return Parser.RELOP;
                }
            }
            if (c == '>') {
                int nextInt = db.nextChar();
                if (nextInt == (char)-1) {
                    yyparser.yylval = new ParserVal(">");
                    column++;
                    return Parser.RELOP;
                }
                char nextChar = (char) nextInt;
                if (nextChar == '=') {
                    yyparser.yylval = new ParserVal(">=");
                    column += 2;
                    return Parser.GE;
                } else {
                    db.unread();
                    yyparser.yylval = new ParserVal(">");
                    column++;
                    return Parser.RELOP;
                }
            }
            if (c == '=') {
                yyparser.yylval = new ParserVal("=");
                column++;
                return Parser.RELOP;
            }
            
            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                column++;
                boolean isFloat = false;
                while (true) {
                    int peekInt = db.nextChar();
                    if (peekInt == (char)-1)
                        break;
                    char peek = (char) peekInt;
                    if (Character.isDigit(peek)) {
                        sb.append(peek);
                        column++;
                    } else if (peek == '.' && !isFloat) {
                        int afterDotInt = db.nextChar();
                        if (afterDotInt == (char)-1) {
                            yyparser.yylval = new ParserVal(sb.toString() + ".");
                            return Fail();
                        }
                        char afterDot = (char) afterDotInt;
                        if (!Character.isDigit(afterDot)) {
                            db.unread();
                            yyparser.yylval = new ParserVal(sb.toString() + ".");
                            return Fail();
                        } else {
                            isFloat = true;
                            sb.append('.');
                            column++;
                            sb.append(afterDot);
                            column++;
                        }
                    } else {
                        db.unread();
                        break;
                    }
                }
                yyparser.yylval = new ParserVal(sb.toString());
                return Parser.NUM;
            }
            
            if (Character.isLetter(c)) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                column++;
                while (true) {
                    int peekInt = db.nextChar();
                    if (peekInt == (char)-1)
                        break;
                    char peek = (char) peekInt;
                    if (Character.isLetterOrDigit(peek) || peek == '_') {
                        sb.append(peek);
                        column++;
                    } else {
                        db.unread();
                        break;
                    }
                }
                String idStr = sb.toString();
                if (idStr.equals("int")) {
                    yyparser.yylval = new ParserVal("int");
                    return Parser.INT;
                } else if (idStr.equals("print")) {
                    yyparser.yylval = new ParserVal("print");
                    return Parser.PRINT;
                } else if (idStr.equals("if")) {
                    yyparser.yylval = new ParserVal("if");
                    return Parser.IF;
                } else if (idStr.equals("else")) {
                    yyparser.yylval = new ParserVal("else");
                    return Parser.ELSE;
                } else if (idStr.equals("while")) {
                    yyparser.yylval = new ParserVal("while");
                    return Parser.WHILE;
                } else if (idStr.equals("void")) {
                    yyparser.yylval = new ParserVal("void");
                    return Parser.VOID;
                } else {
                    yyparser.yylval = new ParserVal(idStr);
                    return Parser.ID;
                }
            }
            
            yyparser.yylval = new ParserVal("Unexpected: " + c);
            column++;
            return Fail();
        }
    }
}
