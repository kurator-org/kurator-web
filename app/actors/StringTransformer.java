package actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import messages.DeregisterListener;
import messages.OutputData;
import messages.RegisterListener;
import messages.SetStrategy;
import actors.transformers.PigLatin;
import actors.transformers.ReverseWord;
import actors.transformers.StringTransformerStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * String transforming actor will transform a string using a configurable strategy
 */
public class StringTransformer extends UntypedActor {
    private ActorRef listener;
    private StringTransformerStrategy strategy = new ReverseWord(); // Default
    private Map<String, StringTransformerStrategy> strategyMap;

    public StringTransformer() {
        System.out.println("Created new instance of StringTransformer: " + self() + ", defaulting to ReverseWord strategy");

        strategyMap = new HashMap<>();
        strategyMap.put("PigLatin", new PigLatin());
        strategyMap.put("ReverseWord", new ReverseWord());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof OutputData) {
            // Transform this line of text using the strategy class

            String line = ((OutputData) message).line;
            String output = strategy.transform(line);

            if (listener != null)
                listener.tell(new OutputData(output), self());

        } else if (message instanceof RegisterListener) {

            listener = ((RegisterListener) message).listener;
            System.out.println("Registered listener " + listener + " with actor " + self());

        } else if (message instanceof DeregisterListener) {

            listener = null; // TODO: Support for multiple listeners
            System.out.println("Deregistered listener " + listener + " from actor " + self());

        } else if (message instanceof SetStrategy) {
            // Set the strategy implementation that this actor should use

            String name = ((SetStrategy) message).strategy;
            strategy = strategyMap.get(name);
            System.out.println("Changing StringTransformer strategy to " + name);

        }
    }

    public static Props props() {
        return Props.create(new Creator<StringTransformer>() {
            private static final long serialVersionUID = 1L;

            @Override
            public StringTransformer create() throws Exception {
                return new StringTransformer();
            }
        });
    }
}
