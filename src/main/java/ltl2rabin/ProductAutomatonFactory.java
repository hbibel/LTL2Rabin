package ltl2rabin;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class ProductAutomatonFactory extends RabinAutomatonFactory<Iterable<RabinAutomaton<?, Set<String>>>,
        Collection<RabinAutomaton.State<?, Set<String>>>,
        Set<String>> {
    public RabinAutomaton<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>> createFrom(Iterable<RabinAutomaton<?, Set<String>>> rabinAutomata,
                                                                                                     ImmutableSet<Set<String>> alphabet) {
        // create the product automaton of a collection of rabin automata
        List<RabinAutomaton.State<?, Set<String>>> initialRaStates = new ArrayList<>();
        rabinAutomata.iterator().forEachRemaining(rabinAutomaton -> initialRaStates.add(rabinAutomaton.getInitialState()));
        RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>> initialState = new RabinAutomaton.State<>(initialRaStates);
        List<RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>>> resultStates = new ArrayList<>();

        Queue<RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>>> stateQueue = new ConcurrentLinkedQueue<>();
        stateQueue.add(initialState);
        while (!stateQueue.isEmpty()) {
            RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>> tempState = stateQueue.poll();
            for (Set<String> letter : alphabet) {
                ImmutableList.Builder<RabinAutomaton.State<?, Set<String>>> tempRaStates = new ImmutableList.Builder<>();
                tempState.getLabel().forEach(rs -> tempRaStates.add(rs.readLetter(letter)));
                RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>> newState = new RabinAutomaton.State<>(tempRaStates.build());
                int index = resultStates.indexOf(newState);
                if (index >= 0) {
                    newState = resultStates.get(index);
                }
                else {
                    resultStates.add(newState);
                }
                tempState.setTransition(letter, newState);
                if (newState.transitionCount() < alphabet.size()) {
                    stateQueue.add(newState);
                }
            }
        }

        ImmutableCollection<RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>>> states = ImmutableSet.copyOf(resultStates);

        ImmutableSet.Builder<Automaton.Transition<RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>>, Set<String>>> avoidBuilder = new ImmutableSet.Builder<>();
        ImmutableSet.Builder<Automaton.Transition<RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>>, Set<String>>> reachBuilder = new ImmutableSet.Builder<>();
        for (RabinAutomaton<?, Set<String>> ra : rabinAutomata) {
            for (Automaton.Transition<RabinAutomaton.State, Set<String>> avoidTransition : ra.getRabinPair().getFirst()) {
                Collection<RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>>> avoidFrom = states.stream().filter(s -> s.getLabel().contains(avoidTransition.getFrom())).collect(Collectors.toList());
                avoidFrom.forEach(s -> {
                    avoidBuilder.add(new Automaton.Transition<>(s, avoidTransition.getLetter(), s.readLetter(avoidTransition.getLetter())));
                });
            }
            for (Automaton.Transition<RabinAutomaton.State, Set<String>> reachTransition : ra.getRabinPair().getSecond()) {
                Collection<RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>>> reachFrom = states.stream().filter(s -> s.getLabel().contains(reachTransition.getFrom())).collect(Collectors.toList());
                reachFrom.forEach(s -> {
                    reachBuilder.add(new Automaton.Transition<>(s, reachTransition.getLetter(), s.readLetter(reachTransition.getLetter())));
                });
            }
        }
        Pair<ImmutableSet<Automaton.Transition<RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>>, Set<String>>>,
                ImmutableSet<Automaton.Transition<RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>>, Set<String>>>> rabinPair
                = new Pair<>(avoidBuilder.build(), reachBuilder.build());

        return new RabinAutomaton<>(states, initialState, rabinPair, alphabet);
    }

    @Override
    public Automaton<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>> createFrom(Iterable<RabinAutomaton<?, Set<String>>> from) {
        return createFrom(from, from.iterator().next().getAlphabet());
    }
}
