package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Singleton;

import views.html.*;

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
}
