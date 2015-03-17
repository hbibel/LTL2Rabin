package ltl2rabin;

import ltl2rabin.parser.LTLLexer;
import ltl2rabin.parser.LTLParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.HashSet;

public class Main {
    public static HashSet<String> stringSet = new HashSet<String>();

    public static void main(String[] args) {
        stringSet.add(new String("Giraffe"));
        boolean shouldBeTrue = stringSet.add(new String("Elephant"));
        boolean shouldBeFalse = stringSet.add(new String("Giraffe"));
        System.out.println(shouldBeTrue + " == true");
        System.out.println(shouldBeFalse + " == false");
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
