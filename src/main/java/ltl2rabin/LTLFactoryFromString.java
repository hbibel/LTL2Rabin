package ltl2rabin;

public class LTLFactoryFromString extends LTLFactoryWithAntlr<String> {
    @Override
    protected String getInputString(String input) {
        return input;
    }
}
