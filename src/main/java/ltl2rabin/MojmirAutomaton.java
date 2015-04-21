package ltl2rabin;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This class describes a mojmir automaton.
 * @param <T> The type of information associated with a state, e.g. its corresponding LTL formula
 * @param <U> The type that represents the "letters", e.g. Set of Strings
 */
public class MojmirAutomaton<T, U> extends Automaton<T, U> {
    private Set<State> acceptingStates = new HashSet<>(); // TODO yet to be identified, if necessary
    private final Set<State> sinks;
    private final Set<State> states;
    private final State initialState;
    private final Set<U> alphabet;
    private final BiFunction<T, U, T> transitionFunction;
    private int maxRank;
    private Function<MojmirAutomaton<T, U>.State, Boolean> accFunction;

    public State getInitialState() {
        return initialState;
    }

    public MojmirAutomaton(T info, BiFunction<T, U, T> transitionFunction, Set<U> alphabet) {
        this.alphabet = alphabet;
        states = new HashSet<> ();
        initialState = new State(info);
        states.add(initialState);
        sinks = new HashSet<>();
        this.transitionFunction = transitionFunction;
        reach();
        maxRank = states.size(); // Has to be executed after reach()
    }

    public MojmirAutomaton(T info, BiFunction<T, U, T> transitionFunction, Set<U> alphabet,
                           Function<MojmirAutomaton<T, U>.State, Boolean> accFunction) {
        this.alphabet = alphabet;
        states = new HashSet<> ();
        initialState = new State(info);
        states.add(initialState);
        sinks = new HashSet<>();
        this.transitionFunction = transitionFunction;
        this.accFunction = accFunction;
        reach();
        maxRank = states.size(); // Has to be executed after reach()
    }

    public Set<State> getSinks() {
        return sinks;
    }

    public Set<State> getStates() {
        return states;
    }

    private void reach() {
        Queue<State> statesToBeAdded = new ConcurrentLinkedQueue<>(states);

        while (!statesToBeAdded.isEmpty()) {
            State temp = statesToBeAdded.poll();
            if (accFunction.apply(temp)) { acceptingStates.add(temp); }
            boolean isSink = true;
            for (U letter : alphabet) {
                T newStateInfo = transitionFunction.apply(temp.label, letter);
                if (newStateInfo.equals(temp.label)) continue;
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

    public class State extends Automaton<T, U>.State {
        private final T label;

        public T getLabel() {
            return label;
        }

        boolean isSink;

        public State(T label) {
            isSink = false;
            this.label = label;
        }

        public void setSink(boolean isSink) {
            this.isSink = isSink;
        }

        // Alternative: Keep all state transitions in a mapping: letter --> State
        public State readLetter(U letter) {
            T newInfo = transitionFunction.apply(this.label, letter);
            return new State(newInfo);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object obj) {
            return (obj != null) && (obj.getClass().equals(this.getClass())) && (((State) obj).label.equals(this.label));
        }

        @Override
        public int hashCode() {
            return label.hashCode();
        }

        @Override
        public String toString() {
            return "state(" + label.toString() + ")";
        }
    }
}
