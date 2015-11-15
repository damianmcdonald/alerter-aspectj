package com.github.damianmcdonald.alerter;

import java.io.IOException;

import com.github.damianmcdonald.alerter.service.WeavableMessageType;
import com.github.damianmcdonald.alerter.service.WeavableService;

public class Main {

  public static void main(final String[] args) throws IOException {
    final WeavableService service = new WeavableService();
    service.weavableMethod(WeavableMessageType.CONTINUE.name());
    service.weavableMethod(WeavableMessageType.TERMINATE.name());
    service.weavableMethod(WeavableMessageType.CONTINUE.name());
  }

}
