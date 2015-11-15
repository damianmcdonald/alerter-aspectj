package com.github.damianmcdonald.alerter.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PointCutInfo {
  
  private final Date timeStamp = new Date();
  private final Map<String, Object> args = new HashMap<String, Object>();
  private String alertType;
  private String alertMethod;
  private String alertSignature;
  private String[] parameterNames;
  private Object[] parameterValues;
  
  public PointCutInfo(final String alertType, final String alertMethod, final String alertSignature, final String[] parameterNames, final Object[] parameterValues) {
    this.alertType = alertType;
    this.alertMethod = alertMethod;
    this.alertSignature = alertSignature;
    this.parameterNames = parameterNames;
    this.parameterValues = parameterValues;
    // sanity checks
    if(parameterNames.length != parameterValues.length) {
      throw new IllegalArgumentException("The number of argument names must equal the number of argument values");
    }
    for(int i=0; i<parameterNames.length; i++) {
      args.put(parameterNames[i], parameterValues[i]);
    }
  }
  
  public Date getTimeStamp() {
    return timeStamp;
  }
  
  public String getAlertType() {
    return alertType;
  }

  public String getAlertMethod() {
    return alertMethod;
  }

  public String getAlertSignature() {
    return alertSignature;
  }
  
  public String[] getParameterNames() {
    return parameterNames;
  }

  public Object[] getParameterValues() {
    return parameterValues;
  }
  
  public Map<String, Object> getArgs() {
    return args;
  }
  
}
