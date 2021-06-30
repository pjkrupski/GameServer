package edu.brown.cs.termproject.gamebackend;

import com.google.gson.Gson;
import edu.brown.cs.termproject.gamebackend.netobjects.PlayerUpdate;
import edu.brown.cs.termproject.httpcore.framework.HTTPServer;
import edu.brown.cs.termproject.httpcore.framework.Headers;
import edu.brown.cs.termproject.httpcore.framework.Middleware;
import edu.brown.cs.termproject.httpcore.framework.MiddlewareHelpers;
import edu.brown.cs.termproject.httpcore.framework.Response;
import edu.brown.cs.termproject.httpcore.net.HTTPRequestParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

public final class Main {

    final static Gson GSON = new Gson();


    public static void main(String[] args) throws Exception {

        Middleware m = new Middleware();
        GameServer gs = new GameServer();

        m.addRequestResponseMapper(
            MiddlewareHelpers.serveStatic(
                "/static/game.js",
                "/static/lobby.js",
                "/static/index.js",
                "/static/index.html",
                "/static/styles.css"));
        m.addRequestResponseMapper(request -> {

            if (request.URL.equals("/api/update")) {
                final String[] tokens = request.body.split("\n");
                if (tokens.length == 3) {
                    final String lobbyId = tokens[0];
                    final String playerId = tokens[1];
                    final String playerMove = tokens[2];
                    try {
                        return new Response(gs.move(lobbyId, playerId, playerMove));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("bad request");
                }
            }

            if (request.URL.equals("/api/newlobby")) {
                final String lobbyId = gs.createLobby();
                return new Response(lobbyId);
            }
            if (request.URL.equals("/api/islobby")) {
                return new Response(Boolean.toString(
                    gs.isValidLobby(request.body)
                ));
            }
            if (request.URL.equals("/api/players")) {
                return new Response(gs.listPlayers(request.body));
            }
            if (request.URL.equals("/api/addplayer")) {
                final String[] tokens = request.body.split("\n");
                if (tokens.length == 3) {
                    final String lobbyId = tokens[0];
                    final String playerId = tokens[1];
                    final String playerName = tokens[2];
                    gs.addPlayer(lobbyId, playerId, playerName);
                    return new Response("");
                }
            }
            if (request.URL.equals("/api/restartgame")) {
                final String lobby = request.body;
                gs.restartGame(lobby);
                return new Response("");
            }

            return null;
        });
        m.addRequestResponseMapper(MiddlewareHelpers.forofor());

        try {
            HTTPServer s = new HTTPServer(8080, m);
            s.run();
        } catch (Exception ioe) {
            System.out.println("Could not establish server");
            ioe.printStackTrace();
            System.exit(1);
        }

    }
}
