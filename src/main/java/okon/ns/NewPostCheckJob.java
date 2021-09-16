package okon.ns;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.FolderTraversal;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.FolderSchema;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.FolderView;
import microsoft.exchange.webservices.data.search.ItemView;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewPostCheckJob implements Job {
    private static final Logger logger = LogManager.getLogger(NotificationSender.class);

    @Override
    public void execute(JobExecutionContext jExeCtx) throws JobExecutionException {
        try {
            logger.info("In get_new_messages()");
            ExchangeService service = createConnection();
            FindFoldersResults folders = getFolders(service);
            List<Item> mails = getLastMails(service, folders);
            doSend(service, mails);
            logger.info("End of get_new_messages():SUCCEED");
        } catch (Exception e) {
            logger.error("End of get_new_messages():FAILED");
            throw new JobExecutionException(e);
        }
    }

    private static ExchangeService createConnection() throws Exception {
        ExchangeService service = new ExchangeService();
        try {
            ExchangeCredentials credentials = new WebCredentials(WorkingEnvironment.getEmail(), WorkingEnvironment.getPassword());
            service.setUrl(new URI(WorkingEnvironment.getServer()));
            service.setCredentials(credentials);
            service.setTraceEnabled(true);
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
        return service;
    }

    private static FindFoldersResults getFolders(ExchangeService service) throws Exception {
        FindFoldersResults result = null;
        try {
            int pagedView = 1000;
            FolderView fv = new FolderView(pagedView);
            fv.setTraversal(FolderTraversal.Deep);
            fv.setPropertySet(new PropertySet(BasePropertySet.IdOnly, FolderSchema.UnreadCount, FolderSchema.DisplayName));
            result = service.findFolders(WellKnownFolderName.Inbox, fv);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return result;
    }

    private static List<Item> getLastMails(ExchangeService service, FindFoldersResults folders) throws Exception {
        List<Item> result = new ArrayList<>();
        for (Folder folder : folders.getFolders()) {
            try {
                PropertySet itempropertyset = new PropertySet(BasePropertySet.FirstClassProperties);
                itempropertyset.setRequestedBodyType(BodyType.HTML);
                ItemView itemview = new ItemView(100);
                itemview.setPropertySet(itempropertyset);
                SearchFilter srchFilter = new SearchFilter.IsGreaterThan(ItemSchema.DateTimeReceived, calculateStartTime());
                FindItemsResults<Item> results = service.findItems(folder.getId(),srchFilter,itemview);
                for (Item item : results) {
                    ItemId itemId = item.getId();
                    Item itm = Item.bind(service, itemId, PropertySet.FirstClassProperties);
                    item.load(itempropertyset);
                    result.add(item);
                }
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
        return result;
    }

    private static void doSend(ExchangeService service, List<Item> mails) throws Exception {
        try {
            for (Item item : mails) {
                sendEmail(service, item.getSubject(), WorkingEnvironment.getTargetEmail(), null, item.getBody().toString(), null);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public static void sendEmail(ExchangeService service, String subject, String to, String[]cc, String bodyText,
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
        /*if (attachmentPath != null && attachmentPath.length > 0) {
            for (int a = 0; a < attachmentPath.length; a++) {
                msg.getAttachments().addFileAttachment(attachmentPath[a]);
            }
        }*/
        msg.send();
    }

    private static Date calculateStartTime() throws Exception {
        Date result = null;
        try {
            LocalDateTime unformatedStartTime = LocalDateTime.now().minusMinutes(Integer.valueOf(WorkingEnvironment.getCheckInterval()));
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String incompatibleStartTime = unformatedStartTime.format(dtf);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            result = sdf.parse(incompatibleStartTime);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return result;
    }
}
