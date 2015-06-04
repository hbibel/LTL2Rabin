package ltl2rabin;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

public class Slave extends RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>> {
    private final Pair<ImmutableMap<Integer, ImmutableSet<Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>>>, ImmutableMap<Integer, ImmutableSet<Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>>>> failBuySucceed;

    public Slave(ImmutableCollection<State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>> states,
                 State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>> initialState,
                 Pair<ImmutableMap<Integer, ImmutableSet<Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>>>, ImmutableMap<Integer, ImmutableSet<Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>>>> rabinCondition,
                 ImmutableSet<Set<String>> alphabet) {
        super(states, initialState, rabinCondition, alphabet);
        this.failBuySucceed = rabinCondition;
    }

    public ImmutableSet<Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>> failBuy (int i) {
        return failBuySucceed.getFirst().get(i);
    }

    public ImmutableSet<Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>> succeed (int i) {
        return failBuySucceed.getSecond().get(i);
    }
}
