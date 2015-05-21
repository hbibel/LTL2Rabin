package ltl2rabin;

public class Pair<F, S> {
    private F first;
    private S second;

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
}
