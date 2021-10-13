package okon.ns;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;

public class PerformanceSettings {
    private static Properties settings = new Properties();

    public static void setParams(Properties parameters) {
        if (parameters.containsKey("number_of.starts")) {
            settings.setProperty("number_of.starts", parameters.getProperty("number_of.starts"));
        }
        settings.setProperty("last.check.time", "");
    }

    public static String getStartCounter() {
        return settings.getProperty("number_of.starts");
    }

    public static void incrementStartCounter() {
        settings.setProperty("number_of.starts", String.valueOf(Integer.valueOf(settings.getProperty("number_of.starts")).intValue() + 1));
    }

    public static String getLastCheckTime() {
        return settings.getProperty("last.check.time");
    }

    public static void actualizeCheckTime() {
        LocalDateTime checkTime = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        settings.setProperty("last.check.time", checkTime.format(dtf));
    }

    public static Date calculateStartTime() throws Exception {
        Date result = null;
        try {
            LocalDateTime unformatedStartTime = LocalDateTime.now().plusMinutes(Integer.valueOf(WorkingSettings.getCheckInterval()));
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
            String incompatibleStartTime = unformatedStartTime.format(dtf);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            result = sdf.parse(incompatibleStartTime);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return result;
    }

    public static Date calculateCheckTime() throws Exception {
        Date result = null;
        try {
            if (getLastCheckTime().equals("")) {
                LocalDateTime unformatedStartTime = LocalDateTime.now().minusMinutes(Integer.valueOf(WorkingSettings.getCheckInterval()));
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
                String incompatibleStartTime = unformatedStartTime.format(dtf);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                result = sdf.parse(incompatibleStartTime);
            } else {
                String incompatibleStartTime = getLastCheckTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                result = sdf.parse(incompatibleStartTime);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return result;
    }

    public static Properties getSettings() {
        return settings;
    }
}