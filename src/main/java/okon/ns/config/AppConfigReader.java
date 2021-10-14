package okon.ns.config;

import okon.ns.exception.ConfigurationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfigReader {
    public static Properties loadProperties(File file) {
        Properties result = new Properties();
        try (FileInputStream input = new FileInputStream(file)){
            result.load(input);
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
        validateServer(properties);
        validateEmail(properties);
        validatePassword(properties);
        validatePasswordEncryption(properties);
        validateCheckInterval(properties);
        validateReadedFilter(properties);
        validateTargetEmail(properties);
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

    public static void validateServer(Properties properties) {
        if (!properties.containsKey("Server") || properties.getProperty("Server").equals("")) {
            System.exit(104);
        }
    }

    public static void validateEmail(Properties properties) {
        if (!properties.containsKey("Email") || isWrongFormat(properties, "Email")) {
            System.exit(105);
        }
    }

    public static void validatePassword(Properties properties) {
        if (!properties.containsKey("Password") || properties.getProperty("Password").equals("")) {
            System.exit(106);
        }
    }

    public static void validatePasswordEncryption(Properties properties) {
        if (properties.containsKey("PasswordEncryption") && (isWrongFormat(properties, "PasswordEncryption")
                || isOutOfRange(properties, "PasswordEncryption"))) {
            System.exit(107);
        }
    }

    public static void validateCheckInterval(Properties properties) {
        if (properties.containsKey("CheckInterval") && (isWrongFormat(properties, "CheckInterval")
                || isOutOfRange(properties, "CheckInterval"))) {
            System.exit(108);
        }
    }

    public static void validateReadedFilter(Properties properties) {
        if (properties.containsKey("ReadedFilter") && (isWrongFormat(properties, "ReadedFilter")
                || isOutOfRange(properties, "ReadedFilter"))) {
            System.exit(109);
        }
    }

    public static void validateTargetEmail(Properties properties) {
        if (!properties.containsKey("TargetEmail") || isWrongFormat(properties, "TargetEmail")) {
            System.exit(110);
        }
    }

    public static boolean isWrongFormat(Properties properties, String key) {
        if (key.equals("LogFile")) {
            try {
                new File(properties.getProperty(key)).getCanonicalPath();
            } catch (IOException e) {
                return true;
            }
        } else if (key.equals("LogFileSize") || key.equals("DebugLevel") || key.equals("CheckInterval") || key.equals("ReadedFilter")
                || key.equals("PasswordEncryption")) {
            try {
                Integer.parseInt(properties.getProperty(key));
            } catch (NumberFormatException e) {
                return true;
            }
        } else if (key.equals("Email") || key.equals("TargetEmail")) {
            if (!properties.getProperty(key).contains("@") || properties.getProperty(key).length() < 6)
                return true;
        }
        return false;
    }

    public static boolean isOutOfRange(Properties properties, String key) {
        if (key.equals("LogFileSize") && (Integer.valueOf(properties.getProperty("LogFileSize")).intValue() < 0
                    || Integer.valueOf(properties.getProperty("LogFileSize")).intValue() > 128)) {
                return true;
        } else if (key.equals("CheckInterval") && (Integer.valueOf(properties.getProperty("CheckInterval")).intValue() < 10
                || Integer.valueOf(properties.getProperty("CheckInterval")).intValue() > 120)) {
                return true;
        } else if (key.equals("UnreadedFilter") && (Integer.valueOf(properties.getProperty("ReadedFilter")).intValue() < 0
                || Integer.valueOf(properties.getProperty("ReadedFilter")).intValue() > 1)){
            return true;
        } else if (key.equals("PasswordEncryption") && (Integer.valueOf(properties.getProperty("PasswordEncryption")).intValue() < 0
                || Integer.valueOf(properties.getProperty("PasswordEncryption")).intValue() > 1)) {
            return true;
        }
        return false;
    }
}
