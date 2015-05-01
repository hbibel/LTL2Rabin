package ltl2rabin;


import java.util.Collection;
import java.util.Set;

public class GDRAFactory extends RabinAutomatonFactory<LTLFormula,
        Collection<RabinAutomaton.State<?, Set<String>>>,
        Set<String>> {
    public RabinAutomaton<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>> createFrom(LTLFormula ltlFormula) {
        // TODO
        return null;
    }
}
