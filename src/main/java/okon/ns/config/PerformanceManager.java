package okon.ns.config;

import okon.ns.exception.ConfigurationException;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class PerformanceManager {
    public static Properties loadProperties(File file) {
        Properties result = new Properties();
        try {
            result.load(new FileInputStream(file));
            validate(result);
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage());
        }
        return result;
    }

    public static void validate(Properties properties) {
        validateAmountOfStarts(properties);
    }

    public static void validateAmountOfStarts(Properties properties) {
        if (properties.containsKey("amount_of.starts") && (isWrongFormat(properties, "amount_of.starts")
                || isOutOfRange(properties, "amount_of.starts"))) {
            System.exit(201);
        }
    }

    public static boolean isWrongFormat(Properties properties, String key) {
        if (key.equals("amount_of.starts")) {
            try {
                Integer.parseInt(properties.getProperty(key));
            } catch (NumberFormatException e) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOutOfRange(Properties properties, String key) {
        if (key.equals("amount_of.starts") && Integer.valueOf(properties.getProperty("amount_of.starts")).intValue() < 0) {
            return true;
        }
        return false;
    }
}
