package com.github.damianmcdonald.alerter.templating;

import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.github.damianmcdonald.alerter.domain.PointCutInfo;
import com.github.damianmcdonald.alerter.utils.BeanUtils;
import com.github.damianmcdonald.alerter.utils.PropertiesFactory;

public class VelocityTemplater {
  
  private final static String ALERT_TEMPLATE_HTML = "alert_html.vm";
  private final static String ALERT_TEMPLATE_PLAINTEXT = "alert_txt.vm";
  private final Properties props = PropertiesFactory.getInstance().getProperties();
  private VelocityEngine velocityEngine;
  
  public VelocityTemplater() {
    velocityEngine = new VelocityEngine(props);
  }
  
  public String generateTemplate(PointCutInfo info, TemplateType templateType) {
    final VelocityContext velocityContext = new VelocityContext();
    velocityContext.put( "info", info);
    velocityContext.put( "infoBeanMap", BeanUtils.recursiveDescribe(info));
    final StringWriter writer = new StringWriter();
    switch(templateType) {
      case HTML:    
        velocityEngine.mergeTemplate(ALERT_TEMPLATE_HTML, "utf-8", velocityContext, writer);
        break;
      case PLAINTEXT:
        velocityEngine.mergeTemplate(ALERT_TEMPLATE_PLAINTEXT, "utf-8", velocityContext, writer);
        break;
      default:
        velocityEngine.mergeTemplate(ALERT_TEMPLATE_PLAINTEXT, "utf-8", velocityContext, writer);
    }
    return writer.toString();
  }
  
}
