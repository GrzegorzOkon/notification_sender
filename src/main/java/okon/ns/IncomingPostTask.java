package okon.ns;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.TimerTask;

public class IncomingPostTask extends TimerTask {
    private static final Logger logger = LogManager.getLogger(IncomingPostTask.class);

    @Override
    public void run() {
        getNewMessages();
    }

    private static void getNewMessages() {
        logger.info("In get_new_messages()");
        try {
            ExchangeService service = new ExchangeService();
            ExchangeCredentials credentials = new WebCredentials(WorkingEnvironment.getEmail(), WorkingEnvironment.getPassword());
            service.setUrl(new URI(WorkingEnvironment.getServer()));
            service.setCredentials(credentials);
            service.setTraceEnabled(true);
            Folder inbox = Folder.bind(service, WellKnownFolderName.Inbox);

            int unreaded = inbox.getUnreadCount();
            if (unreaded > 0) {
                doSend(service, "nowe wiadomości e-mail", WorkingEnvironment.getTargetEmail(), null, "Nowe wiadomości: " + unreaded, null);
            }
            logger.info("End of get_new_messages():SUCCEED");
        } catch (Exception e) {
            logger.error("End of get_new_messages():FAILED");
        }
    }

    public static void doSend(ExchangeService service, String subject, String to, String[]cc, String bodyText,
                               String[]attachmentPath) throws Exception {
        EmailMessage msg = new EmailMessage(service);
        msg.setSubject(subject);
        MessageBody body = MessageBody.getMessageBodyFromText(bodyText);
        body.setBodyType(BodyType.HTML);
        msg.setBody(body);
        msg.getToRecipients().add(to);
        if (cc != null) {
            for (String s : cc) {
                msg.getCcRecipients().add(s);
            }
        }
        if (attachmentPath != null && attachmentPath.length > 0) {
            for (int a = 0; a < attachmentPath.length; a++) {
                msg.getAttachments().addFileAttachment(attachmentPath[a]);
            }
        }
        msg.send();
    }
}
