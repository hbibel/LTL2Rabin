package ltl2rabin;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Set;

public abstract class LTLFactory<T> {
    public abstract Result buildLTL(T input);

    public static class Result {
        private LTLFormula ltlFormula;
        private ImmutableSet<Set<String>> alphabet;
        private Collection<LTLFormula> gFormulas;

        public Result(LTLFormula ltlFormula, Set<Set<String>> alphabet, Collection<LTLFormula> gFormulas) {
            this.ltlFormula = ltlFormula;
            this.alphabet = ImmutableSet.copyOf(alphabet);
            this.gFormulas = gFormulas;
        }

        public LTLFormula getLtlFormula() {
            return ltlFormula;
        }

        public ImmutableSet<Set<String>> getAlphabet() {
            return alphabet;
        }

        public Collection<LTLFormula> getgFormulas() {
            return gFormulas;
        }
    }
}
