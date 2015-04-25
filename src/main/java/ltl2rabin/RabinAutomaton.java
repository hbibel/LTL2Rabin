package ltl2rabin;

import com.google.common.collect.ImmutableCollection;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;

public class RabinAutomaton<T, U extends Collection> extends Automaton<T, U> {
    private final ImmutableCollection<State<T, U>> states;
    private final State<T, U> initialState;
    private final Pair<?, ?> rabinPair;

    public RabinAutomaton(ImmutableCollection<State<T, U>> states, State<T, U> initialState, Pair<?, ?> rabinPair) {
        this.states = states;
        this.initialState = initialState;
        this.rabinPair = rabinPair;
    }

    public State<T, U> getInitialState() {
        return initialState;
    }

    public Pair<?, ?> getRabinPair() {
        return rabinPair;
    }

    public ImmutableCollection<State<T, U>> getStates() {
        return states;
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
        private final List<MojmirAutomaton.State<R, S>> mojmirStates;
        private Map<S, State<R, S>> transitions = new HashMap<>();

        public void setTransition(S letter, State<R, S> to) {
            transitions.put(letter, to);
        }

        public Map<S, State<R, S>> getTransitions() {
            return transitions;
        }

        public List<MojmirAutomaton.State<R, S>> getMojmirStates() {
            return mojmirStates;
        }

        public State<R, S> readLetter(S letter) {
            return transitions.get(letter);
        }

        /**
         *
         * @param mojmirStates the list representing the ranking of the states of the corresponding mojmir automaton.
         *                     The elder states come first in the list.
         */
        public State(List<MojmirAutomaton.State<R, S>> mojmirStates) {
            this.mojmirStates = mojmirStates;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object obj) {
            boolean result = (obj != null)
                    && (obj.getClass().equals(this.getClass()))
                    && ((State)obj).mojmirStates.size() == this.mojmirStates.size();
            if (!result) return false;
            Iterator<MojmirAutomaton.State<R, S>> itObj = ((State)obj).mojmirStates.iterator();
            Iterator<MojmirAutomaton.State<R, S>> itThis = this.mojmirStates.iterator();
            while (itObj.hasNext()) {
                result = itObj.next().equals(itThis.next());
                if (!result) return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(911, 19).append(mojmirStates).toHashCode();
        }

        @Override
        public String toString() {
            return "State{" +
                    "mojmirStates=" + mojmirStates +
                    '}';
        }
    }
}
