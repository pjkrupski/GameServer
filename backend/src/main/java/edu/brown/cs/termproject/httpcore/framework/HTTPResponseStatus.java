package edu.brown.cs.termproject.httpcore.framework;

/**
 * https://www.restapitutorial.com/httpstatuscodes.html
 */
public enum HTTPResponseStatus {

  OK(200, "OK"),
  BAD_REQUEST(400, "Bad Request"),
  UNAUTHORIZED(401, "Unauthorized"),
  FORBIDDEN(403, "Forbidden"),
  NOTFOUND(404, "Not Found"),
  INTERNAL_SERVER_ERROR(500, "Internal Server Error");

  final int status;
  final String description;

  HTTPResponseStatus(int status, String description) {
    this.status = status;
    this.description = description;
  }

  @Override
  public String toString() {
    return String.format("%d %s", status, description);
  }
}

