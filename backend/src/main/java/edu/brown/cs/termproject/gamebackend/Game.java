package edu.brown.cs.termproject.gamebackend;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.termproject.gamebackend.gameinternals.Maze;
import edu.brown.cs.termproject.gamebackend.gameinternals.MazeGenerator;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Game {

	private List<Unit> _units;
	private Map<String, Player> _players;
	private List<Ghost> _ghosts;

	private Maze _maze;

	private boolean _gamestopped;
	private String winner = "";

	private Random _rand;

	private static final Gson GSON = new Gson();


	private int round = 0;


	/**
	 * Constructor for the Game class, sets up the hashmap and lists, initializes the gamestopped state to true
	 */
	public Game() {
		_units = new ArrayList();

		_players = new HashMap();
		_ghosts = new ArrayList();

		_gamestopped = true;
		_rand = new Random();

		_maze = MazeGenerator.randomDFS(20, 20);
	}

	public synchronized void addPlayer(String id, String name) {
		Player player = new Player(id, new Point(10,10), name);

		_players.put(id, player);
		_units.add(player);
	}


	/**
	 * synchronized moveUnit method, takes in an id and direction and moves the unit accordingly if move is valid
	 * @param id unit id
	 * @param direction the move's direction
	 */
	public void movePlayer(String id, Maze.Direction direction) {
		if (_gamestopped) {
			return;
		}

		Player u = _players.get(id);

		if (!u.isAlive() || u.hasEscaped()) {
			return;
		}

		Point p = u.getLocation();

		//System.out.println("about to move player: " + direction);
		if (!_maze.hasWall(p.x,p.y,direction)) {
			u.move(direction);

			if (u.getLocation().x > 19 || u.getLocation().x < 0 || u.getLocation().y > 19 || u.getLocation().y < 0) {
				u.setEscaped(true);
				u.setScore(u.getScore() + 1);
			}

			//System.out.println("Unit " + id + " moved from " + p + " to " + u.getLocation());
		} else {
			//System.out.println("Invalid move attempt");
		}
	}

	/**
	 * synchronized kill player method, kills the player and adds score accordingly to the ghost and player
	 * @param g the ghost that killed the player
	 * @param p the player to be killed
	 */
	public void killPlayer(Ghost g, Player p) {
		p.setAlive(false);
	}

	/**
	 * in case some user leaves the game we can remove them from the game
	 * @param id the id of the user to be removed
	 */
	public synchronized void removeUnit(int id) {
		Unit u = _units.get(id);

		_players.remove(u.getId());
		_ghosts.remove(u);

		_units.remove(u);
	}

	/**
	 * method that moves the ghosts
	 */
	public void moveGhosts() {

		List<Player> players =  new ArrayList<>(_players.values());

		for (Ghost ghost: _ghosts) {
			ghost.move(players);
		}
	}

	/**
	 * checks the game for any kills/win conditions being met
	 */
	public void checkGameState() {

		for (Ghost g: _ghosts) {
			for (Player p: _players.values()) {
				if (p.isAlive() && (p.getLocation().equals(g.getLocation()))) {
					killPlayer(g, p);
				}
			}
		}

		for (Player p: _players.values()) {
			if (p.isAlive() && p.hasEscaped()) {
				// players win
				//p.setScore(p.getScore() + 1);
				_gamestopped = true;
				winner = "players";
			}
		}

		boolean allDead = true;
		for (Player p: _players.values()) {
			if (p.isAlive()) {
				allDead = false;
				break;
			}
		}

		if (allDead) {
			_gamestopped = true;
			winner = "ghosts";
		}

	}

	/**
	 * @return a boolean indicating whether the game has ended or not
	 */
	public boolean getGameStopped() {
		return _gamestopped;
	}

	/**
	 * sets up the game
	 * @param mazetype the type of maze for the game
	 * @param numghosts number of ghosts
	 * @param ghostspeed speed of ghosts
	 */
	public void startGame(String mazetype, int numghosts, int ghostspeed, int ghostprox) {

		//to be changed
		int x = _rand.nextInt(20);
		int y = _rand.nextInt(20);

		for (int i=0; i<numghosts; i++) {
			Ghost g = new Ghost(Integer.toString(i), new Point(x,y), ghostspeed, _maze, ghostprox);

			_ghosts.add(g);
		}

		_gamestopped = false;
	}

	/**
	 * restarts the game and set all players' scores to 0
	 */
	public void restartGame() {

		for (Player p: _players.values()) {
			p.setScore(0);
		}

		resetGame();
	}

	/**
	 * resets the game and set all units to their original locations/spawn points.
	 * refreshes the maze.
	 */
	public synchronized void resetGame() {
		_maze = MazeGenerator.randomNonMemeMaze(20,20);

		//to be changed
		for (Player p: _players.values()) {
			p.setAlive(true);
			p.setLocation(new Point(10,10));
			p.setEscaped(false);
		}

		int x = _rand.nextInt(20);
		int y = _rand.nextInt(20);

		//to be changed
		for (Ghost g: _ghosts) {
			g.setLocation(new Point(x,y));
		}

		_gamestopped = false;
		winner = "";
		round++;
	}

	/**
	 * @return a json dictionary of units that are in String[] format (id, x, y, isalive)
	 */
	public synchronized String updateGame(String id, Maze.Direction move) {

		//System.out.println("updating game");

		if (!_gamestopped && move != null) {
			//System.out.println("moving player");
			movePlayer(id, move);
		}
		checkGameState();
		if (_gamestopped) {
			// the round ended
			// wait on a client to restart the game
		}
		List<String[]> p = new ArrayList();

		//adds the updates from the hashmap into the arraylist

		for(Player player: _players.values()) {
			String[] s = new String[]{player.getId(), Integer.toString(player.getLocation().x), Integer.toString(player.getLocation().y), player.getName(), Long.toString(player.getScore()), Boolean.toString(player.isAlive())};
		  p.add(s);
		}

		List<String[]> g = new ArrayList<>();

		//adds the updates from the hashmap into the arraylist

		for(Ghost ghost: _ghosts) {
			String[] s = new String[]{ghost.getId(), Integer.toString(ghost.getLocation().x), Integer.toString(ghost.getLocation().y), ghost.getName()};
			g.add(s);
		}

		String maze = _maze.serialize();

		/// Hey John, the GSON library will automatically JSON-ify these variables.
		Map<String, Object> vars = new HashMap<>();
		vars.put("maze", maze);
		vars.put("players", _players.values());
		vars.put("ghosts", _ghosts);
		vars.put("gamestopped", _gamestopped);
		vars.put("winner", winner);
		vars.put("round", round);

		return GSON.toJson(vars);
	}


	public List<Unit> getUnits() {
		return _units;
	}

	public Map<String, Player> getPlayers() {
		return _players;
	}

	public List<Ghost> getGhosts() {
		return _ghosts;
	}

	public Maze getMaze() {
		return _maze;
	}
}
