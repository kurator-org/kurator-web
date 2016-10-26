package actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import messages.*;
import scala.concurrent.duration.Duration;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * File reader actor will read a text file line by line.
 *
 */
public class FileReader extends UntypedActor {
    private BufferedReader reader;
    private ActorRef listener;

    public FileReader() {
        System.out.println("Created new instance of file reader: " + self());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ReadFile) {
            // Set up the actor to start reading files

            FileInputStream fis = new FileInputStream(((ReadFile) message).filePath);
            reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

            self().tell(new ReadMore(), sender());

        } else if (message instanceof ReadMore) {
            // Read another line from the file and send to the downstream actor

            String line = reader.readLine();

            if (line != null) {
                if (listener != null)
                    listener.tell(new OutputData(line), self());

                // Throttle input, otherwise it's too fast
                getContext().system().scheduler().scheduleOnce(Duration.create(5, TimeUnit.MILLISECONDS),
                        self(), new ReadMore(), getContext().system().dispatcher(), null);

            } else {
                System.out.println("End of file.");
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
        return Props.create(new Creator<FileReader>() {
            private static final long serialVersionUID = 1L;

            @Override
            public FileReader create() throws Exception {
                return new FileReader();
            }
        });
    }
}
