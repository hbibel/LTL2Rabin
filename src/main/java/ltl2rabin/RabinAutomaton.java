package ltl2rabin;

import javafx.util.Pair;

import java.util.Set;
import java.util.function.BiFunction;

public class RabinAutomaton<T, U> {
    public BiFunction<T, Set<U>, T> transitionFunction;

    public class State{
        Set<Pair<MojmirAutomaton.State, Integer>> rankings;

        public Set<Pair<MojmirAutomaton.State, Integer>> getRankings() {
            return rankings;
        }
    }
}
