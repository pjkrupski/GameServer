package edu.brown.cs.termproject.httpcore.framework;

import java.util.HashMap;
import java.util.Map;

public final class Headers {
  public static final class CommonHeaderNames {
    public static final String Host = "Host";
    public static final String Accept = "Accept";
    public static final String ContentLength = "Content-Length";
  }

  private final Map<String, String> headers = new HashMap<>();

  public void set(String headerName, String headerValue) {
    headers.put(headerName.toLowerCase(), headerValue);
  }
  public String get(String headerName) {
    return headers.get(headerName.toLowerCase());
  }
  public String getDefault(String headerName, String def) {
    final String value = get(headerName);
    return value != null ? value : def;
  }

  @Override
  public String toString() {
    return "Headers{" +
        "headers=" + headers +
        '}';
  }

  public String serialize() {
    final StringBuilder sb = new StringBuilder();
    headers.forEach((name, value) -> {
      sb.append(String.format("%s: %s\r\n", name, value));
    });
    return sb.toString();
  }
}
