package ltl2rabin;

import ltl2rabin.parser.LTLLexer;
import ltl2rabin.parser.LTLParser;
import net.sf.javabdd.BDDFactory;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.HashSet;


// Note to self: When parsing several LTLs, don't forget to call LTLVariable.cachedBDDs.clear() in between to clean up
// the variable space.
public class Main {
    protected static int bddVarCount = 0;
    protected static final BDDFactory bddFactory = BDDFactory.init("java", 2, 2);

    public static void main(String[] args) {
        System.out.println("This program runs, but does not do anything useful yet.");
        LTLListener ltlListener = Main.stringToLTLFormula("a | (b U c)");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton<LTLFormula, String> mojmirAutomaton = new MojmirAutomaton<>(ltlOr, new AfFunction(), alphabet);

        System.out.println("");

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
