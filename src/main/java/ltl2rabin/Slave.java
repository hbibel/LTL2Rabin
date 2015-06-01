package ltl2rabin;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

public class Slave extends RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>> {
    private final Pair<ImmutableSet<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>>, ImmutableSet<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>>> slavePair;

    public Slave(ImmutableCollection<State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>> states,
                 State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>> initialState,
                 Pair<ImmutableSet<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>>, ImmutableSet<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>>> rabinCondition,
                 ImmutableSet<Set<String>> alphabet) {
        super(states, initialState, rabinCondition, alphabet);
        this.slavePair = rabinCondition;
    }

    public Pair<ImmutableSet<Transition<State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>>, ImmutableSet<Transition<State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>>> getRabinPair() {
        return slavePair;
    }
}
