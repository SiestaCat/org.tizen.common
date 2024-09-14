package org.tizen.common;

class AnalyticConfig {
  String id;
  
  Boolean logging;
  
  public AnalyticConfig(String uid, Boolean log) {
    this.id = uid;
    this.logging = log;
  }
}
