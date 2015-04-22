package ltl2rabin;

import org.apache.commons.collections4.set.ListOrderedSet;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public abstract class Automaton<T, U> {
    private ListOrderedSet<State> states = new ListOrderedSet<>();
    private State initialState;

    // TODO: Consider creating a protected constructor which can be called from subclasses via "super"

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

    public abstract class Transition {
        private State from;
        private State to;
        private U letter;

        protected Transition(State from, U letter, State to) {
            this.from = from;
            this.to = to;
            this.letter = letter;
        }
    }
}
