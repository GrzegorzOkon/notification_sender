package okon.ns;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class WorkingEnvironment {
    private static Properties environment = new Properties();

    public static void setEnvironment(Properties parameters) {
        if (parameters.containsKey("LogFile")) {
            environment.setProperty("LogFile", parameters.getProperty("LogFile"));
        }
        if (parameters.containsKey("LogFileSize")) {
            environment.setProperty("LogFileSize", parameters.getProperty("LogFileSize"));
        }
        if (parameters.containsKey("EmailAddress")) {
            environment.setProperty("EmailAddress", parameters.getProperty("EmailAddress"));
        }
        if (parameters.containsKey("Password")) {
            environment.setProperty("Password", parameters.getProperty("Password"));
        }
        environment.setProperty("AppName", checkJarFileName());
        environment.setProperty("HostName", checkHostName());
    }

    private static String checkJarFileName() {
        String path = App.class.getResource(App.class.getSimpleName() + ".class").getFile();
        path = path.substring(0, path.lastIndexOf('!'));
        path = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf('.'));
        return path;
    }

    private static String checkHostName() {
        String result = "Unknown";
        try {
            result = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
        }
        return result;
    }

    public static String getLogFile() {
        return environment.getProperty("LogFile", "./" + getAppName() + ".log");
    }

    public static String getLogFileSize() { return environment.getProperty("LogFileSize", "1"); }

    public static String getEmailAddress() { return environment.getProperty("EmailAddress"); }

    public static String getPassword() { return environment.getProperty("Password"); }

    public static String getAppName() {
        return environment.getProperty("AppName");
    }

    public static String getHostName() { return environment.getProperty("HostName"); }
}
