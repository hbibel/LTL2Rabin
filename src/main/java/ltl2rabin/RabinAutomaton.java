package ltl2rabin;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RabinAutomaton<T, U extends Collection> extends Automaton<T, U>{
    // public BiFunction<T, Set<U>, T> transitionFunction;
    private ListOrderedSet<State> states = new ListOrderedSet<>();
    private final State initialState;
    private Set<Transition> fail = new ListOrderedSet<>();

    public State getInitialState() {
        return initialState;
    }

    //private final Set<U> alphabet;
    private int stateCounter = 0;

    public ListOrderedSet<State> getStates() {
        return states;
    }

    public RabinAutomaton(final MojmirAutomaton<T, U> mojmirAutomaton, Set<U> alphabet) {
        //this.alphabet = alphabet;
        List<MojmirAutomaton<T, U>.State> initialMojmirStates = new ArrayList<>(Collections.singletonList(mojmirAutomaton.getInitialState()));
        initialState = new State(initialMojmirStates);
        states.add(initialState);

        ConcurrentLinkedQueue<State> queue = new ConcurrentLinkedQueue<>();
        queue.add(initialState);
        while (!queue.isEmpty()) {
            State tempState = queue.poll();
            if (tempState.transitions.size() != alphabet.size()) {
                // tempState has transitions for any letter ==> tempState has been visited before
                List<MojmirAutomaton<T, U>.State> tempMojmirStates = tempState.mojmirStates;
                for (U letter : alphabet) {
                    List<MojmirAutomaton<T, U>.State> newMojmirStateList = Stream.concat(tempMojmirStates.stream()
                            .map(e -> e.readLetter(letter)), Stream.of(initialMojmirStates.get(0)))
                            // According to the javadocs: For ordered streams, the selection of distinct elements is
                            // stable (for duplicated elements, the element appearing first in the encounter order is
                            // preserved.)
                            .distinct()
                            // filter strikes out the elements that do not fulfil the condition specified within the
                            // parentheses
                            .filter(e -> !mojmirAutomaton.getSinks().contains(e))
                            .collect(Collectors.toList());
                    State newState = new State(newMojmirStateList);
                    int indexOfExistingState = states.indexOf(newState);
                    if (indexOfExistingState != -1) {
                        newState = states.get(indexOfExistingState);
                        --stateCounter;
                    }
                    else {
                        states.add(newState);
                    }
                    tempState.setTransition(letter, newState);
                    queue.add(newState);
                }
            }
        }

        // a token moves from mojmir state q into non-acc sink q' ==> fail
        states.forEach( state -> {
            state.getMojmirStates().forEach( ms -> {
                alphabet.forEach( letter -> {
                    MojmirAutomaton<T, U>.State nextState = ms.readLetter(letter);
                    if (nextState.isSink() && !nextState.isAccepting()) {
                        fail.add(new Transition(state, letter, nextState));
                    }
                });
            });
        });


    }

    public Set<Transition> failTransitions() {
        return fail;
    }

    public State run(List<U> word) {
        Iterator<U> iteratorOverLetters = word.iterator();
        State nextState = initialState;
        while (iteratorOverLetters.hasNext()) {
            nextState = nextState.readLetter(iteratorOverLetters.next());
        }
        return nextState;
    }

    public class State extends Automaton<T, U>.State {
        private final List<MojmirAutomaton<T, U>.State> mojmirStates;
        private Map<U, State> transitions = new HashMap<>();
        private String label = "rq" + stateCounter++;

        public void setTransition(U letter, State to) {
            transitions.put(letter, to);
        }

        public List<MojmirAutomaton<T, U>.State> getMojmirStates() {
            return mojmirStates;
        }

        public State readLetter(U letter) {
            return transitions.get(letter);
        }

        public String getLabel () {
            return label;
        }

        /**
         *
         * @param mojmirStates the list representing the ranking of the states of the corresponding mojmir automaton.
         *                     The elder states come first in the list.
         */
        public State(List<MojmirAutomaton<T, U>.State> mojmirStates) {
            this.mojmirStates = mojmirStates;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object obj) {
            boolean result = (obj != null)
                    && (obj.getClass().equals(this.getClass()))
                    && ((State)obj).mojmirStates.size() == this.mojmirStates.size();
            if (!result) return false;
            Iterator<MojmirAutomaton<T, U>.State> itObj = ((State)obj).mojmirStates.iterator();
            Iterator<MojmirAutomaton<T, U>.State> itThis = this.mojmirStates.iterator();
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

    public class Transition extends Automaton<T, U>.Transition {
        public Transition(Automaton<T, U>.State from, U letter, Automaton<T, U>.State to) {
            super(from, letter, to);
        }
    }
}
