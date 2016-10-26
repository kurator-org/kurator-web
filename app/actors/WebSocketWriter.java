package actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import messages.RegisterListener;

/**
 * Web socket writer class establishes a connection with a browser via Websockets
 */
public class WebSocketWriter extends UntypedActor {

    private final ActorRef out;
    private final ActorRef listensTo;

    public WebSocketWriter(ActorRef out, ActorRef listensTo) {
        this.out = out;
        this.listensTo = listensTo;
    }

    @Override
    public void preStart() throws Exception {
        System.out.println("Web socket actor pre start");
        listensTo.tell(new RegisterListener(self()), self());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            out.tell(message, ActorRef.noSender());
        }
    }
}
