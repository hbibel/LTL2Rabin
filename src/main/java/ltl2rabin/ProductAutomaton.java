package ltl2rabin;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

// U: usually String
public class ProductAutomaton<U> {
    private Collection<State> states;

    public ProductAutomaton(Iterable<RabinAutomaton> rabinAutomata, Set alphabet) {

    }

    public Collection<State> getStates() {
        return states;
    }

    public State run(List<Set<U>> word) {
        return null;
    }

    public class State {

    }
}
