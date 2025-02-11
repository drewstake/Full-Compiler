import java.io.Reader;

public class Lexer {
    private final Parser parser;
    private final DoubleBuffer db;
    public int lineNumber;
    public int column;
    public int curLine;
    public int curCol;
    
    public Lexer(Reader reader, Parser parser) throws Exception {
        // init the lexer with the reader and a parser reference
        this.parser = parser;
        this.db = new DoubleBuffer(reader);
        lineNumber = 1;
        column = 1;
    }
    
    // simple fail function (returns error token -1)
    public int failLex() {
        return -1;
    }
    
    // helper: if the given string is a keyword, return its token; otherwise, return id.
    private int keywordOrId(String idStr) {
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
    
    // gettoken() reimplemented using states.
    public int getToken() throws Exception {
        // define state constants for clarity.
        final int Q0   = 0;  // initial state
        final int Q1   = 1;  // accepting single-character tokens
        final int Q2   = 2;  // relational operator state (<, >, or =)
        final int Q3   = 3;  // number literal state
        final int Q4   = 4;  // identifier state
        final int QEOF = 5;  // end-of-file
        
        int state = Q0;
        StringBuilder lexeme = new StringBuilder();
        // used in q3 to allow only one dot in a number
        boolean dotSeen = false;
        
        while (true) {
            switch (state) {
                case Q0:
                    // initial state: get next non-whitespace character.
                    int cInt = db.getNextChar();
                    if (cInt == (char) -1) {
                        state = QEOF;
                        break;
                    }
                    char c = (char) cInt;
                    
                    // skip whitespace (except newline) and update column.
                    if (c == ' ' || c == '\t' || c == '\r') {
                        column++;
                        continue;
                    }
                    if (c == '\n') {
                        lineNumber++;
                        column = 1;
                        continue;
                    }
                    
                    // set starting position for this token.
                    curLine = lineNumber;
                    curCol = column;
                    
                    // single-character symbols that are accepted immediately.
                    if (c == ';' || c == '+' || c == '-' || c == '*' || c == '/' ||
                        c == '(' || c == ')' || c == '{' || c == '}' || c == ',') {
                        lexeme.append(c);
                        column++;
                        state = Q1; // move to an accepting state for single-character tokens
                        break;
                    }
                    
                    // if the char is one of the relational operator starts.
                    if (c == '<' || c == '>' || c == '=') {
                        lexeme.append(c);
                        column++;
                        state = Q2;
                        break;
                    }
                    
                    // if the char is a digit, begin a number literal.
                    if (Character.isDigit(c)) {
                        lexeme.append(c);
                        column++;
                        state = Q3;
                        break;
                    }
                    
                    // if the char is a letter, begin an identifier.
                    if (Character.isLetter(c)) {
                        lexeme.append(c);
                        column++;
                        state = Q4;
                        break;
                    }
                    
                    // otherwise, report an unexpected character.
                    lexeme.append(c);
                    column++;
                    parser.yylval = new ParserVal("unexpected: " + c);
                    return failLex();
                    
                case Q1:
                    // q1: accepting state for single-character tokens.
                    char tokenChar = lexeme.charAt(0);
                    switch (tokenChar) {
                        case '+': parser.yylval = new ParserVal("+"); return Parser.OP;
                        case '-': parser.yylval = new ParserVal("-"); return Parser.OP;
                        case '*': parser.yylval = new ParserVal("*"); return Parser.OP;
                        case '/': parser.yylval = new ParserVal("/"); return Parser.OP;
                        case '(': parser.yylval = new ParserVal("("); return Parser.LPAREN;
                        case ')': parser.yylval = new ParserVal(")"); return Parser.RPAREN;
                        case '{': parser.yylval = new ParserVal("{"); return Parser.LBRACE;
                        case '}': parser.yylval = new ParserVal("}"); return Parser.RBRACE;
                        case ';': parser.yylval = new ParserVal(";"); return Parser.SEMI;
                        case ',': parser.yylval = new ParserVal(","); return Parser.COMMA;
                        default:
                            parser.yylval = new ParserVal("unknown token: " + tokenChar);
                            return failLex();
                    }
                    
                case Q2:
                    // q2: relational operator state.
                    // the first character is already in lexeme.
                    char first = lexeme.charAt(0);
                    if (first == '=') {
                        // no lookahead needed for "=".
                        parser.yylval = new ParserVal("=");
                        return Parser.RELOP;
                    } else if (first == '<') {
                        int nextInt = db.getNextChar();
                        if (nextInt == (char) -1) {
                            parser.yylval = new ParserVal("<");
                            return Parser.RELOP;
                        }
                        char next = (char) nextInt;
                        if (next == '-') {
                            lexeme.append(next);
                            column++;
                            parser.yylval = new ParserVal(lexeme.toString());
                            return Parser.ASSIGN;
                        } else if (next == '=') {
                            lexeme.append(next);
                            column++;
                            parser.yylval = new ParserVal(lexeme.toString());
                            return Parser.LE;
                        } else if (next == '>') {
                            lexeme.append(next);
                            column++;
                            parser.yylval = new ParserVal(lexeme.toString());
                            return Parser.NEQ;
                        } else {
                            db.goBack();
                            parser.yylval = new ParserVal("<");
                            return Parser.RELOP;
                        }
                    } else if (first == '>') {
                        int nextInt = db.getNextChar();
                        if (nextInt == (char) -1) {
                            parser.yylval = new ParserVal(">");
                            return Parser.RELOP;
                        }
                        char next = (char) nextInt;
                        if (next == '=') {
                            lexeme.append(next);
                            column++;
                            parser.yylval = new ParserVal(lexeme.toString());
                            return Parser.GE;
                        } else {
                            db.goBack();
                            parser.yylval = new ParserVal(">");
                            return Parser.RELOP;
                        }
                    }
                    break;
                    
                case Q3:
                    // q3: number literal state.
                    int peekInt = db.getNextChar();
                    if (peekInt == (char) -1) {
                        parser.yylval = new ParserVal(lexeme.toString());
                        return Parser.NUM;
                    }
                    char peek = (char) peekInt;
                    if (Character.isDigit(peek)) {
                        lexeme.append(peek);
                        column++;
                    } else if (peek == '.' && !dotSeen) {
                        // lookahead: ensure a digit follows the dot.
                        int afterDotInt = db.getNextChar();
                        if (afterDotInt == (char) -1) {
                            lexeme.append('.');
                            parser.yylval = new ParserVal(lexeme.toString());
                            return failLex();
                        }
                        char afterDot = (char) afterDotInt;
                        if (!Character.isDigit(afterDot)) {
                            db.goBack();
                            lexeme.append('.');
                            parser.yylval = new ParserVal(lexeme.toString());
                            return failLex();
                        }
                        dotSeen = true;
                        lexeme.append('.');
                        lexeme.append(afterDot);
                        column += 2;
                    } else {
                        // not a digit or valid dot -> finish number.
                        db.goBack();
                        parser.yylval = new ParserVal(lexeme.toString());
                        return Parser.NUM;
                    }
                    break;
                    
                case Q4:
                    // q4: identifier state.
                    int peekInt2 = db.getNextChar();
                    if (peekInt2 == (char) -1) {
                        String idStr = lexeme.toString();
                        return keywordOrId(idStr);
                    }
                    char peek2 = (char) peekInt2;
                    if (Character.isLetterOrDigit(peek2) || peek2 == '_') {
                        lexeme.append(peek2);
                        column++;
                    } else {
                        db.goBack();
                        String idStr = lexeme.toString();
                        return keywordOrId(idStr);
                    }
                    break;
                    
                case QEOF:
                    // end-of-file: return token 0.
                    return 0;
            } // end switch
        } // end while
    }
}
