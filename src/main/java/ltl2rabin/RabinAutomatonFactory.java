package ltl2rabin;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public abstract class RabinAutomatonFactory<F, A, L> extends AutomatonFactory<F, A, L> {

    public RabinAutomatonFactory(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }
}
