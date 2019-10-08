package com.ymmihw.core.consumermodule;

import java.util.ServiceLoader;
import com.ymmihw.core.servicemodule.TextService;

public class Application {

  public static void main(String[] args) {
    ServiceLoader<TextService> services = ServiceLoader.load(TextService.class);
    for (final TextService service : services) {
      System.out.println("The service " + service.getClass().getSimpleName() + " says: "
          + service.parseText("Hello from Baeldung!"));
    }
  }
}
