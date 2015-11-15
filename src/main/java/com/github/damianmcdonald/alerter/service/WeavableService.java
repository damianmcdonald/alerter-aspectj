package com.github.damianmcdonald.alerter.service;

import java.io.IOException;

import org.apache.log4j.Logger;

public class WeavableService {
  
  private final static Logger logger = Logger.getLogger(WeavableService.class.getName());
  
  public void weavableMethod(final String message) throws IOException {
    if (message.equals(WeavableMessageType.TERMINATE.name())) {
      logger.error("Received message: " + WeavableMessageType.TERMINATE.name() + ". Terminating with error!");
      final String errMsg = WeavableMessageType.TERMINATE.name() + " message. Terminating with error!";
      throw new IOException(errMsg);
    }
    logger.info("Received message: " + message);
  }

}
