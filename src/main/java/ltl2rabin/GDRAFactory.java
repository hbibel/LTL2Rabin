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
        ImmutableSet<Set<String>> alphabet = ImmutableSet.copyOf(parserResult.getAlphabet());
        LTLFormula phi = parserResult.getLtlFormula();

        ImmutableSet.Builder<RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>> slavesBuilder = new ImmutableSet.Builder<>();
        MojmirAutomatonListFactory mojmirAutomatonListFactory = new MojmirAutomatonListFactory(curlyGSet, alphabet);
        gSet.forEach(psi ->
            mojmirAutomatonListFactory.createFrom(psi).forEach(ma -> slavesBuilder.add(new RabinAutomatonFromMojmirFactory().createFrom(ma)))
        );
        ImmutableSet<RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>> slaves
                = slavesBuilder.build();

        // TODO
        return null;
    }
}
