package ltl2rabin;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * This class describes a mojmir automaton.
 * @param <T> The type of information associated with a state, e.g. its corresponding LTL formula
 * @param <U> The type that represents the "letters", e.g. String
 */
public class MojmirAutomaton<T, U> {
    public Set<State> acceptingStates; // yet to be identified
    public Set<State> sinks; // yet to be identified
    public HashSet<State> states;
    public HashSet<U> alphabet;
    public BiFunction<T, Set<U>, T> transitionFunction;

    public MojmirAutomaton(T info, BiFunction<T, Set<U>, T> transitionFunction, HashSet<U> alphabet) {
        this.alphabet = alphabet;
        states = new HashSet<State> ();
        states.add(new State(info));
        this.transitionFunction = transitionFunction;
        reach();
    }

    public void reach() {
        boolean newStateAdded = true;
        HashSet<State> statesToBeAdded = new HashSet<State> ();
        Set<Set<U>> words = Sets.powerSet(alphabet);

        for (State state : states) {
            for (Set<U> word : words) {
                T newState = transitionFunction.apply(state.info, word);
                statesToBeAdded.add(new State(newState));
            }
        }
        if (states.addAll(statesToBeAdded)) reach(); // if new states have been added, repeat
    }

    public class State {
        T info;

        @Override
        public boolean equals(Object obj) {
            return (obj != null) && (obj.getClass().equals(this.getClass())) && (((State) obj).info.equals(this.info));
        }

        @Override
        public int hashCode() {
            return info.hashCode();
        }

        public State(T info) {
            this.info = info;
        }
    }
}
