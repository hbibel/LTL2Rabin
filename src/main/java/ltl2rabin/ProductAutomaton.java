package ltl2rabin;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.set.ListOrderedSet;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

// U: usually String
public class ProductAutomaton<T, U> {
    private ListOrderedSet<State> states = new ListOrderedSet<>();
    private State initialState;

    public ProductAutomaton(Iterable<RabinAutomaton<T, U>> rabinAutomata, Set<U> alphabet) {
        List<RabinAutomaton<T, U>.State> initialRaStates = new ArrayList<>();
        rabinAutomata.iterator().forEachRemaining(rabinAutomaton -> initialRaStates.add(rabinAutomaton.getInitialState()));
        initialState = new State(initialRaStates);

        Queue<State> stateQueue = new ConcurrentLinkedQueue<>();
        stateQueue.add(initialState);
        Set<Set<U>> letters = Sets.powerSet(alphabet);
        while (!stateQueue.isEmpty()) {
            State tempState = stateQueue.poll();
            for (Set<U> letter : letters) {
                final List<RabinAutomaton<T, U>.State> tempRaStates = new ArrayList<>();
                tempState.rabinStates.forEach(rs -> tempRaStates.add(rs.readLetter(letter)));
                State newState = new State(tempRaStates);
                int index = states.indexOf(newState);
                if (index >= 0) {
                    newState = states.get(index);
                }
                else {
                    states.add(newState);
                }
                tempState.setTransition(letter, newState);
                if (newState.transitions.size() < letters.size()) {
                    stateQueue.add(newState);
                    System.out.println(newState.transitions.size() + "<" + letters.size());
                    System.out.println(states.size());
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

    public State run(List<Set<U>> word) {
        Iterator<Set<U>> iteratorOverLetters = word.iterator();
        State nextState = initialState;
        while (iteratorOverLetters.hasNext()) {
            nextState = nextState.readLetter(iteratorOverLetters.next());
        }
        return nextState;
    }

    public class State {
        private List<RabinAutomaton<T, U>.State> rabinStates;
        private HashMap<Set<U>, State> transitions = new HashMap<>();

        public State(List<RabinAutomaton<T, U>.State> rabinStates) {
            this.rabinStates = rabinStates;
        }

        public void setTransition(Set<U> letter, State to) {
            transitions.put(letter, to);
        }

        public State readLetter(Set<U> letter) {
            return transitions.get(letter);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return Objects.equals(rabinStates, state.rabinStates) &&
                    Objects.equals(transitions, state.transitions);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rabinStates, transitions);
        }
    }
}
