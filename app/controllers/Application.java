package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import config.WorkflowConfig;
import dao.UserDao;
import dao.WorkflowDao;
import models.db.user.UserUpload;
import models.json.WorkflowDefinition;
import play.Routes;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Singleton;

import play.routing.JavaScriptReverseRouter;
import views.html.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The main application controller
 */
@Singleton
public class Application extends Controller {

    public Result jsRoutes() {
        return ok(
                JavaScriptReverseRouter.create("jsRoutes",
                        routes.javascript.Application.auth(),
                        routes.javascript.Application.data(),

                        routes.javascript.Workflows.list(),
                        routes.javascript.Workflows.runWorkflow(),
                        routes.javascript.Workflows.status(),

                        routes.javascript.Workflows.resultArchive(),
                        routes.javascript.Workflows.outputLog(),
                        routes.javascript.Workflows.errorLog(),

                        routes.javascript.Workflows.deploy(),
                        routes.javascript.Workflows.deletePackage(),

                        routes.javascript.Users.manage(),
                        routes.javascript.Users.registerSubmit(),

                        routes.javascript.Users.authenticate()
                )
        ).as("text/javascript");
    }

    /**
     * Index page.
     */
    public Result index() {
        // TODO: use ajax on a Workflows controller route to obtain this list instead
        List<WorkflowDefinition> workflows = Workflows.loadWorkflowFormDefinitions();
        Collections.sort(workflows);

        String uid = session().get("uid");
        List<UserUpload> userUploads = UserUpload.find.where().eq("user.id", uid).findList();

        return ok(
                index.render()
        );
    }

    /**
     * The page for the workflow builder tool.
     */
    @Security.Authenticated(Secured.class)
    public Result builder() {
        return ok(
                builder.render()
        );
    }

    /**
     * DQ Reports and workflow run summary page
     */
    public Result summary(long runId) {
        return ok(
                summary.render(runId)
        );
    }

    //@Security.Authenticated(SecuredBackbone.class)
    public Result test() {
        return ok(
                workflows.render()
        );
    }

    public Result data() {
        WorkflowDefinition workflowDef1 = new WorkflowDefinition();
        workflowDef1.setDocumentation("https://github.com/kurator-org/kurator-validation/wiki/CSV-File-Darwinizer");
        workflowDef1.setTitle("CSV File Darwinizer");
        workflowDef1.setName("darwinize_workflow");
        workflowDef1.setInstructions("Create a file that substitutes standard Darwin Core field names for the fields in an input file.");

        WorkflowDefinition workflowDef2 = new WorkflowDefinition();
        workflowDef2.setDocumentation("https://github.com/kurator-org/kurator-validation/wiki/CSV-File-Darwinizer");
        workflowDef2.setTitle("CSV File Darwinizer");
        workflowDef2.setName("darwinize_workflow");
        workflowDef2.setInstructions("Create a file that substitutes standard Darwin Core field names for the fields in an input file.");

        List<WorkflowDefinition> workflows = new ArrayList<>();
        workflows.add(workflowDef1);
        workflows.add(workflowDef2);

        ArrayNode arr = Json.newArray();
        arr.add(Json.newObject().put("name", "David"));
        arr.add(Json.newObject().put("name", "Bob"));

        return ok(
                Json.toJson(workflows)
        );
    }

    public Result auth() {
        ObjectNode json = Json.newObject();
        json.put("user", "test");
        return ok(json);
    }
}
