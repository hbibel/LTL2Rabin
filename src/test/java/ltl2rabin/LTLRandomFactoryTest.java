package ltl2rabin;

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
            LTLFormula f = factory.buildLTL(i).getLtlFormula();
            HashSet<String> distinctVariables = new HashSet<>();
            int operatorCount = 0;
            Queue<LTLFormula> toBeAnalyzed = new LinkedList<>();
            toBeAnalyzed.add(f);
            while (!toBeAnalyzed.isEmpty()) {
                LTLFormula temp = toBeAnalyzed.poll();
                ++operatorCount;
                if (temp instanceof LTLBoolean) {
                    continue;
                }
                if (temp instanceof LTLVariable) {
                    distinctVariables.add(((LTLVariable) temp).getValue());
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
        LTLFormula f = factory.buildLTL(10, true).getLtlFormula();
        Queue<LTLFormula> toBeAnalyzed = new LinkedList<>();
        toBeAnalyzed.add(f);
        while (!toBeAnalyzed.isEmpty()) {
            LTLFormula temp = toBeAnalyzed.poll();
            if (temp instanceof LTLBoolean || temp instanceof LTLVariable) continue;
            assertFalse("f should not be instance of LTLGOperator", temp instanceof LTLGOperator);
            getChildren(temp).forEach(toBeAnalyzed::add);
        }
    }

    List<LTLFormula> getChildren(LTLFormula f) throws Exception {
        List<LTLFormula> result = new ArrayList<>();
        if(f instanceof LTLAnd) {
            Iterator<LTLFormula> it = ((LTLAnd)f).getIterator();
            while (it.hasNext()) {
                result.add(it.next());
            }
        }
        else if(f instanceof LTLFOperator) {
            result.add(((LTLFOperator) f).getOperand());
        }
        else if(f instanceof LTLGOperator) {
            result.add(((LTLGOperator) f).getOperand());
        }
        else if(f instanceof LTLOr) {
            Iterator<LTLFormula> it = ((LTLOr) f).getIterator();
            while (it.hasNext()) {
                result.add(it.next());
            }
        }
        else if (f instanceof LTLUOperator) {
            result.add(((LTLUOperator) f).getLeft());
            result.add(((LTLUOperator) f).getRight());
        }
        else if (f instanceof LTLXOperator) {
            result.add(((LTLXOperator) f).getOperand());
        }
        else {
            throw new Exception("Error in test: getChildren() called on a LTLFormula without children!");
        }
        return result;
    }
}