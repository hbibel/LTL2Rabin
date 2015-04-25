package ltl2rabin;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.set.ListOrderedSet;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

// U: usually String
// TODO: Refactor Set<U> -> U
public class ProductAutomaton<T, U extends Collection> {
/*    private ListOrderedSet<State> states = new ListOrderedSet<>();
    private State initialState;
    private int stateCounter = 0; // Remove in final version

    public ProductAutomaton(Iterable<RabinAutomaton<T, U>> rabinAutomata, Set<U> alphabet) {
        List<RabinAutomaton<T, U>.State> initialRaStates = new ArrayList<>();
        rabinAutomata.iterator().forEachRemaining(rabinAutomaton -> initialRaStates.add(rabinAutomaton.getInitialState()));
        initialState = new State(initialRaStates);

        Queue<State> stateQueue = new ConcurrentLinkedQueue<>();
        stateQueue.add(initialState);
        while (!stateQueue.isEmpty()) {
            State tempState = stateQueue.poll();
            for (U letter : alphabet) {
                final List<RabinAutomaton<T, U>.State> tempRaStates = new ArrayList<>();
                tempState.rabinStates.forEach(rs -> tempRaStates.add(rs.readLetter(letter)));
                State newState = new State(tempRaStates);
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

    public State getInitialState() {
        return initialState;
    }

    public Collection<State> getStates() {
        return states;
    }

    public State run(List<U> word) {
        Iterator<U> iteratorOverLetters = word.iterator();
        State nextState = initialState;
        while (iteratorOverLetters.hasNext()) {
            nextState = nextState.readLetter(iteratorOverLetters.next());
        }
        return nextState;
    }

    public class State {
        private List<RabinAutomaton<T, U>.State> rabinStates;
        private HashMap<U, State> transitions = new HashMap<>();
        private String label = "pq" + stateCounter++;

        public String getLabel() {
            return label;
        }

        public State(List<RabinAutomaton<T, U>.State> rabinStates) {
            this.rabinStates = rabinStates;
        }

        public void setTransition(U letter, State to) {
            transitions.put(letter, to);
        }

        public State readLetter(U letter) {
            return transitions.get(letter);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return Objects.equals(rabinStates, state.rabinStates);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rabinStates);
        }
    }*/
}
