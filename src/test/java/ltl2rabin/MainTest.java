package ltl2rabin;

import org.junit.Assert;

public class MainTest {

    @org.junit.Test
    public void testAdd() throws Exception {
        Assert.assertEquals(Main.add(1, 2), 3);
    }
}