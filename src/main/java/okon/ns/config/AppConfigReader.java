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
            validateLogFile(result);
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage());
        }
        return result;
    }

    public static void validateLogFile(Properties properties) {
        if (properties.containsKey("LogFile") && isWrongFormat(properties, "LogFile")) {
            System.exit(101);
        }
    }

    public static boolean isWrongFormat(Properties properties, String key) {
        if (key.equals("LogFile")) {
            try {
                new File(properties.getProperty(key)).getCanonicalPath();
            } catch (IOException e) {
                return true;
            }
        }
        return false;
    }
}
