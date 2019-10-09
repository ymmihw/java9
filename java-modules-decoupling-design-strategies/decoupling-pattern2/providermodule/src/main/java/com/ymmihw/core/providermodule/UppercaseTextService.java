package com.ymmihw.core.providermodule;

import com.ymmihw.core.servicemodule.TextService;

public class UppercaseTextService implements TextService {
  @Override
  public String parseText(String text) {
    return text.toUpperCase();
  }
}
