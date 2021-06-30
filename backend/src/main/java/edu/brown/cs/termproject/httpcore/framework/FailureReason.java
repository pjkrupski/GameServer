package edu.brown.cs.termproject.httpcore.framework;

/**
 * An HTML encoding of a failure.
 * We always include what URI they wanted, a short summary, and then any details.
 */
public class FailureReason {

  final String uri, reason, detail;
  public FailureReason(String uri, String reason, String detail) {
    this.uri = uri;
    this.reason = reason;
    this.detail = detail;
  }

  @Override
  public String toString() {
    return String.format("<html><body>Error :%s. " +
        "Request to URI: %s failed. " +
        "Details: %s</body></html>",
        reason, uri, detail
    );
  }
}
