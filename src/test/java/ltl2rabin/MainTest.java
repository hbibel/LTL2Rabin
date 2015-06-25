package ltl2rabin;

import org.junit.Test;

import static org.junit.Assert.*;

public class MainTest {

    @Test
    public void testMain() throws Exception {
        Main.main(new String[] {"-h"});
        Main.main(new String[] {"-f G (a & b)"});
    }
}