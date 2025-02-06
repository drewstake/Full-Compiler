public class Parser
{
    public static final int OP         = 10;    // +  -  *  /
    public static final int RELOP      = 11;    // <  >  <=  >=  ...
    public static final int LPAREN     = 12;    // (
    public static final int RPAREN     = 13;    // )
    public static final int SEMI       = 14;    // ;
    public static final int COMMA      = 15;    // ,
    public static final int INT        = 16;    // int
    public static final int NUM        = 17;    // number
    public static final int ID         = 18;    // identifier
    public static final int PRINT      = 19;    // print
    // add additinoal token types

    Compiler         compiler;
    Lexer            lexer;     // lexer.yylex() returns token-name
    public ParserVal yylval;    // yylval contains token-attribute

    public Parser(java.io.Reader r, Compiler compiler) throws Exception
    {
        this.compiler = compiler;
        this.lexer    = new Lexer(r, this);
    }

    // 1. parser call lexer.yylex that should return (token-name, token-attribute)
    // 2. lexer
    //    a. assign token-attribute to yyparser.yylval
    //       token attribute can be lexeme, line number, colume, etc.
    //    b. return token-id defined in Parser as a token-name
    // 3. parser print the token on console
    //    if there was an error (-1) in lexer, then print error message
    // 4. repeat until EOF (0) is reached
    public int yyparse() throws Exception
    {
        while ( true )
        {
            int token = lexer.yylex();  // get next token-name
            Object attr = yylval.obj;   // get  its token-attribute
            String tokenname = "SEMI";

            if(token == 0)
            {
                // EOF is reached
                System.out.println("Success!");
                return 0;
            }
            if(token == -1)
            {
                // lexical error is found
                System.out.println("Error! There is a lexical error at " + lexer.lineno + ":" + lexer.column + ".");
                return -1;
            }

            System.out.println("<" + tokenname + ", token-attr:\"" + attr + "\", " + lexer.lineno + ":" + lexer.column + ">");
        }
    }
}
