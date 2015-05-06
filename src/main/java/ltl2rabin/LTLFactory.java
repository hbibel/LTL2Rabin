package ltl2rabin;

import java.util.Collection;
import java.util.Set;

public abstract class LTLFactory<T> {
    public abstract Result buildLTL(T input);

    public static class Result {
        private LTLFormula ltlFormula;
        private Set<Set<String>> alphabet;
        private Collection<LTLGOperator> gFormulas;

        public Result(LTLFormula ltlFormula, Set<Set<String>> alphabet, Collection<LTLGOperator> gFormulas) {
            this.ltlFormula = ltlFormula;
            this.alphabet = alphabet;
            this.gFormulas = gFormulas;
        }

        public LTLFormula getLtlFormula() {
            return ltlFormula;
        }

        public Set<Set<String>> getAlphabet() {
            return alphabet;
        }

        public Collection<LTLGOperator> getgFormulas() {
            return gFormulas;
        }
    }
}
