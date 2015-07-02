package ltl2rabin;

import java.util.*;

/**
 * This is the abstract base class for all automata. It contains the abstract base classes <code>State</code>
 * and <code>Transition</code>
 * @param <T> The type of the labels of the states.
 * @param <U> The type of the letters the automaton runs on.
 */
public abstract class Automaton<T, U> {
    /**
     * The abstract base class for all states. A state is an object that has a label and can transition into another
     * state by reading a letter.
     * @param <R> The type of the labels of the states.
     * @param <S> The type of the letters the automaton runs on.
     */
    public static abstract class State<R, S> {
        public abstract State<R, S> readLetter(S letter);

        @Override
        public abstract boolean equals(Object obj);

        @Override
        public abstract int hashCode();
    }

    /**
     * A transition has two states (<i>from</i> and <i>to</i>) and a letter. If <i>from</i> reads that letter, it
     * transitions into <i>to</i>.
     * @param <S> The type of states the transition goes between.
     * @param <L> The type of letter
     */
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
