package ltl2rabin;

import java.util.Objects;

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

    public static <F, S> F getFirst(Pair<F, S> p) {
        return p.getFirst();
    }

    public static <F, S> S getSecond(Pair<F, S> p) {
        return p.getSecond();
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
