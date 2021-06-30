package edu.brown.cs.termproject.gamebackend;

import edu.brown.cs.termproject.gamebackend.gameinternals.Maze;
import edu.brown.cs.termproject.httpcore.framework.HTTPServer;
import edu.brown.cs.termproject.httpcore.framework.Middleware;
import edu.brown.cs.termproject.httpcore.framework.MiddlewareHelpers;
import edu.brown.cs.termproject.httpcore.framework.Response;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Ghost extends Unit {
	int speed = 0;
	int proximity;
	Maze mz;
	List<Player> players;
	
	public Ghost(String id, Point loc, int speed, Maze mz, int ghostProximity) {
		super(id, loc, "Ghost");
		this.speed = speed;
		proximity = ghostProximity;
		this.mz = mz;
	}

	/**
	 * The speed variable will be used as a number to be added when determining
	 * the ghosts new location after a move. The larger the speed variable
	 * is the faster the ghost will move towards the player. This can be an adjustable
	 * parameter depending on what level the players want the game to be
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	/**
	 * The proximity variable will be used as a limit to how far a ghost can see
	 * players on the map when determining who to chase. The larger the proximity variable
	 * is the more likely the ghost will find a player and move towards them. This can be an
	 * adjustable parameter depending on what level the players want the game to be
	 */
	public void setProximity(int proximity) {
		proximity = proximity;
	}

	/**
	 * move will execute all the necessary static functions to
	 * filter out only live players, scan the game board and
	 * determine where the players are, discover which player
	 * is closest and move towards them at a variable speed,
	 * or move in a random direction if there are no players
	 * within the proximity.
	 * If there are no players in the game, the players argument
	 * will be null and the ghost will move randomly
	 * @param players a list of all players in the game
	 */
	public void move(List<Player> players) {
//		System.out.println("Ghost " + this.getId()
//				+ " is about to move from its location"
//				+ " at " + "x = " + this.getLocation().getX()
//				+ " y = " + this.getLocation().getY());
		if (players == null) {
			randomMove();
		} else {
			List<Player> playersInProximity;
			Player closestPlayer;
			players = filterLivePLayers(players);
			playersInProximity = scanProximity(players);
			//If there are no players in the proximity the ghost
			//will move randomly
			if (playersInProximity.size() == 0) {
				randomMove();
			} else {
				//Move towards closest player
				closestPlayer = findClosestPlayer(playersInProximity);
				moveTowardsPlayer(closestPlayer);
			}
		}
//		System.out.println("Ghost " + this.getId()
//				+ " has moved to location "
//				+ "x = " + this.getLocation().getX()
//				+ " y = " + this.getLocation().getY());
	}

	/**
	 * Builds list of players that are within the ghosts proximity
	 * @param players a list of all players in the game
	 * @return playersInProximity list of players within proximity
	 */
	public List<Player> filterLivePLayers(List<Player> players) {
		List<Player> livePlayers = new ArrayList<>();
		for (Player player : players) {
			if (player.isAlive()) {
				livePlayers.add(player);
			}
		}
		return livePlayers;
	}

	/**
	 * Builds list of players that are within the ghosts proximity
	 * @param players a list of all players in the game
	 * @return playersInProximity list of players within proximity
	 */
	public List<Player> scanProximity(List<Player> players) {
		List<Player> playersInProximity = new ArrayList<>();
		for (Player player : players) {
			if (calculateDistance(player) <= proximity) {
				// How would this be null??????
				playersInProximity.add(player);
			}
		}
		return playersInProximity;
	}

	/**
	 * When given a list of players that are within the ghosts proximity,
	 * this function will use the calculateDistance function to determine
	 * which of the players is closest so the ghost can move towards it.
	 * If there are no players wthin the proximity, the ghost will move randomly
	 * @param players a list of all players within the proximity
	 * @return closestPlayer or nullin the case of no players being within proximity
	 */
	public Player findClosestPlayer(List<Player> players) {
		double distance = 0;
		double closestPlayerDistance;
		Player closestPlayer;
		// Initialize closest player and the closest player distance to be the first in list
		closestPlayer = players.get(0);
		closestPlayerDistance = calculateDistance(players.get(0));
		// Find and return closest player starting with second in the list
		for (int i = 1; i < players.size(); i++) {
			distance = calculateDistance(players.get(i));
			if (distance < closestPlayerDistance) {
				closestPlayer = players.get(i);
				closestPlayerDistance = distance;
			}
		}
		return closestPlayer;
	}

	/**
	 * Returns
	 * @param player is the player that has been determined to be closest to
	 *               the ghosts current location
	 * @return a double that is the euclidean distance from the ghost
	 * and the player that it has chosen to move towards
	 */
	public double calculateDistance(Player player) {
		return Math.sqrt(Math.pow(player.getLocation().getX()
				- getLocation().getX(), 2) + Math.pow(player.getLocation().getY()
				- getLocation().getY(), 2));
	}

	/**
	 * If there does not exist any players within the ghosts proximity
	 * the random number generator will be used to determine the next
	 * move of the ghost. If math.random returns an odd number for a
	 * given dimension, the ghost will move -1 in that direction.
	 * If the random number is even, the ghost will move +1 in that
	 * direction
	 */
	public void randomMove() {
		//TODO: Determine map bounds
		// If odd numebr move negative
		// If even move positive
		int x = (int) (Math.random()*10);
		int y = (int) (Math.random()*10);
		if (x % 2 > 0) {
			x = -1 - speed;
		} else {
			x = 1 + speed;
		}
		if (y % 2 > 0) {
			y = -1 - speed;
		} else {
			y = 1 + speed;
		}
		//Validate random X && Y move before calling setLocatio
		if (validateMoveWidth((int) getLocation().getX() + x)
				&& validateMoveHeight((int) getLocation().getY() + y)) {
			setLocation(new Point((int) getLocation().getX() + x,
					(int) getLocation().getY() + y));
		}
	}

	public void moveTowardsPlayer(Player closestPlayer) {
		//Update X
		if (getLocation().getX() < closestPlayer.getLocation().getX()) {
			//Target player has greater X so increment ghosts X coordinate

			//Validate move in X direction
			if (validateMoveWidth((int) getLocation().getX() + 1)) {
				setLocation(new Point((int) getLocation().getX() + 1, (int) getLocation().getY()));
				// If validateMove returned false it will have shifted the ghost
				//one square away from the wall it was about to collide with
			}
		} else {
			//Target player has lesser X
			if (validateMoveWidth((int) getLocation().getX() - 1)) {
				setLocation(new Point((int) getLocation().getX() - 1, (int) getLocation().getY()));
			}
		}

		//Update Y
		if (getLocation().getY() < closestPlayer.getLocation().getY()) {
			//Target player has greater Y
			if (validateMoveHeight((int) getLocation().getY() + 1)) {
				setLocation(new Point((int) getLocation().getX(), (int) getLocation().getY() + 1));
				// If validateMove returned false it will have shifted the ghost
				//one square away from the wall it was about to collide with
			}
		} else {
			//Target player has lesser Y
			if (validateMoveHeight((int) getLocation().getY() - 1)) {
				setLocation(new Point((int) getLocation().getX(), (int) getLocation().getY() - 1));
			}
		}
	}

	/**
	 * If a move will go off the board, this method will
	 * update the ghosts location to be 1 square away
	 * from the side of the board it was about to collide with.
	 * If the move is valid, the method will simply return true
	 *
	 * Validation is needed for every move to account for the ghosts
	 * speed being a nonzero number.
	 */

	public boolean validateMoveWidth(int x) {
		// Check X
		if (x > mz.getWidth()) {
			setLocation(new Point((int) getLocation().getX()
					- 1, (int) getLocation().getY()));
		} else if (x < 0){
			setLocation(new Point((int) getLocation().getX()
					+ 1, (int) getLocation().getY()));
		} else {
			return true;
		}
		return false;
	}

	public boolean validateMoveHeight(int y) {
		// Check Y
		if (y > mz.getHeight()) {
			setLocation(new Point((int) getLocation().getX(),
					(int) getLocation().getY() - 1));
		} else if (y < 0) {
			setLocation(new Point((int) getLocation().getX(),
					(int) getLocation().getY() + 1));
		} else {
			return true;
		}
		return false;
	}


	/**
	 * Takes in the lcation of the closest player. Will
	 * determine the edges in the current box and update
	 * the next move based on where the player is most likely
	 * to move since they will be restricted by edges
	 * @param
	 * @param
	 * @Return
	 */
	public void minimax() {
		// Moves based on walls in a cell of the closest player
		//mz.hasWall?
	}

}
