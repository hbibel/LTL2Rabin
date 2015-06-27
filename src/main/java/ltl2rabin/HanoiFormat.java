package ltl2rabin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.PropEquivalenceClass;

import java.util.*;

/**
 * This class provides static functions that convert Automata to Strings in the Hanoi Omega Automaton Format (hoaf).
 * For details about the hoaf, see https://github.com/adl/hoaf
 */
public class HanoiFormat {
    @SuppressWarnings("suspicious")
    public static String toHOAFv1(GDRA gdra) {
        StringBuilder resultBuilder = new StringBuilder();

        /**** Header ****/
        resultBuilder.append("HOA: v1\n");
        resultBuilder.append("tool: \"LTL2Rabin\"\n");
        resultBuilder.append("name: \"GDRA for ").append(gdra.getInitialState().getLabel().getFirst().getRepresentative()).append("\"\n");
        resultBuilder.append("properties: deterministic\n");
        resultBuilder.append("States: ").append(gdra.getStates().size()).append("\n"); // numbered consecutively from 0 upwards
        resultBuilder.append("Start: 0\n");
        resultBuilder.append("Acceptance: ");

        List<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> acc = new ArrayList<>();
        for (Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> pairs : gdra.getGdraCondition()) {
            Iterator<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> conjunctsIterator = pairs.iterator();
            Set<GDRA.Transition> inf = new HashSet<>();
            Set<GDRA.Transition> fin = new HashSet<>();
            if (conjunctsIterator.hasNext()) {
                Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> firstConjunct = conjunctsIterator.next();
                inf = firstConjunct.getSecond();
                fin = firstConjunct.getFirst();
            }
            while (conjunctsIterator.hasNext()) {
                Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> conjunct = conjunctsIterator.next();
                inf.retainAll(conjunct.getSecond());
                fin.retainAll(conjunct.getFirst());
            }
            acc.add(new Pair<>(fin, inf));
        }

        int acceptanceCount = 0;
        StringBuilder accStringBuilder = new StringBuilder();
        Iterator<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> accIterator = acc.iterator();
        while (accIterator.hasNext()) {
            Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> pair = accIterator.next();
            if ((!pair.getFirst().isEmpty()) & (!pair.getSecond().isEmpty())) {
                accStringBuilder.append('(');
            }
            if (!pair.getFirst().isEmpty()) {
                accStringBuilder.append("FIN(").append(acceptanceCount++).append(')');
                if (!pair.getSecond().isEmpty()) {
                    accStringBuilder.append(" & ");
                }
            }
            if (!pair.getSecond().isEmpty()) {
                accStringBuilder.append("INF(").append(acceptanceCount++).append(')');
            }
            if ((!pair.getFirst().isEmpty()) & (!pair.getSecond().isEmpty())) {
                accStringBuilder.append(')');
                if (accIterator.hasNext()) {
                    accStringBuilder.append(" | ");
                }
            }
        }
        resultBuilder.append(acceptanceCount).append(' ').append(accStringBuilder.toString()).append('\n');
        final ImmutableSet<? extends Set<String>> alphabet = gdra.getAlphabet();
        ImmutableSet.Builder<String> apBuilder = new ImmutableSet.Builder<>();
        StringBuilder apStringBuilder = new StringBuilder();
        final int[] apCount = {0};

        alphabet.forEach(letter -> {
            if (1 == letter.size()) {
                final String atomicProposition = letter.iterator().next();
                apBuilder.add(atomicProposition);
                apStringBuilder.append(" \"").append(letter.iterator().next()).append("\"");
                apCount[0]++;
            }
        });
        resultBuilder.append("AP: ").append(apCount[0]).append(apStringBuilder.toString()).append('\n');

        /**** Body ****/
        resultBuilder.append("--BODY--\n");
        ImmutableMap.Builder<GDRA.State, Integer> stateIntegerBuilder = new ImmutableMap.Builder<>();
        int stateCounter = 1;
        for (GDRA.State state : gdra.getStates()) {
            stateIntegerBuilder.put(state, state.equals(gdra.getInitialState()) ? 0 : stateCounter++);
        }
        Map<GDRA.State, Integer> stateIntegerMap = stateIntegerBuilder.build();

        for (GDRA.State state : gdra.getStates()) {
            /** Label **/
            resultBuilder.append("State: ").append(stateIntegerMap.get(state)).append(" \"").
            append(state.getLabel().getFirst().getRepresentative());
            if (!state.getLabel().getSecond().isEmpty()) {
                resultBuilder.append(" ::");
                state.getLabel().getSecond().forEach(slaveState -> {
                    int rank = 1;
                    for (MojmirAutomaton.State<PropEquivalenceClass, Set<String>> mState : slaveState.getLabel()) {
                        resultBuilder.append(' ').append(mState.getLabel().getRepresentative()).append('=').append(rank++);
                    }
                });
            }
            resultBuilder.append(";\"\n");
            /** Transitions **/
            state.getTransitions().forEach((letter, targetState) -> {
                resultBuilder.append('[');
                Iterator<String> apIterator = apBuilder.build().iterator();
                int apCounter = 0;
                while (apIterator.hasNext()) {
                    String token = apIterator.next();
                    if (letter.contains(token)) {
                        resultBuilder.append(apCounter++);
                    }
                    else {
                        resultBuilder.append('!').append(apCounter++);
                    }
                    if (apIterator.hasNext()) {
                        resultBuilder.append(" & ");
                    }
                }
                resultBuilder.append("] ").append(stateIntegerMap.get(targetState));
                resultBuilder.append(" {");
                int accCounter = 0;
                for (int i = 0; i < acc.size(); i++) {
                    Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> pair = acc.get(i);
                    if (!pair.getFirst().isEmpty()) {
                        if (pair.getFirst().contains(new GDRA.Transition(state, letter, (GDRA.State) targetState))) {
                            resultBuilder.append(accCounter).append(' ');
                        }
                        accCounter++;
                    }
                    if (!pair.getSecond().isEmpty()) {
                        if (pair.getSecond().contains(new GDRA.Transition(state, letter, (GDRA.State) targetState))) {
                            resultBuilder.append(accCounter).append(' ');
                        }
                        accCounter++;
                    }
                }
                resultBuilder.append("}\n");
            });

        }

        resultBuilder.append("--END--");
        return resultBuilder.toString();
    }
}
