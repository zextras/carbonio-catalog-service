/*
 * SPDX-FileCopyrightText: 2025 Zextras <https://www.zextras.com>
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */
package com.zextras.carbonio.catalog;

import java.util.function.Predicate;

public class ServicePredicates {

  public static Predicate<String> excludeUninteresting() {
    return excludeConsul()
        .and(excludeSidecars())
        .and(excludeDatabases())
        .and(excludePrometheusExporters())
        ;
  }

  private static Predicate<String> excludePrometheusExporters() {
    return x -> !(x.startsWith("carbonio-prometheus") && x.endsWith("exporter"));
  }

  private static Predicate<String> excludeDatabases() {
    return x -> !x.endsWith("-db");
  }

  private static Predicate<String> excludeSidecars() {
    return x -> !x.endsWith("-sidecar-proxy");
  }

  private static Predicate<String> excludeConsul() {
    return x -> !x.equals("consul");
  }
}
