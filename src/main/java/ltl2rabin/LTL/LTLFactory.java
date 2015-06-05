package ltl2rabin.LTL;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Set;

public abstract class LTLFactory<T> {
    public abstract Result buildLTL(T input);

    public static class Result {
        private Formula ltlFormula;
        private ImmutableSet<Set<String>> alphabet;
        private Collection<Formula> gFormulas;

        public Result(Formula ltlFormula, Set<Set<String>> alphabet, Collection<Formula> gFormulas) {
            this.ltlFormula = ltlFormula;
            this.alphabet = ImmutableSet.copyOf(alphabet);
            this.gFormulas = gFormulas;
        }

        public Formula getLtlFormula() {
            return ltlFormula;
        }

        public ImmutableSet<Set<String>> getAlphabet() {
            return alphabet;
        }

        public Collection<Formula> getgFormulas() {
            return gFormulas;
        }
    }
}
