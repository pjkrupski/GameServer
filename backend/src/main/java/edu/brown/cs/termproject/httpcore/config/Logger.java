package edu.brown.cs.termproject.httpcore.config;

public final class Logger {

  public static void Log(String fmt, Object ... args) {
    if (HTTPCoreConfig.DEBUG) {
      System.out.format(fmt, args);
    }
  }
}
