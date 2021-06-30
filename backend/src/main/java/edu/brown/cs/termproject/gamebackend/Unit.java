package edu.brown.cs.termproject.gamebackend;

import edu.brown.cs.termproject.gamebackend.gameinternals.Maze;

import java.awt.*;

public class Unit {

	private String _id;
	private Point _loc;

	public String _name;

	public Unit(String id, Point loc, String name) {
		_id = id;
		_loc = loc;
		_name = name;
	}

	public String getId() {
		return _id;
	}

	public void setLocation(Point point) {
		_loc = point;
	}

	public void move(Maze.Direction direction) {
		switch (direction) {
			case NORTH:
				_loc = new Point(getLocation().x, getLocation().y - 1);
				break;

			case EAST:
				_loc = new Point(getLocation().x + 1, getLocation().y);
				break;

			case SOUTH:
				_loc = new Point(getLocation().x, getLocation().y + 1);
				break;

			case WEST:
				_loc = new Point(getLocation().x - 1, getLocation().y);
				break;
		}
	}

	public Point getLocation() {
		return _loc;
	}

	public String getName() {
		return _name;
	}

}
