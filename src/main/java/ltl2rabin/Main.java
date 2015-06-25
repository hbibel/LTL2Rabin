package ltl2rabin;

import ltl2rabin.LTL.LTLFactoryFromString;
import ltl2rabin.LTL.LTLListener;
import ltl2rabin.LTL.PropEquivalenceClass;
import ltl2rabin.parser.LTLLexer;
import ltl2rabin.parser.LTLParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        // GDRAFactory factory = new GDRAFactory();

        String input = "b | (X G (a | (X (b U c))))";
        String otherInput = "(r U s) & ((F G a) | (G F b)) & ((F G c) | (G F d)) ";
        //GDRA gdra = factory.createFrom(otherInput);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        //System.out.println(gdra.getStates().size() + " states in " + elapsedTime + "ms " +
        //        "(=" + elapsedTime / 1000 / 60 + "m " + (elapsedTime % 60000) / 1000 + "s " + (elapsedTime % 1000) +"ms)");
    }

    // Note to developer: When parsing several LTLs, don't forget to call clear() in between to clean up
    // the BDD variable space.

    private static void clear() {
        PropEquivalenceClass.clear();
    }
}
