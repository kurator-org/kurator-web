package messages;

import akka.actor.ActorRef;

/**
 * Created by lowery on 8/2/16.
 */
public class RegisterListener {
    public final ActorRef listener;
    public RegisterListener(ActorRef listener) {
        this.listener = listener;
    }
}
