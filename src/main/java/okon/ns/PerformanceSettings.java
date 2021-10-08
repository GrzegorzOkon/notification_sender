package okon.ns;

import java.util.Properties;

public class PerformanceSettings {
    private static Properties settings = new Properties();

    public static void setParams(Properties parameters) {
        if (parameters.containsKey("number_of.starts")) {
            settings.setProperty("number_of.starts", parameters.getProperty("number_of.starts"));
        }
    }

    public static String getStartCounter() {
        return settings.getProperty("number_of.starts");
    }

    public static void incrementStartCounter() {
        settings.setProperty("number_of.starts", String.valueOf(Integer.valueOf(settings.getProperty("number_of.starts")).intValue() + 1));
    }

    public static Properties getSettings() {
        return settings;
    }
}