package ltl2rabin;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.*;
import ltl2rabin.LTL.Boolean;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * SubAutomata are Rabin automata that are constructed for a G-subformula. States of GDRAs have a collection of SubAutomaton states.
 * SubAutomata provide the GDRA with valuable information that is used to construct the acceptance condition of the GDRA.
 * A <code>SubAutomaton.State</code> represents a ranking of <code>MojmirAutomaton.State</code>s. The <code>failMerge</code>
 * and <code>succeed</code> methods generate the acceptance sets for a given rank and set of G-subformulas.
 */
public class SubAutomaton extends RabinAutomaton<List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>>, Set<String>> {
    private final int maxRank;
    // computing caches for the (expensive to create) acceptance sets by Guava:
    private final CacheLoader<Pair<Integer, Set<G>>, ImmutableSet<Transition>> failMergeLoader =
            new CacheLoader<Pair<Integer, Set<G>>, ImmutableSet<Transition>>() {
                @Override
                public ImmutableSet<Transition> load(Pair<Integer, Set<G>> integerSetPair) throws Exception {
                    return calculateFailMerge(integerSetPair.getFirst(), integerSetPair.getSecond());
                }
            };
    private final LoadingCache<Pair<Integer, Set<G>>, ImmutableSet<Transition>> failMergeCache =
            CacheBuilder.newBuilder()
                    .build(failMergeLoader);
    private final CacheLoader<Pair<Integer, Set<G>>, ImmutableSet<Transition>> succeedLoader =
            new CacheLoader<Pair<Integer, Set<G>>, ImmutableSet<Transition>>() {
                @Override
                public ImmutableSet<Transition> load(Pair<Integer, Set<G>> integerSetPair) throws Exception {
                    return calculateSucceed(integerSetPair.getFirst(), integerSetPair.getSecond());
                }
            };
    private final LoadingCache<Pair<Integer, Set<G>>, ImmutableSet<Transition>> succeedCache =
            CacheBuilder.newBuilder()
                    .build(succeedLoader);

    public SubAutomaton(ImmutableSet<State> states,
                        State initialState,
                        ImmutableSet<Set<String>> alphabet,
                        int maxRank) {
        super(states, initialState, alphabet);
        this.maxRank = maxRank;
    }

    /**
     * @return The maximum rank for any Mojmir automaton state in any of the SubAutomaton automaton states.
     */
    public int getMaxRank() {
        return maxRank;
    }

