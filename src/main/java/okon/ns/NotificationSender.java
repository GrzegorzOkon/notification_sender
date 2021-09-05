package okon.ns;

import okon.ns.config.AppConfigReader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.File;
import java.util.Properties;

public class NotificationSender {
    private static final Logger logger = LogManager.getLogger(NotificationSender.class);
    private static NotificationSender notificationSenderInstance = new NotificationSender();
    private static Scheduler NewPostChecker;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void windowsStop() {}

    public void init() throws Exception {
        Properties properties = AppConfigReader.loadProperties((new File("./settings/program.properties")));
        WorkingEnvironment.setEnvironment(properties);
        NotificationSender.initLogger();
        NotificationSender.initScheduler();
        logger.info("Starting " + Version.getVersionInfo() + " [" + WorkingEnvironment.getHostName() + "]");
        logger.info("using configuration file: './settings/program.properties'");
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

    private static void initScheduler() throws Exception {
        NewPostChecker = new StdSchedulerFactory().getScheduler();
        NewPostChecker.start();
        Trigger trigger =  createTrigger();
        scheduleJob(trigger);
    }

    private static Trigger createTrigger() {
        Trigger result = TriggerBuilder.newTrigger().withIdentity("CHECKING POST TRIGGER", "NEW POST CHECK")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(600).repeatForever())
                .build();
        return result;
    }

    private static void scheduleJob(Trigger trigger) throws Exception {
        JobDetail job = JobBuilder.newJob(NewPostCheckJob.class).withIdentity("EXCHANGE SERVER POST CHECKING", "EXCHANGE SERVER").build();
        NewPostChecker.scheduleJob(job, trigger);
    }

    private static boolean isLogRotationEnabled() {
        if (Integer.valueOf(WorkingEnvironment.getLogFileSize()) != 0)
            return true;
        return false;
    }
}
