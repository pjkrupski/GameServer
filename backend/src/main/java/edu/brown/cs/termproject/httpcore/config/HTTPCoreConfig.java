package edu.brown.cs.termproject.httpcore.config;

import java.util.HashSet;
import java.util.Set;

public final class HTTPCoreConfig {

  public static final String HTTP_VERSION_STRING = "HTTP/1.1";
  public static final String HTTP_METHOD_GET  = "GET";
  public static final String HTTP_METHOD_POST =  "POST";
  public static final Set<String> HTTP_ALLOWED_METHODS =
      Set.of(HTTP_METHOD_GET, HTTP_METHOD_POST);

  public static final int THREADPOOL_SIZE = 8;

  static final boolean DEBUG = true;
}
