package edu.brown.cs.termproject.httpcore;

import edu.brown.cs.termproject.httpcore.framework.HTTPServer;
import edu.brown.cs.termproject.httpcore.framework.Headers;
import edu.brown.cs.termproject.httpcore.framework.Middleware;
import edu.brown.cs.termproject.httpcore.framework.MiddlewareHelpers;
import edu.brown.cs.termproject.httpcore.framework.Response;

import java.io.IOException;

public class DemoMain {

//  public static void main(String[] args) {
//    System.out.println("Demo main boy");
//    Middleware m = new Middleware();
//    m.addRequestModifier(request -> {
//      request.headers.set("brian-server", "true");
//    });
//    m.addRequestResponseMapper(MiddlewareHelpers.serveStatic("/etc/profile"));
//    m.addRequestResponseMapper(request -> new Response(request.headers, "body"));
//    m.addResponseModifier(response -> {
//      response.headers.set("out-heard", "dander");
//    });
//    try {
//      HTTPServer s = new HTTPServer(8080, m);
//      s.run();
//    } catch (IOException ioe) {
//      System.out.println("Could not establish server");
//      ioe.printStackTrace();
//    }
//  }
}
