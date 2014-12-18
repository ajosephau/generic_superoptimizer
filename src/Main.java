import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.dgso.antlrv4parser.*;

import java.io.FileInputStream;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) throws Exception {
        // create a CharStream that reads from standard input
        String inputFile = null;
        if ( args.length>0 ) inputFile = args[0];
        InputStream is = System.in;
        if ( inputFile!=null ) is = new FileInputStream(inputFile);
        ANTLRInputStream input = new ANTLRInputStream(is);
        ANTLRv4Lexer lexer = new ANTLRv4Lexer(input); // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer); // create a parser that feeds off the tokens buffer
        ANTLRv4Parser parser = new ANTLRv4Parser(tokens);
        ParseTree tree = parser.grammarSpec();
        ANTLRv4Visitor av = new ANTLRv4Visitor();
        av.visit(tree);
        System.out.println(tree.toStringTree(parser)); // print LISP-style tree
    }
}
