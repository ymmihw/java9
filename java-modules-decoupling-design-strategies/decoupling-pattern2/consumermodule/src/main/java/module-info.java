module com.ymmihw.core.consumermodule {
  requires com.ymmihw.core.servicemodule;
  requires com.ymmihw.core.providermodule;

  uses com.ymmihw.core.servicemodule.TextService;
}
