package com.zextras.carbonio.catalog;

import java.util.Set;

import static com.zextras.carbonio.catalog.ServicePredicates.excludeUninteresting;

public class ServiceName {

  public static String[] fromAll(Set<String> rawNames) {
    return rawNames
        .stream()
        .filter(excludeUninteresting())
        .sorted()
        .toArray(String[]::new);
  }
}
