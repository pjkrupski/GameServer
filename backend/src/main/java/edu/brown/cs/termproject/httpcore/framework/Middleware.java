package edu.brown.cs.termproject.httpcore.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Middleware {

  private final List<Consumer<Request>> requestModifiers = new ArrayList<>();
  private final List<Consumer<Response>> responseModifiers = new ArrayList<>();
  private final List<Function<Request, Response>> requestResponseMappers = new ArrayList<>();



  public void addRequestModifier(Consumer<Request> mod) {
    requestModifiers.add(mod);
  }

  public void addResponseModifier(Consumer<Response> mod) {
    responseModifiers.add(mod);
  }

  public void addRequestResponseMapper(Function<Request, Response> map) {
    requestResponseMappers.add(map);
  }



  public Response map(Request req) {
    for (Consumer<Request> fn : requestModifiers) {
      fn.accept(req);
    }
    Response res = null;
    for (Function<Request, Response> fn : requestResponseMappers) {
      res = fn.apply(req);
      if (res != null) {
        break;
      }
    }
    if (res == null) {
      System.err.println("Assertion Error: request did not get mapped to a response");
      System.err.println(req);
      System.exit(1);
    }

    for (Consumer<Response> fn : responseModifiers) {
      fn.accept(res);
    }

    return res;
  }
}
