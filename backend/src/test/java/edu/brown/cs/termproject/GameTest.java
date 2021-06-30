package edu.brown.cs.termproject;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import edu.brown.cs.termproject.gamebackend.Game;
import edu.brown.cs.termproject.gamebackend.Ghost;
import edu.brown.cs.termproject.gamebackend.Player;

import edu.brown.cs.termproject.gamebackend.gameinternals.Maze;

import java.awt.*;


public class GameTest {

  @Test
  public void addPlayerTest() {
    Game game = new Game();
    game.addPlayer("1", "player1");

    assertEquals(game.getPlayers().values().size(), 1);
    assertEquals(game.getPlayers().get("1").getName(), "player1");
  }

  @Test
  public void movePlayerTest() {

    Game game = new Game();
    game.addPlayer("1", "player1");

    Point p = game.getPlayers().get("1").getLocation();

    game.movePlayer("1", Maze.Direction.NORTH);

    Point p2 = game.getPlayers().get("1").getLocation();

    if (game.getMaze().hasWall(p.x,p.y,Maze.Direction.NORTH)) {
      assertEquals(p.y, p2.y);
    } else {
      assertEquals(p.y - 1, p2.y);
    }
  }

  @Test
  public void killPlayerTest() {

    Game game = new Game();
    game.addPlayer("1", "player1");

    Ghost ghost = new Ghost("-1", new Point(10,10), 1, game.getMaze(), 10);
    Player player = game.getPlayers().get("1");

    assertEquals(player.isAlive(), true);

    game.killPlayer(ghost, player);

    assertEquals(player.isAlive(), false);
  }

  @Test
  public void checkGameStateTest() {

    Game game = new Game();
    game.addPlayer("1", "player1");

    Player player = game.getPlayers().get("1");

    assertEquals(player.isAlive(), true);
    assertEquals(game.getGameStopped(), true);

    game.startGame("", 1, 1, 10);

    assertEquals(game.getGameStopped(), false);
    
    game.getGhosts().get(0).setLocation(new Point(10,10));
    game.checkGameState();

    assertEquals(player.isAlive(), false);
    assertEquals(game.getGameStopped(), true);
  }

}