package util;

import models.User;
import models.WorkflowRun;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.kurator.akka.Workflow;
import play.Play;
import play.api.libs.mailer.MailerClient;
import play.libs.mailer.MailerPlugin;
import play.libs.mailer.Email;

import javax.inject.Inject;
import java.io.File;

/**
 * Created by lowery on 3/2/2016.
 */
public class ResultNotificationMailer {
    public void sendNotification(User user, WorkflowRun workflowRun) throws EmailException {
        MailerClient mailerClient = Play.application().injector().instanceOf(MailerClient.class);

        Email email = new Email();
        email.setSubject("Kurator result for " + workflowRun.workflow);
        email.setFrom("Kurator Admin <from@email.com>");
        email.addTo(user.email);
        //email.addAttachment(workflowRun.result.getResultFileName(), new File(workflowRun.result.resultFile));
        email.setBodyText("This message is to notify you that the result for workflow run #" + workflowRun.id +
                " is available for download. See the attachment for the result file.");

        mailerClient.send(email);
    }
}
