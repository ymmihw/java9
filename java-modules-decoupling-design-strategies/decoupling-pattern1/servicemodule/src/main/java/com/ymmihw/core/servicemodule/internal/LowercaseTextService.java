package com.ymmihw.core.servicemodule.internal;

import com.ymmihw.core.servicemodule.external.TextService;

public class LowercaseTextService implements TextService {

    @Override
    public String processText(String text) {
        return text.toLowerCase();
    }
    
}