    @Override
    public SubAutomaton.State getInitialState() {
        return (State) super.getInitialState();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ImmutableSet<State> getStates() {
        return (ImmutableSet<State>) super.getStates();
    }

    @Override
    public State run(List<Set<String>> word) {
        return (State) super.run(word);
    }

    /**
     * This method returns the combination of fail and merge(i) for a given rank i and a set of G subformulas. It makes
     * use of computing caches, so the first call will be the (by far) most expensive.
     * @param rank      The requested merging rank
     * @param curlyG    The set of G subformulas that hold
     * @return The union of fail and merge(i)
     */
    public ImmutableSet<Transition> failMerge(int rank, Set<G> curlyG) {
        ImmutableSet<Transition> result = null;
        try {
            result = failMergeCache.get(new Pair<>(rank, curlyG));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result == null ? ImmutableSet.of() : result;
    }

    private ImmutableSet<Transition> calculateFailMerge(int rank, Set<G> curlyG) {
        if (rank > maxRank) {
            return ImmutableSet.copyOf(Collections.emptySet());
        }
        ImmutableSet.Builder<Transition> resultBuilder = new ImmutableSet.Builder<>();
        PropEquivalenceClass gConjunction;
        if (curlyG.isEmpty()) {
            gConjunction = new PropEquivalenceClass(new Boolean(true));
        } else {
            gConjunction = new PropEquivalenceClass(new And(ImmutableList.copyOf(curlyG)));
        }
        getStates().stream().forEach(subState -> {
            getAlphabet().stream().forEach(letter -> {
                // fail: Add transitions that move tokens from all Mojmir states in subState in a non-accepting sink
                final State toState = subState.readLetter(letter);
                subState.getLabel().stream().forEach(ms -> {
                    final MojmirAutomaton.State<PropEquivalenceClass, Set<String>> msToState = ms.readLetter(letter);
                    if (msToState.isSink() && !gConjunction.implies(msToState.getLabel())) {
                        resultBuilder.add(new Transition(subState, letter, toState));
                    }
                });
                // merge: A token with r < rank moves to the non-accepting state q' and another token also moves there
                final int maxMergeRank = rank > subState.getLabel().size() - 1 ? subState.getLabel().size() - 1 : rank;
                for (int r = 0; r < maxMergeRank; r++) {
                    // TODO: failMerge(i) is a superset of failMerge(i-1). Calculating failMerge(i-1) again is
                    // unnecessary. Better get failMerge(j) for all j<i from the cache.
                    MojmirAutomaton.State<PropEquivalenceClass, Set<String>> mFromState = subState.getLabel().get(r); // q
                    MojmirAutomaton.State<PropEquivalenceClass, Set<String>> mToState = mFromState.readLetter(letter); // q'
                    // if mToState is a sink, it is either accepting (==> not in merge) or failing (==> also not in merge)
                    if (mToState.isSink() || mToState.isAcceptingState(curlyG)) { // TODO: Are there accepting states that are not sinks? If no --> mToState.isSink()
                        break;
                    }
                    if (mToState.equals(getInitialState().getLabel().get(0))) {
                        resultBuilder.add(new Transition(subState, letter, toState));
                    }
                    else {
                        List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> otherTokens = subState.getLabel().stream()
                                .filter(ms -> !ms.equals(mFromState)) // tokens that don't come from q ...
                                .map(ms -> ms.readLetter(letter))
                                .filter(ms -> ms.equals(mToState)) // ... also move to q'
                                .collect(Collectors.toList());
                        if (otherTokens.size() > 0) {
                            resultBuilder.add(new Transition(subState, letter, toState));
                        }
                    }
                }
            });
        });
        return resultBuilder.build();
    }

    /**
     * This method returns the set of succeeding transitions for a given rank i and a set of G subformulas. It makes
     * use of computing caches, so the first call will be the (by far) most expensive.
     * @param rank      The rank the transitions succeed at
     * @param curlyG    The set of G subformulas that hold
     * @return The set of succeeding transitions
     */
    public ImmutableSet<Transition> succeed(int rank, Set<G> curlyG) {
        ImmutableSet<Transition> result = null;
        try {
            result = succeedCache.get(new Pair<>(rank, curlyG));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result == null ? ImmutableSet.of() : result;
    }

    private ImmutableSet<Transition> calculateSucceed(int rank, Set<G> curlyG) {
        if (rank > maxRank) {
            return ImmutableSet.copyOf(Collections.emptySet());
        }
        ImmutableSet.Builder<Transition> resultBuilder = new ImmutableSet.Builder<>();
        PropEquivalenceClass gConjunction;
        if (curlyG.isEmpty()) {
            gConjunction = new PropEquivalenceClass(new Boolean(true));
        } else {
            gConjunction = new PropEquivalenceClass(new And(ImmutableList.copyOf(curlyG)));
        }
        getStates().stream()
                .forEach(state -> {
                    getAlphabet().stream().forEach(letter -> {
                        if (state.getLabel().size() > rank && gConjunction.implies(state.getLabel().get(rank).readLetter(letter).getLabel())) {
                            resultBuilder.add(new Transition(state, letter, state.readLetter(letter)));
                        }
                    });
                });
        return resultBuilder.build();
    }

    public static class State extends RabinAutomaton.State<List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>>, Set<String>> {
        private final Formula psi;

        /**
         * @param label The list representing the ranking of the states of the corresponding mojmir automaton.
         *              The elder states come first in the list. The lowest rank is 0.
         * @param psi   The LTL Formula that is used to generate the automaton the state belongs to. This is the
         *              connection between the state and the automaton.
         */
        public State(List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> label, Formula psi) {
            super(label);
            this.psi = psi;
        }

        /**
         *
         * @return The formula that was used to construct the SubAutomaton automaton that this state is part of.
         */
        public Formula getPsi() {
            return psi;
        }

        /**
         * Returns a list of formulas that are labels of <code>MojmirAutomaton.State</code>s that have a rank greater
         * or equal <code>r</code>. For an explanation of the name, see Def. 6.10 in the paper
         * "From LTL to Deterministic Automata."
         * @param r The formulas that are returned will have a rank greater or equal to <code>r</code>.
         * @return The list of formulas ranked <code>r</code> or higher. The elements in this list all are distinct, due
         *         to the construction of SubAutomaton automata.
         */
        public List<Formula> succeedingFormulas(int r) {
            if (r >= getLabel().size()) {
                return Collections.emptyList();
            }
            ImmutableList.Builder<Formula> conjunctionListBuilder = new ImmutableList.Builder<>();
            for (int i = r; i < getLabel().size(); i++) {
                conjunctionListBuilder.add(getLabel().get(i).getLabel().getRepresentative());
            }
            return conjunctionListBuilder.build();
        }

        @Override
        public State readLetter(Set<String> letter) {
            return (State) super.readLetter(letter);
        }
    }

    public static class Transition extends Automaton.Transition<State, Set<String>> {

        protected Transition(State from, Set<String> letter, State to) {
            super(from, letter, to);
        }
    }
}
