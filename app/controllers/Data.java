package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.FormDefinition;
import models.UserUpload;
import models.WorkflowRun;
import org.json.simple.JSONObject;
import play.Routes;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.List;
import views.html.*;

/**
 * Created by lowery on 3/3/2016.
 */
public class Data extends Controller {
    public static Result uploads() {
        List<UserUpload> uploadList = UserUpload.findUploadsByUserId(Application.getCurrentUserId());

        ObjectNode response = Json.newObject();
        ArrayNode uploads = response.putArray("uploads");

        for (UserUpload userUpload : uploadList) {
            ObjectNode upload = Json.newObject();
            upload.put("id", userUpload.id);
            upload.put("filename", userUpload.fileName);
            uploads.add(upload);
        }

        return ok(uploads);
    }

    public static Result status(Long runId) {
        WorkflowRun run = WorkflowRun.find.byId(runId);

        ObjectNode response = Json.newObject();

        if (run.endTime == null) {
            response.put("status", "running");
        } else {
            response.put("status", "terminated");
            response.put("output", run.result.outputText);
            response.put("errors", run.result.errorText != null ? run.result.errorText : "");
        }

        return ok(response);
    }
}
