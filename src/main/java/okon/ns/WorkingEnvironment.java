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
        if (parameters.containsKey("DebugLevel")) {
            environment.setProperty("DebugLevel", parameters.getProperty("DebugLevel"));
        }
        if (parameters.containsKey("Server")) {
            environment.setProperty("Server", parameters.getProperty("Server"));
        }
        if (parameters.containsKey("Email")) {
            environment.setProperty("Email", parameters.getProperty("Email"));
        }
        if (parameters.containsKey("Password")) {
            environment.setProperty("Password", parameters.getProperty("Password"));
        }
        if (parameters.containsKey("CheckInterval")) {
            environment.setProperty("CheckInterval", parameters.getProperty("CheckInterval"));
        }
        if (parameters.containsKey("TargetEmail")) {
            environment.setProperty("TargetEmail", parameters.getProperty("TargetEmail"));
        }
        environment.setProperty("AppName", checkJarFileName());
        environment.setProperty("HostName", checkHostName());
    }

    private static String checkJarFileName() {
        String path = NotificationSender.class.getResource(NotificationSender.class.getSimpleName() + ".class").getFile();
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

    public static Integer getDebugLevel() { return Integer.valueOf(environment.getProperty("DebugLevel", "3")); }

    public static String getServer() { return environment.getProperty("Server"); }

    public static String getEmail() { return environment.getProperty("Email"); }

    public static String getPassword() { return environment.getProperty("Password"); }

    public static String getCheckInterval() { return environment.getProperty("CheckInterval", "10"); }

    public static String getTargetEmail() { return environment.getProperty("TargetEmail"); }

    public static String getAppName() {
        return environment.getProperty("AppName");
    }

    public static String getHostName() { return environment.getProperty("HostName"); }
}
