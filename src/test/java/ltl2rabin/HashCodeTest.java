package ltl2rabin;

import ltl2rabin.LTL.F;
import ltl2rabin.LTL.G;
import ltl2rabin.LTL.Variable;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


// TODO: Remove or expand
public class HashCodeTest {
    Variable a;
    Variable a2;
    Variable b;

    @Before
    public void setUp() {
        a = new Variable("a");
        a2 = new Variable("a");
        b = new Variable("b");
    }

    @Test
    public void testVariableHashCode() {
        assertEquals(a.hashCode(), a2.hashCode());
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testGandF() {
        F f = new F(a);
        G g = new G(a);

        assertNotEquals(f.hashCode(), g.hashCode());
    }
}
