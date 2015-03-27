package ltl2rabin;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LTLFactoryFromStringTest {
    LTLFactoryFromString factory;
    LTLRandomFactory randomFactory;

    @Before
    public void setUp() throws Exception {
        factory = new LTLFactoryFromString();
        randomFactory = new LTLRandomFactory();
    }

    @Test
    public void testAgainstRandom() {
        for (int i = 0; i < 5; i++) {
            LTLFormula randomFormula = randomFactory.buildLTL(6);
            LTLFormula generatedFormula = factory.buildLTL(randomFormula.toString());

            assertEquals(randomFormula, generatedFormula);
        }
    }
}