module com.ymmihw.core.providermodule {
  requires com.ymmihw.core.servicemodule;

  provides com.ymmihw.core.servicemodule.TextService
      with com.ymmihw.core.providermodule.LowercaseTextService,
      com.ymmihw.core.providermodule.UppercaseTextService;
}
