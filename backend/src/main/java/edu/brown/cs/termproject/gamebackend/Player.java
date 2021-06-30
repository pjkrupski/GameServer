package edu.brown.cs.termproject.gamebackend;

import java.awt.*;

public class Player extends Unit {

	private long _score;

	private boolean _alive;

	private boolean _escaped;

	public Player(String id, Point loc, String name) {
		super(id, loc, name);

		_alive = true;
		_score = 0;
	}

	public void setScore(long score) {
		_score = score;
	}

	public long getScore() {
		return _score;
	}

	public void setAlive(boolean b) {
		_alive = b;
	}

	public boolean isAlive() {
		return _alive;
	}

	public void setEscaped(boolean b) {
		_escaped = b;
	}

	public boolean hasEscaped() {
		return _escaped;
	}
}
