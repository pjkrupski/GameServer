package edu.brown.cs.termproject.httpcore.net;

import edu.brown.cs.termproject.httpcore.datastructures.Either;
import edu.brown.cs.termproject.httpcore.framework.FailureReason;
import edu.brown.cs.termproject.httpcore.framework.HTTPResponseStatus;
import edu.brown.cs.termproject.httpcore.framework.Middleware;
import edu.brown.cs.termproject.httpcore.framework.Request;
import edu.brown.cs.termproject.httpcore.framework.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ClientHandler implements Runnable {

  /// Incremented by each instance
  private static int totalClientCount = 0;

  private final Socket socket;
  private final int id;
  final Middleware middleware;

  public ClientHandler(Socket socket, Middleware middleware) {
    this.socket = socket;
    this.id = ++ClientHandler.totalClientCount;
    this.middleware = middleware;
  }

  @Override
  public void run() {
    OutputStream os = null;
    try (InputStream is = this.socket.getInputStream()) {
      os = this.socket.getOutputStream();
      final Either<Request, FailureReason> either = HTTPRequestParser.parseRequest(is);

      if (either != null && either.isRight()) {
        final Response res = new Response(either.getRight().toString());
        res.status = HTTPResponseStatus.BAD_REQUEST;
        os.write(res.serialize());
      } else if (either != null && either.isLeft()) {
        final Request req = either.getLeft();
        final Response res = middleware.map(req);
        os.write(res.serialize());
      }
      is.close();
      os.close();
      socket.close();

    } catch (Exception e) {
      final Response fiveHundred = Response.fiveHundred(Arrays.toString(e.getStackTrace()));
      e.printStackTrace();
      if (os != null) {
        try {
          os.write(fiveHundred.serialize());
          os.close();
          socket.close();
        } catch (IOException ioe) {
          // can't really do anything here
        }
      }
    }
  }

}
