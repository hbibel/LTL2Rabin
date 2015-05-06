package ltl2rabin;


import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class GDRAFactory extends RabinAutomatonFactory<String,
        Collection<RabinAutomaton.State<?, Set<String>>>,
        Set<String>> {

    public RabinAutomaton<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>> createFrom(String from) {
        LTLFactoryFromString ltlFactory = new LTLFactoryFromString();
        LTLFactory.Result parserResult = ltlFactory.buildLTL(from);

        ImmutableSet<LTLGOperator> gSet = (new ImmutableSet.Builder<LTLGOperator>())
                .addAll(parserResult.getgFormulas()).build();
        ImmutableSet<Set<LTLGOperator>> curlyGSet = (new ImmutableSet.Builder<Set<LTLGOperator>>())
                .addAll(Sets.powerSet(gSet)).build();
        Set<Set<String>> alphabet = parserResult.getAlphabet();
        LTLFormula phi = parserResult.getLtlFormula();

        ImmutableSet.Builder<RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>> slavesBuilder = new ImmutableSet.Builder<>();
        curlyGSet.forEach(psi -> slavesBuilder.add(
                (new RabinAutomatonFromMojmirFactory()).createFrom((new MojmirAutomatonFactoryFromLTL()).createFrom(new Pair<>(phi, alphabet)))
        ));
        ImmutableSet<RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>> slaves
                = slavesBuilder.build();

        // TODO
        return null;
    }
}
