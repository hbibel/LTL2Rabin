package ltl2rabin;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.set.ListOrderedSet;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GDRA<T, U extends Collection> {
    private State initialState;
    private ListOrderedSet<State> states = new ListOrderedSet<>();
    private int stateCounter = 0; // Remove in final version

    public GDRA(Set<U> alphabet, MojmirAutomaton<T, U> mojmirAutomaton, ProductAutomaton<T, U> productAutomaton) {
        MojmirAutomaton<T, U>.State initialMojmirState = mojmirAutomaton.getInitialState();
        ProductAutomaton<T, U>.State initialPAState = productAutomaton.getInitialState();
        initialState = new State(initialMojmirState, initialPAState);

        Queue<State> stateQueue = new ConcurrentLinkedQueue<>();
        stateQueue.add(initialState);
        while (!stateQueue.isEmpty()) {
            State tempState = stateQueue.poll();
            for (U letter : alphabet) {
                MojmirAutomaton<T, U>.State nextMojmirState = tempState.mojmirState.readLetter(letter);
                ProductAutomaton<T, U>.State nextPAState = tempState.paState.readLetter(letter);

                State newState = new State(nextMojmirState, nextPAState);
                int index = states.indexOf(newState);
                if (index >= 0) {
                    newState = states.get(index);
                    stateCounter--;
                }
                else {
                    states.add(newState);
                }
                tempState.setTransition(letter, newState);
                if (newState.transitions.size() < alphabet.size()) {
                    stateQueue.add(newState);
                }
            }
        }
    }

    public class State {
        MojmirAutomaton<T, U>.State mojmirState;
        ProductAutomaton<T, U>.State paState;
        HashMap<U, State> transitions;
        String label = "gq" + stateCounter;

        public State(MojmirAutomaton<T, U>.State mojmirState, ProductAutomaton<T, U>.State paState) {
            this.mojmirState = mojmirState;
            this.paState = paState;
        }

        public void setTransition(U letter, State to) {
            transitions.put(letter, to);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return Objects.equals(mojmirState, state.mojmirState) &&
                    Objects.equals(paState, state.paState);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mojmirState, paState);
        }
    }
}
