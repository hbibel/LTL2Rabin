package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.*;

import static org.mockito.Mockito.mock;

@SuppressWarnings("unchecked")
public abstract class AutomatonMockFactory<T> {

    public static ImmutableSet<Set<String>> generateAlphabet (int numLetters) {
        Set<String> result = new HashSet<>();
        for (int i = 0; i < numLetters; i++) {
            result.add(Character.toString((char) ('a' + i)));
        }
        return ImmutableSet.copyOf(Sets.powerSet(result));
    }

    public static List<Set<String>> createWord(String... letters) {
        List<Set<String>> result = new ArrayList<>();
        for (String l : letters) {
            char[] parts = l.toCharArray();
            List<String> partsAsStrings = new ArrayList<>();
            for (int i = 0; i < parts.length; i++) {
                partsAsStrings.add("" + parts[i]);
            }
            result.add(new HashSet<>(partsAsStrings));
        }
        return result;
    }
}
