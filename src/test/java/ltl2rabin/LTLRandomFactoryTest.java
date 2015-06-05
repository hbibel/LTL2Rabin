package ltl2rabin;

import ltl2rabin.LTL.*;
import ltl2rabin.LTL.Boolean;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertFalse;

public class LTLRandomFactoryTest {
    LTLRandomFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new LTLRandomFactory();
    }

    @Test
    public void testSize() throws Exception {
        System.out.println("i  | operatorCount | variableCount ");
        System.out.println("-----------------------------------");
        for (int i = 0; i < 11; i++) {
            Formula f = factory.buildLTL(i).getLtlFormula();
            HashSet<String> distinctVariables = new HashSet<>();
            int operatorCount = 0;
            Queue<Formula> toBeAnalyzed = new LinkedList<>();
            toBeAnalyzed.add(f);
            while (!toBeAnalyzed.isEmpty()) {
                Formula temp = toBeAnalyzed.poll();
                ++operatorCount;
                if (temp instanceof Boolean) {
                    continue;
                }
                if (temp instanceof Variable) {
                    distinctVariables.add(((Variable) temp).getValue());
                    continue;
                }
                getChildren(temp).forEach(toBeAnalyzed::add);
            }
            int variableCount = distinctVariables.size();
            System.out.println(padRight(""+i, 3) + "| " + padRight(""+operatorCount, 14) + "| " + padRight(""+variableCount, 14));
        }
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    @Test
    public void testGFreeNess() throws Exception {
        Formula f = factory.buildLTL(10, true).getLtlFormula();
        Queue<Formula> toBeAnalyzed = new LinkedList<>();
        toBeAnalyzed.add(f);
        while (!toBeAnalyzed.isEmpty()) {
            Formula temp = toBeAnalyzed.poll();
            if (temp instanceof Boolean || temp instanceof Variable) continue;
            assertFalse("f should not be instance of G", temp instanceof G);
            getChildren(temp).forEach(toBeAnalyzed::add);
        }
    }

    List<Formula> getChildren(Formula f) throws Exception {
        List<Formula> result = new ArrayList<>();
        if(f instanceof And) {
            Iterator<Formula> it = ((And)f).getIterator();
            while (it.hasNext()) {
                result.add(it.next());
            }
        }
        else if(f instanceof F) {
            result.add(((F) f).getOperand());
        }
        else if(f instanceof G) {
            result.add(((G) f).getOperand());
        }
        else if(f instanceof Or) {
            Iterator<Formula> it = ((Or) f).getIterator();
            while (it.hasNext()) {
                result.add(it.next());
            }
        }
        else if (f instanceof U) {
            result.add(((U) f).getLeft());
            result.add(((U) f).getRight());
        }
        else if (f instanceof X) {
            result.add(((X) f).getOperand());
        }
        else {
            throw new Exception("Error in test: getChildren() called on a Formula without children!");
        }
        return result;
    }
}