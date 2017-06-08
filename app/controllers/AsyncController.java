package controllers;

import akka.actor.ActorSystem;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.Duration;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AsyncController extends Controller {
    private final ActorSystem system;
    private final ExecutionContextExecutor exec;

    @Inject
    public AsyncController(ActorSystem system, ExecutionContextExecutor exec) {
        this.system = system;
        this.exec = exec;
    }

    public Result scheduleRun() {
        final String runId = UUID.randomUUID().toString();

        system.dispatcher().execute(() -> {
            Logger.debug("started run: " + runId);

            try {
                // TODO: run workflow here and save result to db
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Logger.debug("ended run: " + runId);
        });

        ObjectNode response = Json.newObject();
        response.put("runId", runId);

        return ok(response);
    }
}
