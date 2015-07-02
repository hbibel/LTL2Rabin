package ltl2rabin.LTL;

import com.google.common.collect.ImmutableSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * Abstract class to construct <code>Formula</code> objects.
 * @param <T> The type of the input object that is used to construct a Formula.
 */
public abstract class LTLFactory<T> {
    public abstract Result buildLTL(T input);

    /**
     * This struct-like class exists for the sole purpose of returning several objects at once.
     */
    public static class Result {
        private Formula ltlFormula;
        private ImmutableSet<Set<String>> alphabet;
        private Collection<Formula> gFormulas;

        /**
         * A Result is an object that bundles its parameters.
         * @param ltlFormula The formula that is constructed by the input of the <code>buildLTL</code> method of the
         *                   <code>LTLFactory</code>
         * @param alphabet All possible combinations of tokens.
         * @param gFormulas The set of Formulas of type <code>G</code> that appear within the ltlFormula.
         */
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
