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
        validateDebugLevel(properties);
        validateEmailAddress(properties);
        validatePassword(properties);
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

    public static void validateDebugLevel(Properties properties) {
        if (properties.containsKey("DebugLevel") && (isWrongFormat(properties, "DebugLevel")
                || isOutOfRange(properties, "DebugLevel"))) {
            System.exit(103);
        }
    }

    public static void validateEmailAddress(Properties properties) {
        if (!properties.containsKey("EmailAddress") || isWrongFormat(properties, "EmailAddress")) {
            System.exit(104);
        }
    }

    public static void validatePassword(Properties properties) {
        if (!properties.containsKey("Password") || properties.getProperty("Password").equals("")) {
            System.exit(105);
        }
    }

    public static boolean isWrongFormat(Properties properties, String key) {
        if (key.equals("LogFile")) {
            try {
                new File(properties.getProperty(key)).getCanonicalPath();
            } catch (IOException e) {
                return true;
            }
        } else if (key.equals("LogFileSize") || key.equals("DebugLevel")) {
            try {
                Integer.parseInt(properties.getProperty(key));
            } catch (NumberFormatException e) {
                return true;
            }
        } else if (key.equals("EmailAddress")) {
            if (!properties.getProperty(key).contains("@") || properties.getProperty(key).length() < 6)
                return true;
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
