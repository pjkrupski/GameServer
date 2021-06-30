package edu.brown.cs.termproject.gamebackend;

import com.google.gson.Gson;
import edu.brown.cs.termproject.gamebackend.gameinternals.Maze;
import edu.brown.cs.termproject.httpcore.framework.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameServer {

  /// Map a lobby ID into its game
  private Map<String, Game> gameMap = new HashMap<>();
  private final Gson GSON = new Gson();

  public GameServer() {
    new Thread() {
      @Override
      public void run() {
        super.run();
        try {
          while (true) {
            Thread.sleep(500);
            updateGhosts();
          }
        } catch (InterruptedException e) {
          // This would be a shame, but we can't really recover.
          e.printStackTrace();
        }
      }
    }.start();
  }

  private Maze.Direction convertDirection(String fromClient) {
    switch (fromClient) {
      case "1": return Maze.Direction.NORTH;
      case "2": return Maze.Direction.EAST;
      case "4": return Maze.Direction.SOUTH;
      case "8": return Maze.Direction.WEST;
      default: return null;
    }
  }

  public String move(String lobbyId, String playerId, String direction) {
    final Game g = gameMap.get(lobbyId);
    if (g == null) {
      return "null";
    }
    final Maze.Direction dir = convertDirection(direction);
    final String update = g.updateGame(playerId, dir);
    return update;
  }


  /// Thread every .5 seconds
  private synchronized void updateGhosts() {
    for (Game g : gameMap.values()) {
      g.moveGhosts();
    }
  }

  public String listPlayers(String lobbyId) {
    final Game g = gameMap.get(lobbyId);
    if (g == null) {
      return "[]";
    }
    final List<String> names = g.getPlayers().values().stream().map(Player::getName).collect(
        Collectors.toList());
    return GSON.toJson(names);
    //return "[\"john\", \"terry\"]";
  }

  public String createLobby() {
    final String id = generateLobbyId();
    final Game g = new Game();
    g.startGame("", 2, 0, 5);
    g.restartGame();
    gameMap.put(id, g);
    return id;
  }

  public void addPlayer(String lobby, String id, String name) {
    final Game g = gameMap.get(lobby);
    if (g == null) {
      return;
    }
    g.addPlayer(id, name);
  }

  public void restartGame(String lobby) {
    final Game g = gameMap.get(lobby);
    if (g == null) {
      return;
    }
    if (g.getGameStopped()) {
      g.resetGame();
    }
  }

  public boolean isValidLobby(String id) {
    return gameMap.containsKey(id);
  }

  private String generateLobbyId() {
    return UUID.randomUUID().toString().substring(0, 8);
  }

}
