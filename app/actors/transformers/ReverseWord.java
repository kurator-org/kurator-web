package actors.transformers;

import java.util.StringTokenizer;

/**
 * Reverse all words in the line
 */
public class ReverseWord implements StringTransformerStrategy {
    @Override
    public String transform(String line) {
        StringBuilder reversed = new StringBuilder();

        StringTokenizer tokenizer = new StringTokenizer(line, " ");
        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken().toLowerCase();

            for (int i = word.length(); i > 0; i--) {
                reversed.append(word.charAt(i-1));
            }
        }

        return reversed.toString();
    }
}
