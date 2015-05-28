package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.parser.LTLLexer;
import ltl2rabin.parser.LTLParser;
import net.sf.javabdd.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;


// Note to self: When parsing several LTLs, don't forget to call LTLVariable.cachedBDDs.clear() in between to clean up
// the variable space.

// TODO: Let all factories take alphabet in their constructor
public class Main {

    public static void main(String[] args) {
        System.out.println("This program runs, but does not do anything useful yet.");
    }

    public static LTLListener stringToLTLFormula (String s) {
        ANTLRInputStream input = new ANTLRInputStream(s);
        LTLLexer lexer = new LTLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LTLParser parser = new LTLParser(tokens);
        ParserRuleContext tree = parser.formula();

        ParseTreeWalker walker = new ParseTreeWalker();
        LTLListener extractor = new LTLListener(parser);
        walker.walk(extractor, tree);

        return extractor;
    }
}
