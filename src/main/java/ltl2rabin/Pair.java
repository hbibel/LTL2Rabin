package ltl2rabin;

import java.util.Objects;

/**
 * A 2-tuple
 * @param <F>    Type of the first object (arbitrary).
 * @param <S>    Type of the second object (arbitrary).
 */
public class Pair<F, S> {
    private final F first;
    private final S second;

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
