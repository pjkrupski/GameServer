package edu.brown.cs.termproject.httpcore.framework;

public final class Request {

  public final String method;
  public final String URL;
  public final Headers headers;
  public String body;

  public Request(String method, String URL,
                 Headers headers) {
    this.method = method;
    this.URL = URL;
    this.headers = headers != null ? headers : new Headers();
  }

  @Override
  public String toString() {
    return "Request{" +
        "method='" + method + '\'' +
        ", URL='" + URL + '\'' +
        ", headers=" + headers +
        ", body='" + body + '\'' +
        '}';
  }
}
