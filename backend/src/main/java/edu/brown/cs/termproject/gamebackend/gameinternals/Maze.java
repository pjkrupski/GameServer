package edu.brown.cs.termproject.gamebackend.gameinternals;

/**
 * Abstract the data of a single immutable maze.
 *
 * The origin is in the TOP LEFT CORNER.
 * Positive X values indicate moving right.
 * Positive Y values indicate moving down.
 * The array is indexed [x][y], with X first, Y second.
 * X values are indexed first (column)
 * Y values are indexed second (row)
 *
 * So (2, 5) means Second Column, Fifth Row --> Across 2, Down 5
 *
 * The serialization of a maze reflects this (see Maze::serialize)
 *
 * (0,0)----------------(10,0)
 * |                        |    ^
 * |                        |    |
 * |          (5,5)         |   height
 * |                        |    |
 * |                        |    v
 * (0,10)------------------(10,10)
 *         <-- width -->
 */
public final class Maze {

  public enum Direction {
    NORTH(1),
    EAST(2),
    SOUTH(4),
    WEST(8);

    final int value;
    Direction(int _v) {
      this.value = _v;
    }
  }

  private final int[][] mazeData;
  private final int width, height;

  public Maze(final int width, final int height) {
    mazeData = new int[width][height];
    this.width = width;
    this.height = height;
  }

  private void validateMazeEntry(int entry) {
    boolean valid = entry >= 0 && entry < 16;
    if (!valid) {
      throw new AssertionError("invalid maze entry: " + entry);
    }
  }

  private int[] oppositeWall(int x, int y, Direction inDirection) {
    int dx = 0, dy = 0;
    switch (inDirection) {
      case NORTH: dy = -1; break;
      case EAST: dx = 1; break;
      case SOUTH: dy = 1; break;
      case WEST: dx = -1; break;
    }
    return new int[] { x + dx, y + dy };
  }
  private Direction oppositeDirection(Direction dir) {
    switch (dir) {
      case NORTH: return Direction.SOUTH;
      case EAST: return Direction.WEST;
      case SOUTH: return Direction.NORTH;
      case WEST: return Direction.EAST;
    }
    throw new IllegalArgumentException("direction enum should be bounded to 4 cases");
  }

  public void addWall(int x, int y, Direction inDirection) {
    mazeData[x][y] |= inDirection.value;
    validateMazeEntry(mazeData[x][y]);
    final int[] oppCell = oppositeWall(x, y, inDirection);
    final Direction oppDir = oppositeDirection(inDirection);
    if (oppCell[0] >= 0 && oppCell[0] < width && oppCell[1] >= 0 && oppCell[1] < height) {
      mazeData[oppCell[0]][oppCell[1]] |= oppDir.value;
      validateMazeEntry(mazeData[x][y]);
    }
  }

  public void removeWall(int x, int y, Direction inDirection) {
    final int complement = ~(inDirection.value);
    mazeData[x][y] &= complement;
    validateMazeEntry(mazeData[x][y]);
    final int[] oppCell = oppositeWall(x, y, inDirection);
    final Direction oppDir = oppositeDirection(inDirection);
    if (oppCell[0] >= 0 && oppCell[0] < width && oppCell[1] >= 0 && oppCell[1] < height) {
      mazeData[oppCell[0]][oppCell[1]] &= ~(oppDir.value);
      validateMazeEntry(mazeData[x][y]);
    }
  }

  public boolean hasWall(int x, int y, Direction inDirection) {
    return (mazeData[x][y] & inDirection.value) == inDirection.value;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getMazeCell(int x, int y) {
    return mazeData[x][y];
  }

  /**
   *   Compress the maze into a string to be sent to clients
   *   Serializes in coordinate order: (0,0) is first character.
   *   Each maze cell is represented by 1 hex character
   */
  public String serialize() {
    StringBuilder sb = new StringBuilder();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        sb.append(Integer.toHexString(mazeData[x][y]));
      }
      sb.append("\n");
    }
    return sb.toString();
  }
}
