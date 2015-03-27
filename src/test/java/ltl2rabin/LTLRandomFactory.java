package ltl2rabin;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.*;
import java.util.function.Supplier;

public class LTLRandomFactory extends LTLFactory<Integer> {
    private List<Supplier<LTLFormula>> terminatingGeneratingFunctions = Arrays.asList(
            this::createRandomBoolean,
            this::chooseRandomVariable
    );
    private List<Supplier<LTLFormula>> nonTerminatingGeneratingFunctions = Arrays.asList(
            this::createRandomConjunction,
            this::createRandomF,
            this::createRandomG,
            this::createRandomDisjunction,
            this::createRandomU,
            this::createRandomX
    );
    private List<Supplier<LTLFormula>> gFreeNonTerminatingGeneratingFunctions = Arrays.asList(
            this::createRandomConjunction,
            this::createRandomF,
            this::createRandomDisjunction,
            this::createRandomU,
            this::createRandomX
    );
    private static final Random random = new Random();
    private List<LTLVariable> variableSet = new ArrayList<>();
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
     * @return              An arbitrary random LTLFormula object.
     */
    @Override
    public LTLFormula buildLTL(Integer complexity) {
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
            variableSet.add(new LTLVariable(newVariableName));
        }
        maximumOperatorCount = (int) Math.pow(2.0, 1.0*complexity);
        LTLFormula result = createRandomFormula();
        operatorCount=0;

        return result;
    }

    // If the generated LTLFormula should not contain any G-subformulae set the gFree parameter to true
    public LTLFormula buildLTL(Integer complexity, boolean gFree) {
        List<Supplier<LTLFormula>> savedList = nonTerminatingGeneratingFunctions;
        if (gFree) {
            nonTerminatingGeneratingFunctions = gFreeNonTerminatingGeneratingFunctions;
        }
        LTLFormula result = buildLTL(complexity);
        if (gFree) {
            nonTerminatingGeneratingFunctions = savedList;
        }
        return result;
    }

    private LTLFormula createRandomFormula() {
        // To get a positive random int, you have to specify any bound, otherwise negative results are possible
        int rnd = random.nextInt(1000);
        if (++operatorCount >= maximumOperatorCount) {
            return terminatingGeneratingFunctions.get(rnd % terminatingGeneratingFunctions.size()).get();
        }
        return nonTerminatingGeneratingFunctions.get(rnd % nonTerminatingGeneratingFunctions.size()).get();
    }

    private LTLAnd createRandomConjunction() {
        int numberOfConjuncts = 2 + random.nextInt(4); // The number 4 is chosen arbitrarily.
        List<LTLFormula> conjuncts = new ArrayList<>();
        for (int i = 0; i < numberOfConjuncts; i++) {
            conjuncts.add(createRandomFormula());
        }
        return new LTLAnd(conjuncts);
    }

    private LTLBoolean createRandomBoolean() {
        return new LTLBoolean(random.nextInt() % 2 == 0);
    }

    private LTLFOperator createRandomF() {
        return new LTLFOperator(createRandomFormula());
    }

    private LTLGOperator createRandomG() {
        return new LTLGOperator(createRandomFormula());
    }

    private LTLOr createRandomDisjunction() {
        int numberOfDisjuncts = 2 + random.nextInt(4); // The number 4 is chosen arbitrarily.
        List<LTLFormula> disjuncts = new ArrayList<>();
        for (int i = 0; i < numberOfDisjuncts; i++) {
            disjuncts.add(createRandomFormula());
        }
        return new LTLOr(disjuncts);
    }

    private LTLUOperator createRandomU() {
        return new LTLUOperator(createRandomFormula(), createRandomFormula());
    }

    private LTLVariable chooseRandomVariable() {
        return variableSet.get(random.nextInt(variableSet.size()));
    }

    private LTLXOperator createRandomX() {
        return new LTLXOperator(createRandomFormula());
    }
}
