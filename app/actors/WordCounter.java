package actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import messages.DeregisterListener;
import messages.OutputData;
import messages.RegisterListener;
import play.libs.Json;

import java.util.*;

/**
 * Count the occurrence of the top 25 words. Processes messages line by line.
 */
public class WordCounter extends UntypedActor {
    private ActorRef listener;

    private final Map<String, Long> count = new HashMap<>();

    public WordCounter() {
        System.out.println("Created new instance of WordCounter actor: " + self());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof OutputData) {

            String line = ((OutputData) message).line;

            // Count the words

            if (line != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, " ");
                while (tokenizer.hasMoreTokens()) {
                    String word = tokenizer.nextToken().toLowerCase();

                    if (!count.containsKey(word)) {
                        count.put(word, 1L);
                    } else {
                        count.put(word, (count.get(word) + 1));
                    }
                }

                // Sort by top words
                List<String> words = new ArrayList(count.keySet());
                Collections.sort(words);

                // Construct a JSON array
                ArrayNode wordCountArray = Json.newArray();

                int i = 0;
                for (String word : words) {
                    i++;

                    ObjectNode wordCount = Json.newObject();

                    wordCount.put("word", word);
                    wordCount.put("count", count.get(word));

                    wordCountArray.add(wordCount);

                    if (i > 25)
                        break;
                }

                if (listener != null)
                    listener.tell(new OutputData(wordCountArray.toString()), self());
            }
        } else if (message instanceof RegisterListener) {

                listener = ((RegisterListener) message).listener;
            System.out.println("Registered listener " + listener + " with actor " + self());

        } else if (message instanceof DeregisterListener) {

            listener = null; // TODO: Support for multiple listeners
            System.out.println("Deregistered listener " + listener + " from actor " + self());

        }
    }

    public static Props props() {
        return Props.create(new Creator<WordCounter>() {
            private static final long serialVersionUID = 1L;

            @Override
            public WordCounter create() throws Exception {
                return new WordCounter();
            }
        });
    }
}