package ltl2rabin;

import java.util.Set;

public abstract class Alphabet implements Set<Set<String>> {
    // TODO: Refactor ALL occurrences of Set<Set<String>> to alphabet
    // This is an antipattern: http://www.ibm.com/developerworks/library/j-jtp02216/
}
