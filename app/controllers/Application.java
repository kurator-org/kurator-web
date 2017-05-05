package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import be.objectify.deadbolt.java.actions.SubjectPresent;
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
                        routes.javascript.Users.checkAuth(),

                        routes.javascript.Workflows.list(),
                        routes.javascript.Workflows.runWorkflow(),
                        routes.javascript.Workflows.status(),

                        routes.javascript.Workflows.resultArtifacts(),
                        routes.javascript.Workflows.resultArchive(),
                        routes.javascript.Workflows.resultFile(),

                        routes.javascript.Workflows.outputLog(),
                        routes.javascript.Workflows.errorLog(),
                        routes.javascript.Workflows.report(),
                        routes.javascript.Workflows.dataset(),

                        routes.javascript.Workflows.deploy(),
                        routes.javascript.Workflows.deletePackage(),

                        routes.javascript.Users.manage(),
                        routes.javascript.Users.registerSubmit(),
                        routes.javascript.Users.createWorkshop(),

                        routes.javascript.Users.login(),
                        routes.javascript.Users.authenticate(),

                        routes.javascript.Assets.at()
                )
        ).as("text/javascript");
    }

    /**
     * Index page.
     */
    public Result index() {
        return ok(
                index.render()
        );
    }

    /**
     * The page for the workflow builder tool.
     */
    @SubjectPresent
    public Result builder() {
        return ok(
                builder.render()
        );
    }

    @SubjectPresent
    public Result test() {
        return ok(
                workflows.render()
        );
    }

    public Result help() {
        return ok(
                help.render()
        );
    }

    public Result about() {
        return ok(
                about.render()
        );
    }

    @Restrict({@Group("ADMIN")})
    public Result admin() {
        return ok(
                admin.render()
        );
    }
}
