package ltl2rabin;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;

public abstract class RabinAutomaton<T, U extends Collection> extends Automaton<T, U> {
    private final ImmutableCollection<? extends State<T, U>> states;
    private final State<T, U> initialState;
    // private final Pair<? extends ImmutableCollection<? extends Automaton.Transition>, ? extends ImmutableCollection<? extends Automaton.Transition>> rabinPair;
    private final ImmutableSet<U> alphabet;

    public RabinAutomaton(ImmutableCollection<? extends State<T, U>> states,
                          State<T, U> initialState,
                          // Pair<? extends ImmutableCollection<? extends Transition>, ? extends ImmutableCollection<? extends Automaton.Transition>> rabinPair,
                          ImmutableSet<U> alphabet) {
        this.states = states;
        this.initialState = initialState;
        this.alphabet = alphabet;
    }

    public State<T, U> getInitialState() {
        return initialState;
    }


    /*public Pair<? extends ImmutableCollection<? extends Automaton.Transition>, ? extends ImmutableCollection<? extends Automaton.Transition>> getRabinPair() {
        return rabinPair;
    }*/

    public ImmutableCollection<? extends State<T, U>> getStates() {
        return states;
    }

    public ImmutableSet<? extends U> getAlphabet() {
        return alphabet;
    }

    public State<T, U> run(List<U> word) {
        State<T, U> result = initialState;
        Iterator<U> letterIterator = word.iterator();
        while (letterIterator.hasNext()) {
            U nextLetter = letterIterator.next();
            result = result.readLetter(nextLetter);
        }
        return result;
    }

    public static class State<R, S> extends Automaton.State<R, S> {
        private final R label;
        private Map<S, State<R, S>> transitions = new HashMap<>();

        public void setTransition(S letter, State<R, S> to) {
            transitions.put(letter, to);
        }

        public Map<S, State<R, S>> getTransitions() {
            return transitions;
        }

        public R getLabel() {
            return label;
        }

        public State<R, S> readLetter(S letter) {
            return transitions.get(letter);
        }

        /**
         *
         * @param label the list representing the ranking of the states of the corresponding mojmir automaton.
         *                     The elder states come first in the list.
         */
        public State(R label) {
            this.label = label;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State<?, ?> state = (State<?, ?>) o;
            return Objects.equals(label, state.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(label);
        }

        @Override
        public String toString() {
            return "State{" +
                    "label=" + label +
                    '}';
        }
    }
}
