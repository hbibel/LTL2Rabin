package ltl2rabin;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class HashCodeTest {
    LTLVariable a;
    LTLVariable a2;
    LTLVariable b;

    @Before
    public void setUp() {
        a = new LTLVariable("a");
        a2 = new LTLVariable("a");
        b = new LTLVariable("b");
    }

    @Test
    public void testVariableHashCode() {
        assertEquals(a.hashCode(), a2.hashCode());
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testGandF() {
        LTLFOperator f = new LTLFOperator(a);
        LTLGOperator g = new LTLGOperator(a);

        assertNotEquals(f.hashCode(), g.hashCode());
    }
}
