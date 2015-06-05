package ltl2rabin.LTL;

public class LTLFactoryFromString extends LTLFactoryWithAntlr<String> {
    @Override
    protected String getInputString(String input) {
        return input;
    }
}
