package ltl2rabin;

import org.apache.commons.collections4.set.ListOrderedSet;

import java.util.HashMap;
import java.util.Map;

public abstract class Automaton<T, U> {
    private ListOrderedSet<State> states = new ListOrderedSet<>();
    private State initialState;

    public State getInitialState() {
        return initialState;
    }

    public abstract class State {
        public abstract State readLetter(U letter);

        @Override
        public abstract boolean equals(Object obj);

        @Override
        public abstract int hashCode();
    }
}
