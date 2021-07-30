package okon.ns.config;

import okon.ns.exception.ConfigurationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfigReader {
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
        validateLogFile(properties);
        validateLogFileSize(properties);
    }

    public static void validateLogFile(Properties properties) {
        if (properties.containsKey("LogFile") && isWrongFormat(properties, "LogFile")) {
            System.exit(101);
        }
    }

    public static void validateLogFileSize(Properties properties) {
        if (properties.containsKey("LogFileSize") && (isWrongFormat(properties, "LogFileSize")
                || isOutOfRange(properties, "LogFileSize"))) {
            System.exit(102);
        }
    }

    public static boolean isWrongFormat(Properties properties, String key) {
        if (key.equals("LogFile")) {
            try {
                new File(properties.getProperty(key)).getCanonicalPath();
            } catch (IOException e) {
                return true;
            }
        } else if (key.equals("LogFileSize")) {
            try {
                Integer.parseInt(properties.getProperty(key));
            } catch (NumberFormatException e) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOutOfRange(Properties properties, String key) {
        if (key.equals("LogFileSize")) {
            if (Integer.valueOf(properties.getProperty("LogFileSize")).intValue() < 0
                    || Integer.valueOf(properties.getProperty("LogFileSize")).intValue() > 128) {
                return true;
            }
        }
        return false;
    }
}
