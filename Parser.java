public class Parser {
    // Token definitions – these numbers must match your assignment.
    public static final int OP     = 10;    // +, -, *, /
    public static final int RELOP  = 11;    // relational operators (<, >, =, <>, <=, >=)
    public static final int LPAREN = 12;    // (
    public static final int RPAREN = 13;    // )
    public static final int SEMI   = 14;    // ;
    public static final int COMMA  = 15;    // ,
    public static final int INT    = 16;    // "int"
    public static final int NUM    = 17;    // number literal
    public static final int ID     = 18;    // identifier
    public static final int PRINT  = 19;    // "print"
    public static final int LBRACE = 20;    // {
    public static final int RBRACE = 21;    // }
    // Additional keywords:
    public static final int IF     = 22;    // "if"
    public static final int ELSE   = 23;    // "else"
    public static final int WHILE  = 24;    // "while"
    public static final int VOID   = 25;    // "void"
    public static final int ASSIGN = 26;    // "<-"
    public static final int NEQ    = 28;    // "<>"
    public static final int LE     = 29;    // "<="
    public static final int GE     = 30;    // ">="
    
    private final Lexer lexer;
    private final Compiler compiler;
    public ParserVal yylval; // set by the lexer
    
    public Parser(java.io.Reader r, Compiler compiler) throws Exception {
        this.compiler = compiler;
        this.lexer = new Lexer(r, this);
    }
    
    public int yyparse() throws Exception {
        while (true) {
            int token = lexer.yylex();
            if (token == 0) {
                System.out.println("Success!");
                return 0;
            }
            if (token == -1) {
                int errLine = lexer.tokenLine;
                int errCol = lexer.tokenCol;
                System.out.println("Error! There is a lexical error at " + errLine + ":" + errCol + ".");
                return -1;
            }
            
            // Retrieve the token attribute: use obj if non-null; otherwise use sval.
            String attr;
            if (yylval != null) {
                if (yylval.obj != null)
                    attr = yylval.obj.toString();
                else if (yylval.sval != null)
                    attr = yylval.sval;
                else
                    attr = "";
            } else {
                attr = "";
            }
            
            int line = lexer.tokenLine;
            int col = lexer.tokenCol;
            
            String tokenname = switch (token) {
                case ID -> "ID";
                case INT -> "INT";
                case PRINT -> "PRINT";
                case IF -> "IF";
                case ELSE -> "ELSE";
                case WHILE -> "WHILE";
                case VOID -> "VOID";
                case LPAREN -> "LPAREN";
                case RPAREN -> "RPAREN";
                case ASSIGN -> "ASSIGN";
                case OP -> "OP";
                case SEMI -> "SEMI";
                case COMMA -> "COMMA";
                // Map all relational operator tokens to "RELOP"
                case RELOP, NEQ, LE, GE -> "RELOP";
                case LBRACE -> "BEGIN";
                case RBRACE -> "END";
                case NUM -> "NUM";
                default -> "UNKNOWN";
            };
            
            System.out.println("<" + tokenname + ", token-attr:\"" + attr + "\", " + line + ":" + col + ">");
        }
    }
}
