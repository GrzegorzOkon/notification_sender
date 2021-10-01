package okon.ns;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class WorkingSettings {
    private static Properties settings = new Properties();

    public static void setParams(Properties parameters) {
        if (parameters.containsKey("LogFile")) {
            settings.setProperty("LogFile", parameters.getProperty("LogFile"));
        }
        if (parameters.containsKey("LogFileSize")) {
            settings.setProperty("LogFileSize", parameters.getProperty("LogFileSize"));
        }
        if (parameters.containsKey("DebugLevel")) {
            settings.setProperty("DebugLevel", parameters.getProperty("DebugLevel"));
        }
        if (parameters.containsKey("Server")) {
            settings.setProperty("Server", parameters.getProperty("Server"));
        }
        if (parameters.containsKey("Email")) {
            settings.setProperty("Email", parameters.getProperty("Email"));
        }
        if (parameters.containsKey("Password")) {
            settings.setProperty("Password", parameters.getProperty("Password"));
        }
        if (parameters.containsKey("CheckInterval")) {
            settings.setProperty("CheckInterval", parameters.getProperty("CheckInterval"));
        }
        if (parameters.containsKey("TargetEmail")) {
            settings.setProperty("TargetEmail", parameters.getProperty("TargetEmail"));
        }
        settings.setProperty("AppName", checkJarFileName());
        settings.setProperty("HostName", checkHostName());
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
        return settings.getProperty("LogFile", "./" + getAppName() + ".log");
    }

    public static String getLogFileSize() { return settings.getProperty("LogFileSize", "1"); }

    public static Integer getDebugLevel() { return Integer.valueOf(settings.getProperty("DebugLevel", "3")); }

    public static String getServer() { return settings.getProperty("Server"); }

    public static String getEmail() { return settings.getProperty("Email"); }

    public static String getPassword() { return settings.getProperty("Password"); }

    public static String getCheckInterval() { return settings.getProperty("CheckInterval", "10"); }

    public static String getTargetEmail() { return settings.getProperty("TargetEmail"); }

    public static String getAppName() {
        return settings.getProperty("AppName");
    }

    public static String getHostName() { return settings.getProperty("HostName"); }
}
