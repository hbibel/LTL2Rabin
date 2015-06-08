package ltl2rabin;

import org.apache.commons.collections4.set.ListOrderedSet;

import java.util.*;

public abstract class Automaton<T, U> {
    private State initialState;

    public State getInitialState() {
        return initialState;
    }

    public static abstract class State<R, S> {
        public abstract State<R, S> readLetter(S letter);

        @Override
        public abstract boolean equals(Object obj);

        @Override
        public abstract int hashCode();
    }

    public static class Transition<S extends State, L> {
        private final S from;
        private final S to;
        private final L letter;

        protected Transition(S from, L letter, S to) {
            this.from = from;
            this.to = to;
            this.letter = letter;
        }

        public S getFrom() {
            return from;
        }

        public S getTo() {
            return to;
        }

        public L getLetter() {
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
