package edu.brown.cs.termproject;

import edu.brown.cs.termproject.gamebackend.Game;
import org.junit.Test;
import java.awt.*;

import static org.junit.Assert.*;

public class GhostTests {


    @Test
    public void testCreation() {
        Game g = new Game();
        int x = 0, y = 0;
        g.startGame("neverUsed", 1, 0, 0);
        assertNotEquals(null, (int) g.getGhosts().get(0).getLocation().getX());
        assertNotEquals(null, (int) g.getGhosts().get(0).getLocation().getY());
    }

    @Test
    public void testGhostRandomMove() {
        Game g = new Game();
        g.startGame("neverUsed", 1, 0, 0);
        int x = (int) g.getGhosts().get(0).getLocation().getX();
        int y = (int) g.getGhosts().get(0).getLocation().getY();
        g.moveGhosts();
        assertNotEquals(x, (int) g.getGhosts().get(0).getLocation().getX());
        assertNotEquals(y, (int) g.getGhosts().get(0).getLocation().getY());
    }

    @Test
    public void testGhostTargetedMove() {
        Game g = new Game();
        g.startGame("neverUsed", 1, 0, 10);
        int x = (int) g.getGhosts().get(0).getLocation().getX();
        int y = (int) g.getGhosts().get(0).getLocation().getY();
        g.addPlayer("1", "testPlayer");

        //Get the one ghost and set its location to be two less
        // in the x and y direction as testPLayer
        g.getGhosts().get(0).setLocation(new Point((int)
                g.getPlayers().get("1").getLocation().getX()
                - 2, (int) g.getPlayers().get("1").getLocation().getY()
                - 2));

        // Now that the ghost location is known and the player is within
        // the ghosts proximity, the ghost will execute a targeted move
        // towards the player
        g.moveGhosts();

        // Test ghost is not in its original location
        assertNotEquals(x, (int) g.getGhosts().get(0).getLocation().getX());
        assertNotEquals(y, (int) g.getGhosts().get(0).getLocation().getY());

        //Rewrite x, y to be location of player
        x = (int) g.getPlayers().get("1").getLocation().getX();
        y = (int) g.getPlayers().get("1").getLocation().getY();

        //Test ghost has moved 1 x space and 1 y space towards the player
        assertEquals(x - 1, (int) g.getGhosts().get(0).getLocation().getX());
        assertEquals(y - 1, (int) g.getGhosts().get(0).getLocation().getY());


    }

    /**
       This is not a perfect test.
       To test the proximity parameter, the ghost will be set to (0, 0)
       and the player will be set to (10, 10). The player will not move
       and the ghost will be tested on its ability to converge towards the
       player. If the ghost has a maximum proximity, it is expected that the
       ghost will land on the players location in 10 moves.

       If the ghost has a zero proximity, it is expected that it will move
       randomly for 10 moves and not capture the player. However, there exists
       a small probability P(x) = 9.5 X 10^-7 that the ghost will randomly move
       in the correct direction 10 times in a row causing it to capture the player
       completely on accident. In this edge case, the test will fail.

       Probability calculation,
       There is a 1/2 probability that the ghost will move in the correct X direction
       and a 1/2 probability it will move in the correct Y direction. Thus the probability
       of a correct move is 1/2 * 1/2 = 1/4

       With the player being 10 moves away, the ghost would need to make a correct move 10
       times in a row to capture the player, this probability is equal to
       (1/4 * 1/4 ... * 1/4) = 1/4^10 = 9.5 X 10^-7

     */

    @Test
    public void testGhostZeroProximity() {
        Game g = new Game();
        g.startGame("neverUsed", 1, 0, 0);
        g.getGhosts().get(0).setLocation(new Point(0, 0));
        g.addPlayer("1", "testPlayer");

        //Get location of player
        int x = (int) g.getPlayers().get("1").getLocation().getX();
        int y = (int) g.getPlayers().get("1").getLocation().getY();

        for (int i = 0; i < 10; i++) {
            g.moveGhosts();
        }

        //Test ghost and player do not have same location
        assertNotEquals(x, (int) g.getGhosts().get(0).getLocation().getX());
        assertNotEquals(y, (int) g.getGhosts().get(0).getLocation().getY());
    }

    @Test
    public void testGhostMaxProximity() {
        Game g = new Game();
        g.startGame("neverUsed", 1, 0, 99);
        g.getGhosts().get(0).setLocation(new Point(0, 0));
        g.addPlayer("1", "testPlayer");

        //Get location of player
        int x = (int) g.getPlayers().get("1").getLocation().getX();
        int y = (int) g.getPlayers().get("1").getLocation().getY();

        for (int i = 0; i < 10; i++) {
            g.moveGhosts();
        }

        //Test ghost and player do not have same location
        assertEquals(x, (int) g.getGhosts().get(0).getLocation().getX());
        assertEquals(y, (int) g.getGhosts().get(0).getLocation().getY());
    }
    
}