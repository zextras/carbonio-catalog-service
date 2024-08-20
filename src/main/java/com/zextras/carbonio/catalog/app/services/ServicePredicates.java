package com.zextras.carbonio.catalog.app.services;

import java.util.function.Predicate;

public class ServicePredicates {

  public static Predicate<String> excludeUninteresting() {
    return excludeConsul().and(excludeSidecars());
  }

  private static Predicate<String> excludeSidecars() {
    return x -> !x.contains("sidecar-proxy");
  }

  private static Predicate<String> excludeConsul() {
    return x -> !x.equals("consul");
  }
}
