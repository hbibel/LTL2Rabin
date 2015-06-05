package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import ltl2rabin.LTL.*;
import ltl2rabin.LTL.Boolean;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.*;
import java.util.function.Supplier;

public class LTLRandomFactory extends LTLFactory<Integer> {
    private List<Supplier<Formula>> terminatingGeneratingFunctions = Arrays.asList(
            this::createRandomBoolean,
            this::chooseRandomVariable
    );
    private List<Supplier<Formula>> nonTerminatingGeneratingFunctions = Arrays.asList(
            this::createRandomConjunction,
            this::createRandomF,
            this::createRandomG,
            this::createRandomDisjunction,
            this::createRandomU,
            this::createRandomX
    );
    private List<Supplier<Formula>> gFreeNonTerminatingGeneratingFunctions = Arrays.asList(
            this::createRandomConjunction,
            this::createRandomF,
            this::createRandomDisjunction,
            this::createRandomU,
            this::createRandomX
    );
    private static final Random random = new Random();
    private List<Variable> variableSet = new ArrayList<>();
    private int maximumOperatorCount = 0;
    private int operatorCount = 0;
    // These strings represent Operators and should not be used as variable names:
    private static final Set<String> reservedNames = new HashSet<>(Arrays.asList("", "F", "G", "U", "X", "tt", "ff"));

    public LTLRandomFactory() {}

    /**
     *
     * @param complexity    Determines the desired complexity of the returned LTL formula. The value should be between
     *                      0 (terminal symbols) and 10 (very complex formulae with hundreds of distinct variables and
     *                      over 1000 (possibly over 2000) operators)
     * @return              An arbitrary random Formula object.
     */
    @Override
    public Result buildLTL(Integer complexity) {
        // Make sure the complexity does not surpass 10:
        complexity = complexity > 10 ? 10 : complexity;
        int numVariables = 1+10*complexity;
        // generate the variables:
        for (int i = 0; i < numVariables; i++) {
            String newVariableName = "";
            while (reservedNames.contains(newVariableName)) {
                // For 11 and 21 variables, one-letter names should be sufficient.
                // Also, it's not a tragedy if two variables have the same name.
                newVariableName = RandomStringUtils.randomAlphabetic(numVariables > 30 ? 2 : 1);
            }
            variableSet.add(new Variable(newVariableName));
        }
        maximumOperatorCount = (int) Math.pow(2.0, 1.0*complexity);
        Formula result = createRandomFormula();
        operatorCount=0;

        Set<Set<String>> alphabet = Sets.powerSet(ImmutableSet.of("There is no alphabet."));
        return new Result(result, alphabet, null);
    }

    // If the generated Formula should not contain any G-subformulae set the gFree parameter to true
    public Result buildLTL(Integer complexity, boolean gFree) {
        List<Supplier<Formula>> savedList = nonTerminatingGeneratingFunctions;
        if (gFree) {
            nonTerminatingGeneratingFunctions = gFreeNonTerminatingGeneratingFunctions;
        }
        Result result = buildLTL(complexity);
        if (gFree) {
            nonTerminatingGeneratingFunctions = savedList;
        }
        return result;
    }

    private Formula createRandomFormula() {
        // To get a positive random int, you have to specify any bound, otherwise negative results are possible
        int rnd = random.nextInt(1000);
        if (++operatorCount >= maximumOperatorCount) {
            return terminatingGeneratingFunctions.get(rnd % terminatingGeneratingFunctions.size()).get();
        }
        return nonTerminatingGeneratingFunctions.get(rnd % nonTerminatingGeneratingFunctions.size()).get();
    }

    private And createRandomConjunction() {
        int numberOfConjuncts = 2 + random.nextInt(4); // The number 4 is chosen arbitrarily.
        List<Formula> conjuncts = new ArrayList<>();
        for (int i = 0; i < numberOfConjuncts; i++) {
            conjuncts.add(createRandomFormula());
        }
        return new And(conjuncts);
    }

    private Boolean createRandomBoolean() {
        return new Boolean(random.nextInt() % 2 == 0);
    }

    private F createRandomF() {
        return new F(createRandomFormula());
    }

    private G createRandomG() {
        return new G(createRandomFormula());
    }

    private Or createRandomDisjunction() {
        int numberOfDisjuncts = 2 + random.nextInt(4); // The number 4 is chosen arbitrarily.
        List<Formula> disjuncts = new ArrayList<>();
        for (int i = 0; i < numberOfDisjuncts; i++) {
            disjuncts.add(createRandomFormula());
        }
        return new Or(disjuncts);
    }

    private U createRandomU() {
        return new U(createRandomFormula(), createRandomFormula());
    }

    private Variable chooseRandomVariable() {
        return variableSet.get(random.nextInt(variableSet.size()));
    }

    private X createRandomX() {
        return new X(createRandomFormula());
    }
}
