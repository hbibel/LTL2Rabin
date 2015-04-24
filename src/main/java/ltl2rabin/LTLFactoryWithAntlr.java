package ltl2rabin;

import com.google.common.collect.Sets;
import ltl2rabin.parser.LTLLexer;
import ltl2rabin.parser.LTLParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.Set;

public abstract class LTLFactoryWithAntlr<T> extends LTLFactory<T> {
    protected abstract String getInputString(T input);

    @Override
    public Pair<LTLFormula, Set<Set<String>>> buildLTL(T input) {
        String inputString = getInputString(input);
        ANTLRInputStream antlrInputStream = new ANTLRInputStream(inputString);
        LTLLexer lexer = new LTLLexer(antlrInputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LTLParser parser = new LTLParser(tokens);
        ParserRuleContext tree = parser.formula();
        ParseTreeWalker walker = new ParseTreeWalker();
        LTLListener extractor = new LTLListener(parser);
        walker.walk(extractor, tree);
        return new Pair<>(extractor.getLtlTree(), Sets.powerSet(extractor.getTerminalSymbols()));
    }
}
