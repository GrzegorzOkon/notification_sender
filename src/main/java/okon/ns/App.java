package okon.ns;

import okon.ns.config.AppConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.io.File;
import java.util.Properties;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    static {
        Properties properties = AppConfigReader.loadProperties((new File("./settings/program.properties")));
        WorkingEnvironment.setEnvironment(properties);
        App.initLogger("%6.6pid:%d{yyyyMMdd':'HHmmss.SSS} %m%n");
        logger.error("Starting " + Version.getVersionInfo() + " [" + WorkingEnvironment.getHostName() + "]");
        logger.error("using configuration file: './settings/program.properties'");
    }

    public static void main(String[] args) {

    }

    private static void initLogger(String pattern) {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
                .addAttribute("pattern", pattern);
        AppenderComponentBuilder appenderBuilder = builder.newAppender("LogToRollingFile", "RollingFile")
                .addAttribute("fileName", WorkingEnvironment.getLogFile())
                .addAttribute("filePattern", WorkingEnvironment.getLogFile() + "-%d{MM-dd-yy-HH-mm-ss}.log.")
                .add(layoutBuilder);
        ComponentBuilder triggeringPolicy = builder.newComponent("Policies")
                .addComponent(builder.newComponent("SizeBasedTriggeringPolicy")
                        .addAttribute("size", 4 + "MB"));
            appenderBuilder.addComponent(triggeringPolicy);
        builder.add(appenderBuilder);
        RootLoggerComponentBuilder rootLogger = builder.newRootLogger();
        rootLogger.add(builder.newAppenderRef("LogToRollingFile"));
        builder.add(rootLogger);
        Configurator.reconfigure(builder.build());
    }
}
