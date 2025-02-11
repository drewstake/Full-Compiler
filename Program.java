public class Program {
    public static void main(String[] args) throws Exception {
        java.io.Reader r;

        if (args.length <= 0)
            return;
        r = new java.io.FileReader(args[0]);

        Compiler compiler = new Compiler(r);
        compiler.Compile();
    }
}
