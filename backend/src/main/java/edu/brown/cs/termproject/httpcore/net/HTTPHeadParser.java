package edu.brown.cs.termproject.httpcore.net;

import edu.brown.cs.termproject.httpcore.config.HTTPCoreConfig;
import edu.brown.cs.termproject.httpcore.datastructures.Either;
import edu.brown.cs.termproject.httpcore.framework.FailureReason;
import edu.brown.cs.termproject.httpcore.framework.Headers;
import edu.brown.cs.termproject.httpcore.framework.Request;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.cert.CRL;
import java.util.Scanner;

/**
 * Parses everything up until the body of the HTTP request.
 * This can be done deterministically because until the body
 * is reached, every line is terminated with CRLF
 */
public class HTTPHeadParser {

  /// Carriage Return Line Feed
  private static final String CRLF = "\015\012";

  /**
   * This is kind of a monster because of the error checking...
   */
  static Either<Request, FailureReason> parseHead(InputStream is) {
    try {
      StringBuilder sb = new StringBuilder();
      final int bufferSize = 1024;
      byte[] buffer = new byte[bufferSize];
      System.out.println("starting read");
      int read = 0;
      while ((read = is.read(buffer, 0, bufferSize)) > 0) {
        final String thisBuff = new String(buffer, 0, read, Charset.defaultCharset());
        if (!thisBuff.contains(CRLF)) {
          sb.append(thisBuff);
        } else {

        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("end read");
    InputStreamReader isr = new InputStreamReader(is);
    Scanner s = new Scanner(isr).useDelimiter(CRLF);
    if (!s.hasNext()) {
      // Every valid request has a head line
      return Either.right(
          new FailureReason(
              "unknown",
              "invalid request",
              "All HTTP requests must contain a head line"));
    }
    final String headLine = s.next();
    if (headLine == null) {
      return Either.right(
          new FailureReason(
              "unknown",
              "invalid request",
              "All HTTP requests must contain a head line"));
    }
    final String[] headTokens = headLine.split(" ");
    if (headTokens.length != 3) {
      return Either.right(
          new FailureReason(
              "unknown",
              "invalid head line",
              "The head line of an HTTP request must " +
                  "contain a method, a URI, and a version number. " +
                  "You provided: " + headLine));
    }
    final String method = headTokens[0];
    final String uri = headTokens[1];
    final String version = headTokens[2];

    if (!HTTPCoreConfig.HTTP_ALLOWED_METHODS.contains(method)) {
      return Either.right(
          new FailureReason(
              uri,
              "invalid HTTP method",
              "Valid HTTP methods this server supports are: " +
                  HTTPCoreConfig.HTTP_ALLOWED_METHODS.toString()));
    }

    if (!version.equals(HTTPCoreConfig.HTTP_VERSION_STRING)) {
      return Either.right(
          new FailureReason(
              uri,
              "invalid HTTP version",
              "The HTTP version this server supports is: " +
                  HTTPCoreConfig.HTTP_VERSION_STRING));
    }

    // The head line is valid, now let's parse the headers.
    Headers headers = new Headers();
    while (s.hasNext()) {
      final String headerLine = s.next();
      System.out.println("got line from scanner = [" + headerLine + "]");
      if (headerLine == null) {
        return Either.right(
            new FailureReason(
                uri,
                "invalid HTTP request",
                "HTTP1.1 requests must be terminated by a CRLF only line"));

      }
      if (headerLine.equals("")) {
        // We have reached the end of the headers
        System.out.println("last line here + ");
        break;
      }
      final String[] currentHeaderTokens = chompHeader(headerLine);
      if (currentHeaderTokens == null) {
        return Either.right(
            new FailureReason(
                uri,
                "invalid HTTP header",
                "HTTP1.1 headers must be of the form <name>:<value>." +
                    "You gave " + headerLine));
      }
      headers.set(currentHeaderTokens[0], currentHeaderTokens[1]);
    }
    System.out.println("out of loop");
    s.useDelimiter("");
    System.out.println(s.next());
    try {
      char[] buff = new char[100];
      System.out.println(isr.read(buff, 0, 4));
      System.out.println(new String(buff));
//      System.out.println("here is what is left + " + new String(is.readAllBytes()));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return Either.left(
        new Request(
            method,
            uri,
            headers
        )
    );
  }

  private static String[] chompHeader(final String line) {
    System.out.println("chomping: " + line);
    int idx = line.indexOf(':');
    if (idx == -1 || idx == 0) {
      return null;
    }
    return new String[] {
      line.substring(0, idx).trim().toLowerCase(),
      line.substring(idx + 1).trim().toLowerCase()
    };
  }
}
