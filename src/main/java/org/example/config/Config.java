package org.example.config;

import java.io.InputStream;
import java.util.Properties;

public class Config
{
    private static final Properties properties = new Properties();

    static
    {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties"))
        {
            if (input == null)
            {
                throw new RuntimeException("File config.properties not found");
            }

            properties.load(input);
        }
        catch (Exception exception)
        {
            throw new RuntimeException("Cannot load file config.properties");
        }
    }

    public static String get(String key)
    {
        return properties.getProperty(key);
    }
}
