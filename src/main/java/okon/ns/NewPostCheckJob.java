package okon.ns;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.FolderTraversal;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.schema.FolderSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FolderView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.net.URI;

public class NewPostCheckJob implements Job {
    private static final Logger logger = LogManager.getLogger(NotificationSender.class);

    @Override
    public void execute(JobExecutionContext jExeCtx) throws JobExecutionException {
        try {
            logger.info("In get_new_messages()");
            ExchangeService service = createConnection();
            int unreaded = getTotalUnreadCount(service);
            if (unreaded > 0) {
                doSend(service, "nowe wiadomości e-mail", WorkingEnvironment.getTargetEmail(), null, "Nowe wiadomości: " + unreaded, null);
            }
            logger.info("End of get_new_messages():SUCCEED");
        } catch (Exception e) {
            logger.error("End of get_new_messages():FAILED");
            throw new JobExecutionException(e);
        }
    }

    private static ExchangeService createConnection() throws JobExecutionException {
        ExchangeService service = new ExchangeService();
        try {
            ExchangeCredentials credentials = new WebCredentials(WorkingEnvironment.getEmail(), WorkingEnvironment.getPassword());
            service.setUrl(new URI(WorkingEnvironment.getServer()));
            service.setCredentials(credentials);
            service.setTraceEnabled(true);
        } catch (Exception e){
            throw new JobExecutionException(e);
        }
        return service;
    }

    public static int getTotalUnreadCount(ExchangeService ewsConnector) throws JobExecutionException {
        int result = 0;
        try {
            int pagedView = 1000;
            FolderView fv = new FolderView(pagedView);
            fv.setTraversal(FolderTraversal.Deep);
            fv.setPropertySet(new PropertySet(BasePropertySet.IdOnly, FolderSchema.UnreadCount, FolderSchema.DisplayName));
            FindFoldersResults findResults = ewsConnector.findFolders(WellKnownFolderName.Inbox, fv);
            for (Folder folder : findResults.getFolders()) {
                try {
                    result += folder.getUnreadCount();
                } catch (Exception ex) {

                }
            }
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
        return result;
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
