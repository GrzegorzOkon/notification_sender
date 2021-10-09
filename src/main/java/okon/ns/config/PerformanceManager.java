package okon.ns.config;

import okon.ns.PerformanceSettings;
import okon.ns.exception.ConfigurationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class PerformanceManager {
    public static Properties loadProperties(File file) {
        Properties result = new Properties();
        try (FileInputStream input = new FileInputStream(file)) {
            result.load(input);
            validate(result);
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage());
        }
        return result;
    }

    public static void saveProperties(File file) {
        try (FileOutputStream output = new FileOutputStream(file)) {
            PerformanceSettings.getSettings().store(output, null);
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage());
        }
    }

    public static void validate(Properties properties) {
        validateNumberOfStarts(properties);
    }

    public static void validateNumberOfStarts(Properties properties) {
        if (properties.containsKey("number_of.starts") && (isWrongFormat(properties, "number_of.starts")
                || isOutOfRange(properties, "number_of.starts"))) {
            System.exit(201);
        }
    }

    public static boolean isWrongFormat(Properties properties, String key) {
        if (key.equals("number_of.starts")) {
            try {
                Integer.parseInt(properties.getProperty(key));
            } catch (NumberFormatException e) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOutOfRange(Properties properties, String key) {
        if (key.equals("number_of.starts") && Integer.valueOf(properties.getProperty("number_of.starts")).intValue() < 0) {
            return true;
        }
        return false;
    }
}
