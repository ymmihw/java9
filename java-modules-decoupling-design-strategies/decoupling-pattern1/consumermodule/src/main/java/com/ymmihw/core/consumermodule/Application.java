package com.ymmihw.core.consumermodule;

import com.ymmihw.core.servicemodule.external.TextService;
import com.ymmihw.core.servicemodule.external.TextServiceFactory;

public class Application {

  public static void main(String args[]) {
    TextService textService = TextServiceFactory.getTextService("lowercase");
    System.out.println(textService.processText("Hello from Baeldung!"));
  }

}
