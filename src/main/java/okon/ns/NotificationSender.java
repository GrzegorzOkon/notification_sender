package okon.ns;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import okon.ns.config.AppConfigReader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.io.File;
import java.net.URI;
import java.util.Properties;

public class NotificationSender {
    private static final Logger logger = LogManager.getLogger(NotificationSender.class);
    private static NotificationSender notificationSenderInstance = new NotificationSender();

    public static void main(String[] args) {
        String cmd = "start";
        if (args.length > 0) {
            cmd = args[0];

            System.out.println("Service called with param: " + cmd);
        }

        if ("start".equals(cmd)) {
            notificationSenderInstance.windowsStart();
        } else {
            notificationSenderInstance.windowsStop();
        }
    }

    public static void windowsService(String args[]) {
        String cmd = "start";
        if (args.length > 0) {
            cmd = args[0];
        }

        if ("start".equals(cmd)) {
            notificationSenderInstance.windowsStart();
        }
        else {
            notificationSenderInstance.windowsStop();
        }
    }

    public void windowsStart() {
        try {
            init();
            while (true) {
                logger.debug("Checking for new email");
                connectViaExchangeManually(WorkingEnvironment.getEmail(), WorkingEnvironment.getPassword());
                Thread.sleep(600000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void windowsStop() {}

    public void init() throws Exception {
        Properties properties = AppConfigReader.loadProperties((new File("./settings/program.properties")));
        WorkingEnvironment.setEnvironment(properties);
        NotificationSender.initLogger();
        logger.info("Starting " + Version.getVersionInfo() + " [" + WorkingEnvironment.getHostName() + "]");
        logger.info("using configuration file: './settings/program.properties'");
    }

    public static void connectViaExchangeManually(String email, String password) throws Exception {
        ExchangeService service = new ExchangeService();
        ExchangeCredentials credentials = new WebCredentials(email, password);
        service.setUrl(new URI(WorkingEnvironment.getServer()));
        service.setCredentials(credentials);
        service.setTraceEnabled(true);
        Folder inbox = Folder.bind(service, WellKnownFolderName.Inbox);

        int unreaded = inbox.getUnreadCount();
        if (unreaded > 0) {
            doSend(service, "nowe wiadomości e-mail", WorkingEnvironment.getTargetEmail(), null, "Nowe wiadomości: " + unreaded, null);
        }
    }

    public static void doSend (ExchangeService service, String subject, String to, String[]cc, String bodyText,
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

    private static void initLogger() {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
                .addAttribute("pattern", "%6.6pid:%d{yyyyMMdd':'HHmmss.SSS} %m%n");
        if (isLogRotationEnabled()) {
            AppenderComponentBuilder appenderBuilder = builder.newAppender("LogToRollingFile", "RollingFile")
                    .addAttribute("fileName", WorkingEnvironment.getLogFile())
                    .addAttribute("filePattern", WorkingEnvironment.getLogFile() + ".old")
                    .add(layoutBuilder);
            ComponentBuilder triggeringPolicy = builder.newComponent("Policies")
                    .addComponent(builder.newComponent("SizeBasedTriggeringPolicy")
                            .addAttribute("size", WorkingEnvironment.getLogFileSize() + "MB"));
            appenderBuilder.addComponent(triggeringPolicy);
            builder.add(appenderBuilder);
            RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.values()[WorkingEnvironment.getDebugLevel()]);
            rootLogger.add(builder.newAppenderRef("LogToRollingFile"));
            builder.add(rootLogger);
        } else {
            AppenderComponentBuilder appenderBuilder = builder.newAppender("LogToFile", "File")
                    .addAttribute("fileName", WorkingEnvironment.getLogFile())
                    .add(layoutBuilder);
            builder.add(appenderBuilder);
            RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.values()[WorkingEnvironment.getDebugLevel()]);
            rootLogger.add(builder.newAppenderRef("LogToFile"));
            builder.add(rootLogger);
        }
        Configurator.reconfigure(builder.build());
    }

    private static boolean isLogRotationEnabled() {
        if (Integer.valueOf(WorkingEnvironment.getLogFileSize()) != 0)
            return true;
        return false;
    }
}
