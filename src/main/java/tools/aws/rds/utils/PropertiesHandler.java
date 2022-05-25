package tools.aws.rds.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class PropertiesHandler {
    private static InputStream stream;
    static {
        try {
            stream = new FileInputStream("rds-config.yml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final Map<String, Object> data = new Yaml().load(stream);

    // retrieve either a configured yaml key or a system property
    public static String getKey(String arg) throws IllegalArgumentException {
        for (Map.Entry<String, Object> entry: data.entrySet()) {
            if (entry.getKey().equals(arg)) {
                String configVal = entry.getValue().toString();
                if (!configVal.equals("UNSET")) {
                    return configVal;
                }
                String propVal = System.getProperty(arg);
                if (propVal != null) {
                    return propVal;
                }
            }
        }
        throw new IllegalArgumentException("you have neither a system property or an rds-config value for: " + arg);
    }
}