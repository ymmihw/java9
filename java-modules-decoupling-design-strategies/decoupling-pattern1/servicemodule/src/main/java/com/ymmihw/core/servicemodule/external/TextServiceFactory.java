package com.ymmihw.core.servicemodule.external;

import com.ymmihw.core.servicemodule.internal.LowercaseTextService;
import com.ymmihw.core.servicemodule.internal.UppercaseTextService;

public class TextServiceFactory {
    
    private TextServiceFactory() {}
    
    public static TextService getTextService(String name) {
        return name.equalsIgnoreCase("lowercase") ? new LowercaseTextService(): new UppercaseTextService();
    }
    
}
