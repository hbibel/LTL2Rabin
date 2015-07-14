package ltl2rabin;

import java.util.*;

/**
 * This is the abstract base class for all automata. It contains the abstract base classes
 * {@link ltl2rabin.Automaton.State} and {@link ltl2rabin.Automaton.Transition}. Automata have in common that they have
 * a transition system (states + transitions) and their states are labeled. Acceptance conditions are handled by the
 * subclasses of this class.
 *
 * <p><b>Note:</b> This class only describes deterministic automata.
 * @param <T> The type of the labels of the states.
 * @param <U> The type of the letters the automaton runs on.
 */
public abstract class Automaton<T, U> {


    /**
     * The abstract base class for all states. A state is an object that has a label and can transition into another
     * state of the same type by reading a letter.
     *
     * @param <R> The type of the labels of the states.
     * @param <S> The type of the letters the automaton runs on.
     */
    public static abstract class State<R, S> {
        /**
         * Return the <code>State</code> of same type that is reached after "reading" a letter (usually this
         * is formally denoted by &delta;(q, &nu;).
         *
         * <p>Every <code>State</code> has outgoing transitions. A transition maps a letter to a
         * <code>State</code>. How the transitions are stored depends on the implementation
         * of {@link ltl2rabin.Automaton.State} and {@link Automaton}.
         *
         * <p><b>Note:</b> Since the base class only describes deterministic automata the transitions are deterministic.
         * @param letter    The letter (&nu;) that is consumed by the transition
         * @return          The state the letter maps to
         */
        public abstract State<R, S> readLetter(S letter);

        @Override
        public abstract boolean equals(Object obj);

        @Override
        public abstract int hashCode();
    }

    /**
     * A <code>Transition</code> represents a function (<code>State</code> &rarr; <code>Letter</code> &rarr;
     * <code>State</code>). It is merely a struct-like object containing references to two states (<i>from</i> and
     * <i>to</i>) and a letter. If <i>from</i> reads that letter, it transitions into <i>to</i>.
     *
     * @param <S> The type of states the transition goes between.
     * @param <L> The type of letter
     */
    public static class Transition<S extends State, L> {
        // TODO: Conceptional error, refactoring needed: The parameter L is unnecessary, since it already is included in the type of S

        private final S from;
        private final S to;
        private final L letter;

        /**
         * A transition represents the transition function for one state and one letter: &delta;(<i>q</i>, &nu; <i>q'</i>)
         * @param from      <i>q</i>
         * @param letter    &nu;
         * @param to        <i>q'</i>
         */
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
