package edu.brown.cs.termproject.httpcore.net;

import java.io.IOException;
import java.io.InputStream;

public class HTTPBodyParser {

  public static String getBodyText(InputStream is, int length) {
    final byte[] bodyBytes = new byte[length];
    // automatically reads length bytes
    try {
      System.out.format("reading %d bytes\n", length);
      System.out.println(is.available());
      final int bytesRead = is.read(bodyBytes);
      System.out.format("read %d bytes\n", bytesRead);
      return new String(bodyBytes);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
