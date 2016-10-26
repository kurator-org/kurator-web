package actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import messages.DeregisterListener;
import messages.OutputData;
import messages.RegisterListener;

/**
 * OutputAdapter interfaces with the WebSocketWriter in the web app.
 */
public class OutputAdapter extends UntypedActor {
    private ActorRef listener;

    public OutputAdapter() {
        System.out.println("Created new instance of OutputAdapter: " + self());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof OutputData) {

            if (listener != null)
                listener.tell(((OutputData) message).line, self());

        } else if (message instanceof RegisterListener) {

            listener = ((RegisterListener) message).listener;
            System.out.println("Registered listener " + listener + " with actor " + self());

        } else if (message instanceof DeregisterListener) {

            listener = null; // TODO: Support for multiple listeners
            System.out.println("Deregistered listener " + listener + " from actor " + self());

        }
    }

    public static Props props() {
        return Props.create(new Creator<OutputAdapter>() {
            private static final long serialVersionUID = 1L;

            @Override
            public OutputAdapter create() throws Exception {
                return new OutputAdapter();
            }
        });
    }
}
