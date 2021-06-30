package edu.brown.cs.termproject;

import edu.brown.cs.termproject.httpcore.datastructures.Either;
import edu.brown.cs.termproject.httpcore.framework.FailureReason;
import edu.brown.cs.termproject.httpcore.net.HTTPRequestParser;
import edu.brown.cs.termproject.httpcore.framework.Request;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class HTTPRequestParserTest {


  private Either<Request, FailureReason> call(String str) {
    return HTTPRequestParser.parseRequest(
        new ByteArrayInputStream(
          str.getBytes(StandardCharsets.UTF_8)));
  }

  @Test
  public void testEmptyGet() {
    final Request req = call("GET /hello HTTP/1.1\r\n\r\nbody").getLeft();
    assertEquals(req.method, "GET");
    assertEquals(req.URL, "/hello");
    assertEquals(req.body, "");
  }

  @Test
  public void testBadRequests() {
    assertTrue(call("GET /hello HTTP/1.1").isRight());
    assertTrue(call("GET /hello HTTP/1.1\r").isRight());
    assertTrue(call("GET /hello HTTP/1.1\n").isRight());
    assertTrue(call("GET /hello HTTP/1.1\r\n").isLeft());
    assertTrue(call("SPET /hello HTTP/1.1\r\n").isRight());
    assertTrue(call("GEF /hello HTTP/1.1\r\n").isRight());
    assertTrue(call("GET /hello HTTP/2.o\r\n").isRight());
    assertTrue(call("GETHTTP/1.1\r\n").isRight());
    assertTrue(call("GET HTTP/1.1\r\n").isRight());
    assertTrue(call("GET /hello HTTP/1.1\r\n").isLeft());
    assertTrue(call("GET-/hello-HTTP/1.1\r\n").isRight());
  }

  @Test
  public void testHeaders() {
    Request req = call("GET /hello HTTP/1.1\r\n" +
        "header1:           val1        \r\n" +
        "header2       :      val2    \r\n" +
        "\r\n").getLeft();
    assertEquals(req.URL, "/hello");
    assertEquals(req.method, "GET");
    assertEquals(req.headers.get("header1"), "val1");
    assertEquals(req.headers.get("header2"), "val2");
    assertEquals(req.headers.getDefault("header3", "def"), "def");

    StringBuilder sb = new StringBuilder("GET /manyheader HTTP/1.1\r\n");
    for (int i = 0; i < 5000; i++) {
      sb.append(String.format("header%d:value%d\r\n", i, i));
    }
    sb.append("\r\n");
    req = call(sb.toString()).getLeft();
    for (int i = 0; i < 5000; i++) {
      assertEquals(req.headers.get("header" + i), "value" + i);
    }
  }

  @Test
  public void testPOST() {
    Request req = call("POST /empty HTTP/1.1\r\n\r\n").getLeft();
    assertEquals(req.body, "");
    assertEquals(req.method, "POST");
    assertEquals(req.URL, "/empty");

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      sb.append("abcdefghijklmnopqrstuvwxyz0");
    }
    final String body = sb.toString();

    req = call("POST /bigbody HTTP/1.1\r\n" +
        "content-length: " + body.length() + "\r\n\r\n" +
        body).getLeft();
    assertEquals(req.body, body);

    assertTrue(
        call("POST /garbageConlen HTTP/1.1\r\n" +
            "content-length: 5\r\n\r\n12345").isLeft());
    assertTrue(
        call("POST /garbageConlen HTTP/1.1\r\n" +
            "content-length: 5f\r\n\r\n").isLeft());
    assertTrue(
        call("POST /tooLargeConLen HTTP/1.1\r\n" +
            "content-length: 20\r\n\r\nabc").isRight());
    assertTrue(
        call("POST /tooLargeConLen HTTP/1.1\r\n" +
            "content-length: 1\r\n\r\nabc").isLeft());

  }
}