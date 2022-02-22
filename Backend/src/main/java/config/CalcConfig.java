package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

public class CalcConfig {

    private static final Logger logger = Logger.getLogger(CalcConfig.class.getName());

    Map<String, String> properties;

    public CalcConfig() {
        this.properties = new HashMap<>();
        Properties servProps = new Properties();
        try {
            String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
            servProps.load(new FileInputStream(rootPath + "servlet.properties"));
            for (String property : servProps.stringPropertyNames()) {
                properties.put(property, servProps.getProperty(property));
            }
        } catch (IOException e) {
            logger.severe("Failed to load properties from resources");
        }
    }

    public String getPropertyValue(String property) {
        return properties.getOrDefault(property, null);
    }
}
