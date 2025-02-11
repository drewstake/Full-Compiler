public class Parser {
    public static final int OP     = 10;
    public static final int RELOP  = 11;
    public static final int LPAREN = 12;
    public static final int RPAREN = 13;
    public static final int SEMI   = 14;
    public static final int COMMA  = 15;
    public static final int INT    = 16;
    public static final int NUM    = 17;
    public static final int ID     = 18;
    public static final int PRINT  = 19;
    public static final int LBRACE = 20;
    public static final int RBRACE = 21;
    public static final int IF     = 22;
    public static final int ELSE   = 23;
    public static final int WHILE  = 24;
    public static final int VOID   = 25;
    public static final int ASSIGN = 26;
    public static final int NEQ    = 28;
    public static final int LE     = 29;
    public static final int GE     = 30;
    
    private final Lexer lexer;
    private final Compiler compiler;
    public ParserVal yylval;
    
    public Parser(java.io.Reader r, Compiler compiler) throws Exception {
        // init the parser with the reader and a compiler ref
        this.compiler = compiler;
        this.lexer = new Lexer(r, this);
    }
    
    public int yyparse() throws Exception {
        while (true) {
            int token = lexer.getToken();
            if (token == 0) {
                System.out.println("Success!");
                return 0;
            }
            if (token == -1) {
                int errLine = lexer.curLine;
                int errCol = lexer.curCol;
                System.out.println("Error! There is a lexical error at " + errLine + ":" + errCol + ".");
                return -1;
            }
            
            String attribute;
            if (yylval != null) {
                if (yylval.obj != null)
                    attribute = yylval.obj.toString();
                else if (yylval.sval != null)
                    attribute = yylval.sval;
                else
                    attribute = "";
            } else {
                attribute = "";
            }
            
            int line = lexer.curLine;
            int col = lexer.curCol;
            
            String tokenname = "";
            switch (token) {
                case ID:
                    tokenname = "ID";
                    break;
                case INT:
                    tokenname = "INT";
                    break;
                case PRINT:
                    tokenname = "PRINT";
                    break;
                case IF:
                    tokenname = "IF";
                    break;
                case ELSE:
                    tokenname = "ELSE";
                    break;
                case WHILE:
                    tokenname = "WHILE";
                    break;
                case VOID:
                    tokenname = "VOID";
                    break;
                case LPAREN:
                    tokenname = "LPAREN";
                    break;
                case RPAREN:
                    tokenname = "RPAREN";
                    break;
                case ASSIGN:
                    tokenname = "ASSIGN";
                    break;
                case OP:
                    tokenname = "OP";
                    break;
                case SEMI:
                    tokenname = "SEMI";
                    break;
                case COMMA:
                    tokenname = "COMMA";
                    break;
                case RELOP:
                case NEQ:
                case LE:
                case GE:
                    tokenname = "RELOP";
                    break;
                case LBRACE:
                    tokenname = "BEGIN";
                    break;
                case RBRACE:
                    tokenname = "END";
                    break;
                case NUM:
                    tokenname = "NUM";
                    break;
                default:
                    tokenname = "UNKNOWN";
                    break;
            }
            
            System.out.println("<" + tokenname + ", token-attr:\"" + attribute + "\", " + line + ":" + col + ">");
        }
    }
}
