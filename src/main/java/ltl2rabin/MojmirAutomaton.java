package ltl2rabin;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiFunction;

/**
 * This class describes a mojmir automaton.
 * @param <T> The type of information associated with a state, e.g. its corresponding LTL formula
 * @param <U> The type that represents the "letters", e.g. String
 */
public class MojmirAutomaton<T, U> {
    // public Set<State> acceptingStates; // TODO yet to be identified
    private final Set<State> sinks;
    private final Set<State> states;
    private final State initialState;
    private final Set<U> alphabet;
    private final BiFunction<T, Set<U>, T> transitionFunction;

    public State getInitialState() {
        return initialState;
    }

    public MojmirAutomaton(T info, BiFunction<T, Set<U>, T> transitionFunction, HashSet<U> alphabet) {
        this.alphabet = alphabet;
        states = new HashSet<> ();
        initialState = new State(info);
        states.add(initialState);
        sinks = new HashSet<>();
        this.transitionFunction = transitionFunction;
        reach();
    }

    public Set<State> getSinks() {
        return sinks;
    }

    public Set<State> getStates() {
        return states;
    }

    private void reach() {
        Queue<State> statesToBeAdded = new ConcurrentLinkedQueue<>(states);
        Set<Set<U>> letters = Sets.powerSet(alphabet);

        while (!statesToBeAdded.isEmpty()) {
            State temp = statesToBeAdded.poll();
            boolean isSink = true;
            for (Set<U> letter : letters) {
                T newStateInfo = transitionFunction.apply(temp.info, letter);
                if (newStateInfo.equals(temp.info)) continue;
                // A sink is a state that only has self-loops as outgoing transitions. If temp is a sink, this
                // line never will be reached.
                isSink = false;
                State newState = new State(newStateInfo);
                if (!states.add(newState)) continue; // Remark: states is a set, so no duplicate states will be added, instead false is returned
                statesToBeAdded.offer(newState);
            }
            if (isSink && !(temp == initialState)) {
                temp.setSink(true);
                sinks.add(temp);
            }
        }
    }

    public class State {
        private final T info;

        boolean isSink;

/*        public boolean isSink() {
            return isSink;
        }*/

        public State(T info) {
         isSink = false;
            this.info = info;
        }

        public void setSink(boolean isSink) {
            this.isSink = isSink;
        }

        // Alternative: Keep all state transitions in a mapping: letter --> State
        public State readLetter(Set<U> letter) {
            T newInfo = transitionFunction.apply(this.info, letter);
            return new State(newInfo);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object obj) {
            return (obj != null) && (obj.getClass().equals(this.getClass())) && (((State) obj).info.equals(this.info));
        }

        @Override
        public int hashCode() {
            return info.hashCode();
        }

        @Override
        public String toString() {
            return "state(" + info.toString() + ")";
        }
    }
}
