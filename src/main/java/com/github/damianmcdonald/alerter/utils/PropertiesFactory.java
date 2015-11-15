package com.github.damianmcdonald.alerter.utils;

import java.io.IOException;

public class PropertiesFactory {
  
  private static PropertiesUtil propertiesUtil = null;
  
  public static PropertiesUtil getInstance() {
    if(propertiesUtil != null) return propertiesUtil;
    try {
      propertiesUtil = new PropertiesUtil();
      return propertiesUtil;
    } catch (IOException e) {
      e.printStackTrace();
    } 
    return null;
  }

}
