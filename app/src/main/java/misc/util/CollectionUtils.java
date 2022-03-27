package misc.util;

import java.util.HashMap;
import java.util.Map;

public class CollectionUtils {
    public static String characterArrayToString(Character[] characterArray) {
        StringBuilder sb = new StringBuilder(characterArray.length);
        for (Character c : characterArray) {
            sb.append(c.charValue());
        }

        return sb.toString();
    }

    public static Map<Character, Integer> countCharactersInString(Character[] characters) {
        Map<Character, Integer> charCount = new HashMap<>();
        for (Character c : characters) {
            if (charCount.containsKey(c)) {
                charCount.put(c, charCount.get(c) + 1);
            }
            else {
                charCount.put(c, 1);
            }
        }

        return charCount;
    }
}
