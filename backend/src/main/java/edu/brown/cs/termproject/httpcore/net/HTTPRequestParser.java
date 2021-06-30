package edu.brown.cs.termproject.httpcore.net;

import edu.brown.cs.termproject.httpcore.config.HTTPCoreConfig;
import edu.brown.cs.termproject.httpcore.datastructures.Either;
import edu.brown.cs.termproject.httpcore.framework.FailureReason;
import edu.brown.cs.termproject.httpcore.framework.Headers;
import edu.brown.cs.termproject.httpcore.framework.Request;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class HTTPRequestParser {

  private static final String CRLF = "\r\n";


  public static Either<Request, FailureReason> parseRequest(InputStream is) {
    final int bufferSize = 1024;
    final byte[] buffer = new byte[bufferSize];

    int bytesRead = 0;
    String data = "";
    final List<String> lines = new ArrayList<>();
    boolean allHeadersParsed = false;
    try {
      while ((bytesRead = is.read(buffer, 0, bufferSize)) > 0) {
        data += new String(buffer, 0, bytesRead, Charset.defaultCharset());
        int idx = -1;
        while ((idx = data.indexOf(CRLF)) != -1) {
          if (data.startsWith(CRLF)) {
            // we found subsequent CRLF lines. headers are done
            data = data.substring(idx + 2);
            allHeadersParsed = true;
            break;
          }
          lines.add(data.substring(0, idx));
          data = data.substring(idx + 2);
        }
        if (allHeadersParsed) {
          break;
        }
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
      return null;
    }

    if (lines.isEmpty()) {
      return Either.right(
          new FailureReason(
              "unknown",
              "invalid request",
              "All HTTP requests must contain a head line"));
    }
    final String headLine = lines.remove(0);
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

    // Now we parse the lines containing headers.
    final Headers headers = new Headers();
    for (final String line : lines) {
      final String[] currentHeaderTokens = chompHeader(line);
      if (currentHeaderTokens == null) {
        return Either.right(
            new FailureReason(
                uri,
                "invalid HTTP header",
                "HTTP1.1 headers must be of the form <name>:<value>." +
                    "You gave " + line));
      }
      headers.set(currentHeaderTokens[0], currentHeaderTokens[1]);
    }

    // now we must read in the body if a content-length was given
    String bodyData = data;
    final String lenStr = headers.getDefault(Headers.CommonHeaderNames.ContentLength, "0");
    try {
      final int contentLength = Integer.parseInt(lenStr);
      int bytesLeftToRead = contentLength - data.length();
      bytesRead = 0;
      while (bytesLeftToRead > 0) {
        bytesRead = is.read(buffer, 0, bufferSize);
        if (bytesRead <= 0) {
          return Either.right(
              new FailureReason(
                  uri,
                  "invalid body length",
                  "You provided a content length of " + lenStr + "but the body did not" +
                      "contain this many bytes"));
        }
        bodyData += new String(buffer, 0, bytesRead, Charset.defaultCharset());
        bytesLeftToRead -= bytesRead;
      }

    } catch (NumberFormatException nfe) {
      // could not get content length
      bodyData = "";
    } catch (IOException e) {
      return Either.right(
          new FailureReason(
              uri,
              "unknown",
              "An error occurred while reading your request"));
    }

    final Request req = new Request(method, uri, headers);
    if (req.method.equals(HTTPCoreConfig.HTTP_METHOD_GET)) {
      req.body = "";
    } else {
      req.body = bodyData;
    }
    return Either.left(req);
  }


  private static String[] chompHeader(final String line) {
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
