package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.*;
import ltl2rabin.LTL.Boolean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MojmirAutomatonFactoryFromLTLAndSet extends MojmirAutomatonFactory<Pair<Formula, ImmutableSet<Formula>>> {
    public MojmirAutomatonFactoryFromLTLAndSet(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    @Override
    public MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> createFrom(Pair<Formula, ImmutableSet<Formula>> from) {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> cachedResult = getFromCache(new Pair<>(from.getFirst(), from.getSecond()));
        if (null != cachedResult) {
            return cachedResult;
        }

        // get cached result with same transition system but other acceptance condition:
        cachedResult = getFromCache(new Pair<>(from.getFirst(), Collections.emptySet()));
        if (null != cachedResult) {
            ImmutableSet<Formula> curlyG = from.getSecond();
            LTLPropEquivalenceClass curlyGConjunction;
            if (curlyG.isEmpty()) {
                curlyGConjunction = new LTLPropEquivalenceClass(new Boolean(true));
            }
            else {
                List<Formula> curlyGConjuncts = new ArrayList<>();
                curlyG.forEach(ltlFormula -> {
                    curlyGConjuncts.add(new G(ltlFormula));
                });
                curlyGConjunction = new LTLPropEquivalenceClass(new And(curlyGConjuncts));
            }
            ImmutableSet.Builder<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> acceptingStatesBuilder = new ImmutableSet.Builder<>();
            cachedResult.getStates().forEach(state -> {
                if (state.getLabel().isTautology() || curlyGConjunction.implies(state.getLabel())) {
                    acceptingStatesBuilder.add(state);
                }
            });

            MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> modifiedResult = new MojmirAutomaton<>(cachedResult.getStates(), cachedResult.getInitialState(), acceptingStatesBuilder.build(), cachedResult.getAlphabet());
            putIntoCache(new Pair<>(from.getFirst(), from.getSecond()), modifiedResult);
            return modifiedResult;
        }

        Formula formula = from.getFirst();
        ImmutableSet<Formula> curlyG = from.getSecond();

        LTLPropEquivalenceClass initialLabel = new LTLPropEquivalenceClass(formula);
        MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> initialState =
                new MojmirAutomaton.State<>(initialLabel);
        Pair<Set<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, ImmutableSet<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>> reachResult = super.reach(initialState, getAlphabet(), curlyG);
        ImmutableSet<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> states = ImmutableSet.copyOf(reachResult.getFirst());

        cachedResult = new MojmirAutomaton<>(states, initialState, reachResult.getSecond(), getAlphabet());
        putIntoCache(new Pair<>(from.getFirst(), from.getSecond()), cachedResult);
        return cachedResult;
    }
}
