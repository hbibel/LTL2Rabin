package ltl2rabin;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RabinAutomaton<T, U extends Collection> extends Automaton<T, U> {
    private ListOrderedSet<State> states = new ListOrderedSet<>();
    private final State initialState;
    private Set<Transition> transitions = new HashSet<>();
    private Set<Transition> fail = new ListOrderedSet<>();
    private List<Set<Transition>> buy = new ArrayList<>();
    private List<Set<Transition>> succeed = new ArrayList<>();

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
        List<MojmirAutomaton.State<T, U>> initialMojmirStates = new ArrayList<>(Collections.singletonList(mojmirAutomaton.getInitialState()));
        initialState = new State(initialMojmirStates);
        states.add(initialState);

        ConcurrentLinkedQueue<State> queue = new ConcurrentLinkedQueue<>();
        queue.add(initialState);
        while (!queue.isEmpty()) {
            State tempState = queue.poll();
            if (tempState.transitions.size() != alphabet.size()) {
                // tempState has transitions for any letter ==> tempState has been visited before
                List<MojmirAutomaton.State<T, U>> tempMojmirStates = tempState.mojmirStates;
                for (U letter : alphabet) {
                    List<MojmirAutomaton.State<T, U>> newMojmirStateList = Stream.concat(tempMojmirStates.stream()
                            .map(e -> e.readLetter(letter)), Stream.of(initialMojmirStates.get(0)))
                            // According to the javadocs: For ordered streams, the selection of distinct elements is
                            // stable (for duplicated elements, the element appearing first in the encounter order is
                            // preserved.)
                            .distinct()
                            // filter strikes out the elements that do not fulfil the condition specified within the
                            // parentheses
                            .filter(e -> !e.isSink())
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
                    transitions.add(new Transition(tempState, letter, newState));
                    queue.add(newState);
                }
            }
        }

        // a token moves from mojmir state q into non-acc sink q' ==> fail
        states.forEach(rState -> {
            rState.getMojmirStates().forEach(mState -> {
                alphabet.forEach(letter -> {
                    MojmirAutomaton.State<T, U> nextMState = mState.readLetter(letter);
                    if (nextMState.isSink() && !nextMState.isAccepting()) {
                        fail.add(new Transition(rState, letter, rState.readLetter(letter)));
                    }
                });
            });
        });


        // one token with rank = i buys another or gets bought
        for (int rank = 0; rank < mojmirAutomaton.getMaxRank(); rank++) {
            Set<Transition> buyI = new ListOrderedSet<>();
            final int finalRank = rank;
            transitions.forEach(transition -> {
                if (buysOrGetBought(transition, finalRank, mojmirAutomaton.getInitialState())) {
                    buyI.add(transition);
                }
            });
            buy.add(buyI);
        }

        for (int rank = 0; rank < mojmirAutomaton.getMaxRank(); rank++) {
            Set<Transition> succeedI = new ListOrderedSet<>();
            final int finalRank = rank;
            for (U letter : alphabet) {
                succeedI.addAll(transitions.stream()
                        .filter(transition -> transition.getFrom().getMojmirStates().size() > finalRank)
                        .filter(transition -> !transition.getFrom().getMojmirStates().get(finalRank).isAccepting()
                                || transition.getFrom().getMojmirStates().get(finalRank).equals(mojmirAutomaton.getInitialState()))
                        .filter(transition -> transition.getFrom().getMojmirStates().get(finalRank).readLetter(letter).isAccepting())
                        .filter(transition -> transition.letter.equals(letter))
                        .collect(Collectors.toList()));
            }
            succeed.add(succeedI);
        }
    }

    private boolean buysOrGetBought(Transition transition, int rank, MojmirAutomaton.State<T, U> mInitialState) {
        if (rank >= transition.getFrom().getMojmirStates().size()) {
            return false;
        }
        MojmirAutomaton.State<T, U> mFromState = transition.getFrom().getMojmirStates().get(rank); // q
        MojmirAutomaton.State<T, U> mToState = mFromState.readLetter(transition.getLetter()); // q'
        if (mToState.equals(mInitialState)) {
            return true;
        }
        else {
            List<MojmirAutomaton.State<T, U>> mStatesAfterReadingLetter = transition.getFrom().getMojmirStates().stream()
                    .filter(ms -> !ms.equals(mFromState))
                    .map(ms -> ms.readLetter(transition.getLetter()))
                    .filter(ms -> ms.equals(mToState))
                    .collect(Collectors.toList());
            if (mStatesAfterReadingLetter.size() > 0) {
                return true;
            }
        }
        return false;
    }

    public Set<Transition> fail() {
        return fail;
    }

    public Set<Transition> buy(int rank) {
        Set<Transition> result = new ListOrderedSet<>();
        for (int i = 0; i < rank; i++) {
            result.addAll(buy.get(i));
        }
        return result;
    }

    public State run(List<U> word) {
        Iterator<U> iteratorOverLetters = word.iterator();
        State nextState = initialState;
        while (iteratorOverLetters.hasNext()) {
            nextState = nextState.readLetter(iteratorOverLetters.next());
        }
        return nextState;
    }

    public class State extends Automaton.State<T, U> {
        private final List<MojmirAutomaton.State<T, U>> mojmirStates;
        private Map<U, State> transitions = new HashMap<>();
        private String label = "rq" + stateCounter++;

        public void setTransition(U letter, State to) {
            transitions.put(letter, to);
        }

        public List<MojmirAutomaton.State<T, U>> getMojmirStates() {
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
        public State(List<MojmirAutomaton.State<T, U>> mojmirStates) {
            this.mojmirStates = mojmirStates;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object obj) {
            boolean result = (obj != null)
                    && (obj.getClass().equals(this.getClass()))
                    && ((State)obj).mojmirStates.size() == this.mojmirStates.size();
            if (!result) return false;
            Iterator<MojmirAutomaton.State<T, U>> itObj = ((State)obj).mojmirStates.iterator();
            Iterator<MojmirAutomaton.State<T, U>> itThis = this.mojmirStates.iterator();
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

    public class Transition extends Automaton.Transition {
        private State from;
        private State to;
        private U letter;

        public Transition(RabinAutomaton<T, U>.State from, U letter, RabinAutomaton<T, U>.State to) {
            super(from, letter, to);
            // TODO: Lines below necessary?
            this.from = from;
            this.to = to;
            this.letter = letter;
        }

        public State getFrom() {
            return from;
        }

        public State getTo() {
            return to;
        }

        public U getLetter() {
            return letter;
        }


    }
}
