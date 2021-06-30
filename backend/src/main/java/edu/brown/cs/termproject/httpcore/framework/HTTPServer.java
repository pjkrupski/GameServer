package edu.brown.cs.termproject.httpcore.framework;

import edu.brown.cs.termproject.httpcore.config.HTTPCoreConfig;
import edu.brown.cs.termproject.httpcore.net.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HTTPServer {
  private final ServerSocket serverSocket;
  private boolean running = false;
  private final Middleware middleware;

  public HTTPServer(int port, Middleware middleware) throws IOException {
    this.serverSocket = new ServerSocket(port);
    this.middleware = middleware;
  }

  public void stop() {
    try {
      running = false;
      serverSocket.close();
    } catch (IOException e) {
      // Do nothing, the user wanted to close anyway.
      e.printStackTrace();
    }
  }

  public void run() {
    new Thread() {
      @Override
      public void run() {
        super.run();
        System.out.println("Waiting for clients on port " + serverSocket.getLocalPort() + "...");
        ExecutorService executor = Executors.newFixedThreadPool(HTTPCoreConfig.THREADPOOL_SIZE);
        running = true;
        while (running) {
          try {
            Socket sock = serverSocket.accept();
            executor.submit(new ClientHandler(sock, middleware));
          } catch (SocketException se) {
              // The server has been closed, nothing wrong
            se.printStackTrace();
          } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
          }
        }
      }
    }.start();
  }
}
