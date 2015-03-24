package ltl2rabin;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RabinAutomaton<T, U> {
    // public BiFunction<T, Set<U>, T> transitionFunction;
    private Set<State> states = new HashSet<>();
    private final State initialState;
    private final Set<U> alphabet;

    public Set<State> getStates() {
        return states;
    }

    public RabinAutomaton(final MojmirAutomaton<T, U> mojmirAutomaton, Set<U> alphabet) {
        this.alphabet = alphabet;
        List<MojmirAutomaton<T, U>.State> initialMojmirStates = new ArrayList<>();
        initialMojmirStates.add(mojmirAutomaton.getInitialState());
        initialState = new State(initialMojmirStates);
        states.add(initialState);

        boolean reachedFixpoint = false;
        Set<Set<U>> letters = Sets.powerSet(alphabet);
        List<MojmirAutomaton<T, U>.State> tempStates = initialMojmirStates;
        while (!reachedFixpoint) {
            reachedFixpoint = true;
            for (Set<U> letter : letters) {
                // read; map e.readLetter to tempStates, then add initialMojmirStates.get(0), then filter duplicates,
                // then filter sinks, and then transform it back to a List
                List<MojmirAutomaton<T, U>.State> newStateList = Stream.concat(tempStates.stream()
                        .map(e -> e.readLetter(letter)), Stream.of(initialMojmirStates.get(0)))
                        // According to the javadocs: For ordered streams, the selection of distinct elements is stable
                        // (for duplicated elements, the element appearing first in the encounter order is preserved.)
                        .distinct()
                        // filter strikes out the elements that do not fulfil the condition specified within the
                        // parentheses
                        .filter(e -> !mojmirAutomaton.getSinks().contains(e))
                        .collect(Collectors.toList());
                tempStates = newStateList;
                reachedFixpoint &= !states.add(new State(newStateList));
            }
        }
    }

    public class State{
        private final List<MojmirAutomaton<T, U>.State> mojmirStates;

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
}
