package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.FormDefinition;
import models.UserUpload;
import models.Workflow;
import models.WorkflowRun;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import play.Routes;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    public static Result status(Long uid) {
        List<WorkflowRun> workflowRuns = WorkflowRun.find.where().eq("user.id", uid).findList();

        ArrayNode response = Json.newArray();
        for (WorkflowRun run : workflowRuns) {
            ObjectNode runJson = Json.newObject();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

            runJson.put("id", run.id);
            runJson.put("workflow", run.workflow.title);
            runJson.put("startTime", dateFormat.format(run.startTime));
            runJson.put("endTime", run.endTime != null ? dateFormat.format(run.endTime) : null);
            runJson.put("hasResult", run.result != null);
            if (run.result != null) {
                runJson.put("hasOutput", !run.result.outputText.equals(""));
                runJson.put("hasErrors", !run.result.errorText.equals(""));
            } else {
                runJson.put("hasOutput", false);
                runJson.put("hasErrors", false);
            }

            response.add(runJson);
        }

        return ok(response);

    }
}
