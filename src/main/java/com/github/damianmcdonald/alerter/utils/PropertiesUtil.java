package com.github.damianmcdonald.alerter.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
  
  private final Properties properties = new Properties();
  private final String propertyFile = System.getProperty("alerter.properties");
  
  public PropertiesUtil() throws IOException {
    if (propertyFile != null && new File(propertyFile).exists()) {
      try (final InputStream input = new FileInputStream(propertyFile)) {
        properties.load(input);
      }
    } else {
      properties.load(PropertiesUtil.class.getResourceAsStream("/alerter.properties"));
    }
  }
  
  public Properties getProperties() {
    return properties;
  }
 
}
