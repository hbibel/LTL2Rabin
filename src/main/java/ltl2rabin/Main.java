package ltl2rabin;

import ltl2rabin.LTL.LTLListener;
import ltl2rabin.parser.LTLLexer;
import ltl2rabin.parser.LTLParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;
import java.util.List;


// Note to self: When parsing several LTLs, don't forget to call Variable.cachedBDDs.clear() in between to clean up
// the variable space.

public class Main {

    public static void main(String[] args) {
        System.out.println("This program runs, but does not do anything useful yet.");
    }
}
