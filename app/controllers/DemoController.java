package controllers;

import actors.*;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import messages.DeregisterListener;
import messages.ReadFile;
import messages.RegisterListener;
import messages.SetStrategy;
import play.libs.Json;
import play.mvc.*;
import scala.concurrent.ExecutionContextExecutor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import views.html.*;

/**
 * Main controller handles requests listed in the routes file
 *
 */
@Singleton
public class DemoController extends Controller {

    private final ActorSystem actorSystem;

    private Map<String, Props> actorRegistry = new HashMap<>();

    private Map<String, ActorRef> actors = new HashMap<>();
    private List<ActorRef> workflow = new ArrayList<>();

    @Inject
    public DemoController(ActorSystem actorSystem, ExecutionContextExecutor exec) {
        this.actorSystem = actorSystem;

        // Hardcoded list of actors by name

        actorRegistry.put("FileReader", FileReader.props());
        actorRegistry.put("WordCounter", WordCounter.props());
        actorRegistry.put("StringTransformer", StringTransformer.props());
        actorRegistry.put("OutputAdapter", OutputAdapter.props());
    }

    /**
     * Set the strategy that the named actor should use by sending the actor a SetStrategy message
     *
     * @param name
     * @param strategy
     */
    public Result set(String name, String strategy) {
        ActorRef actorRef = actors.get(name);
        actorRef.tell(new SetStrategy(strategy), ActorRef.noSender());

        return ok();
    }

    /**
     * Register target as a listener of the source actor
     *
     * @param source
     * @param target
     * @return
     */
    public Result connect(String source, String target) {
        actors.get(source).tell(new RegisterListener(actors.get(target)), actors.get(target));
        return ok();
    }

    /**
     * Deregister target as a listener of the source actor
     *
     * @param source
     * @param target
     * @return
     */
    public Result detach(String source, String target) {
        actors.get(source).tell(new DeregisterListener(actors.get(target)), actors.get(target));
        return ok();
    }

    /**
     * Main page
     */
    public Result demo() {
        return ok(builder.render());
    }

    // Opens a web socket connection with the browser via akka actor
    public LegacyWebSocket<String> socket() {

        // Register the websocket as a listener of the last actor in the workflow
        final ActorRef last = workflow.get(workflow.size()-1);

        return WebSocket.withActor(new Function<ActorRef, Props>() {
            @Override
            public Props apply(ActorRef actorRef) {
                return Props.create(WebSocketWriter.class, actorRef, last);
            }
        });
    }

    /**
     * The add endpoint will create an instance of an actor.
     *
     * @param name identifies the actor in the map
     * @return json response
     */
    public Result add(String name) {
        Props props = actorRegistry.get(name);
        ActorRef actorRef = actorSystem.actorOf(props);

        workflow.add(actorRef);
        actors.put(name, actorRef);

        ObjectNode json = Json.newObject();

        String type = "actor";

        if (name.equals("FileReader")) {
            type = "input";
        } else if (name.equals("OutputAdapter")) {
            type = "output";
        }

        json.put("actor", name);
        json.put("type", type);

        if (name.equals("StringTransformer")) {
            ArrayNode strategies = Json.newArray();
            strategies.add("ReverseWord");
            strategies.add("PigLatin");

            json.set("strategies", strategies);
        }

        return ok(json);
    }

    /**
     * Stop and remove an instance of the actor from the workflow
     *
     * @param name actor name
     */
    public Result remove(String name) {
        ActorRef actorRef = actors.remove(name);

        actorSystem.stop(actorRef);
        workflow.remove(actorRef);

        return ok();
    }

    /**
     * Upload a file and send the path to the first actor in the workflow
     *
     */
    public Result upload() {
        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> file = body.getFile("file");

        ReadFile message = new ReadFile(file.getFile().getAbsolutePath());

        workflow.get(0).tell(message, ActorRef.noSender());

        return ok("File upload success!");
    }

    /**
     * Responds with all actor metadata in the list as json
     *
     */
    public Result list() {
        return ok(Json.toJson(actorRegistry.keySet()));
    }
}
