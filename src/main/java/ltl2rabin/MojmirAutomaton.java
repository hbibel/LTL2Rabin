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
    public Set<State> acceptingStates; // yet to be identified
    public Set<State> sinks;
    public Set<State> states;
    public Set<U> alphabet;
    public BiFunction<T, Set<U>, T> transitionFunction;

    public MojmirAutomaton(T info, BiFunction<T, Set<U>, T> transitionFunction, HashSet<U> alphabet) {
        this.alphabet = alphabet;
        states = new HashSet<State> ();
        State firstState = new State(info);
        firstState.setOrigin(true);
        states.add(firstState);
        sinks = new HashSet<>();
        this.transitionFunction = transitionFunction;
        reach();
    }

    public void reach() {
        Queue<State> statesToBeAdded = new ConcurrentLinkedQueue<State>(states);
        Set<Set<U>> words = Sets.powerSet(alphabet);

        while (!statesToBeAdded.isEmpty()) {
            State temp = statesToBeAdded.poll();
            boolean isSink = true;
            for (Set<U> word : words) {
                T newStateInfo = transitionFunction.apply(temp.info, word);
                if (newStateInfo.equals(temp.info)) continue;
                isSink = false;
                State newState = new State(newStateInfo);
                if (!states.add(newState)) continue; // Remark: states is a set, so no duplicate states will be added, instead false is returned
                statesToBeAdded.offer(newState);
            }
            if (isSink && (!temp.isOrigin())) {
                temp.setSink(true);
                sinks.add(temp);
            }
        }
    }

    public class State {
        T info;
        boolean isSink;
        boolean isOrigin;

        public State(T info) {
            isSink = false;
            isOrigin = false;
            this.info = info;
        }

        public void setSink(boolean isSink) {
            this.isSink = isSink;
        }

        public void setOrigin(boolean isOrigin) {
            this.isOrigin = isOrigin;
        }

        public boolean isOrigin() {
            return isOrigin;
        }

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
