package ltl2rabin;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public abstract class Automaton<T, U> {
    private ListOrderedSet<State> states = new ListOrderedSet<>();
    private State initialState;

    // TODO: Consider creating a protected constructor which can be called from subclasses via "super"

    public State getInitialState() {
        return initialState;
    }

    public static abstract class State<R, S> {
        public abstract State readLetter(S letter);

        @Override
        public abstract boolean equals(Object obj);

        @Override
        public abstract int hashCode();
    }

    public static class Transition<P extends State, Q> {
        private final P from;
        private final P to;
        private final Q letter;

        protected Transition(P from, Q letter, P to) {
            this.from = from;
            this.to = to;
            this.letter = letter;
        }

        public P getFrom() {
            return from;
        }

        public P getTo() {
            return to;
        }

        public Q getLetter() {
            return letter;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Transition that = (Transition) o;
            return Objects.equals(from, that.from) &&
                    Objects.equals(to, that.to) &&
                    Objects.equals(letter, that.letter);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to, letter);
        }
    }
}
