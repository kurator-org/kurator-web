package util;

import models.db.user.User;
import models.db.workflow.WorkflowRun;
import org.apache.commons.mail.EmailException;
import play.api.libs.mailer.MailerClient;
import play.libs.mailer.Email;

import javax.inject.Inject;

/**
 * Created by lowery on 3/2/2016.
 */
public class ResultNotificationMailer {
    @Inject
    MailerClient mailerClient;

    public void sendNotification(User user, WorkflowRun workflowRun) throws EmailException {
        Email email = new Email();
        email.setSubject("Kurator result for " + workflowRun.getWorkflow());
        email.setFrom("Kurator Admin <from@email.com>");
        email.addTo(user.getEmail());
        //email.addAttachment(workflowRun.result.getResultFileName(), new File(workflowRun.result.resultFile));
        email.setBodyText("This message is to notify you that the result for workflow run #" + workflowRun.getId() +
                " is available for download. See the attachment for the result file.");

        mailerClient.send(email);
    }
}
