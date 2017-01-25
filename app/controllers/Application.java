package controllers;

import dao.UserDao;
import dao.WorkflowDao;
import models.db.user.UserUpload;
import models.json.WorkflowDefinition;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Singleton;

import views.html.*;

import java.util.Collections;
import java.util.List;

/**
 * The main application controller
 */
@Singleton
public class Application extends Controller {

    /**
     * Index page.
     */
    @Security.Authenticated(Secured.class)
    public Result index() {
        // TODO: use ajax on a Workflows controller route to obtain this list instead
        List<WorkflowDefinition> workflows = Workflows.loadWorkflowFormDefinitions();
        Collections.sort(workflows);

        String uid = session().get("uid");
        List<UserUpload> userUploads = UserUpload.find.where().eq("user.id", uid).findList();

        return ok(
                index.render(workflows, userUploads)
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
}
