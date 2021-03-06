/** Application.java
 *
 * Copyright 2017 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import models.forms.ChangePass;
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

import static play.data.Form.form;

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

                        routes.javascript.Workflows.upload(),
                        routes.javascript.Workflows.deleteUpload(),

                        routes.javascript.AsyncController.scheduleRun(),
                        routes.javascript.Workflows.status(),
                        routes.javascript.Workflows.removeRuns(),

                        routes.javascript.Workflows.resultArtifacts(),
                        routes.javascript.Workflows.resultArchive(),
                        routes.javascript.Workflows.resultFile(),
                        routes.javascript.Workflows.workflowYaml(),

                        routes.javascript.Workflows.outputLog(),
                        routes.javascript.Workflows.errorLog(),
                        routes.javascript.Workflows.report(),
                        routes.javascript.Workflows.dataset(),

                        routes.javascript.Workflows.shareRun(),

                        routes.javascript.Workflows.deploy(),
                        routes.javascript.Workflows.deletePackage(),

                        routes.javascript.Users.manage(),
                        routes.javascript.Users.listGroups(),
                        routes.javascript.Users.createGroup(),
                        routes.javascript.Users.addUserToGroup(),
                        routes.javascript.Users.registerSubmit(),
                        routes.javascript.Users.listUploads(),
                        routes.javascript.Users.downloadFile(),
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
     * User settings page
     */
    public Result settings() {
        return ok(
                settings.render(form(ChangePass.class))
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
