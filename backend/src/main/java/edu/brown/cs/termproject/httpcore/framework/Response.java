package edu.brown.cs.termproject.httpcore.framework;

import edu.brown.cs.termproject.httpcore.config.HTTPCoreConfig;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class Response {

  public HTTPResponseStatus status = HTTPResponseStatus.OK;
  public final Headers headers = new Headers();
  private byte[] body = new byte[0];


  public Response() {}

  public Response(String body) {
    setBodyString(body);
  }

  public void setBodyString(String s) {
    body = s.getBytes(StandardCharsets.UTF_8);
    headers.set(Headers.CommonHeaderNames.ContentLength, Integer.toString(body.length));
  }

  public void setBody(byte[] body) {
    this.body = body;
    headers.set(Headers.CommonHeaderNames.ContentLength, Integer.toString(body.length));
  }

  public byte[] serialize() {
    final StringBuilder head = new StringBuilder();
    head.append(String.format("%s %s\r\n", HTTPCoreConfig.HTTP_VERSION_STRING, status.toString()));
    head.append(headers.serialize());
    head.append("\r\n");
    final byte[] headBytes = head.toString().getBytes(StandardCharsets.UTF_8);
    final byte[] totalBytes = new byte[headBytes.length + body.length];
    System.arraycopy(headBytes, 0, totalBytes, 0, headBytes.length);
    System.arraycopy(body, 0, totalBytes, headBytes.length, body.length);
    return totalBytes;
  }

  @Override
  public String toString() {
    return new String(serialize());
  }


  public static Response fiveHundred(String error) {
    final String body = "Internal Server Error:\n" + error;
    final Response r = new Response(body);
    r.status = HTTPResponseStatus.INTERNAL_SERVER_ERROR;
    return r;
  }

  public static Response notFound(String resource) {
    final String body = "File Not Found:\n" + resource;
    final Response r = new Response(body);
    r.status = HTTPResponseStatus.NOTFOUND;
    return r;
  }
}
