package ltl2rabin;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MojmirAutomaton {
    public TransitionFunction transitionFunction;

    public class TransitionFunction implements BiFunction<State,Character,State > {

        @Override
        public State apply(State state, Character character) {
            return null;
        }

        @Override
        public <V> BiFunction<State, Character, V> andThen(Function<? super State, ? extends V> after) {
            return null;
        }
    }

    private class State {

    }
}
