package okon.ns;

import java.util.Properties;

public class PerformanceSettings {
    private static Properties settings = new Properties();

    public static void setParams(Properties parameters) {
        if (parameters.containsKey("amount_of.starts")) {
            settings.setProperty("amount_of.starts", parameters.getProperty("amount_of.starts"));
        }
    }

    public static String getStartCounter() {
        return settings.getProperty("amount_of.starts");
    }

    public static void incrementStartCounter() {
        settings.setProperty("amount_of.starts", String.valueOf(Integer.valueOf(settings.getProperty("amount_of.starts")).intValue() + 1));
    }
}