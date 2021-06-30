package edu.brown.cs.termproject.gamebackend.gameinternals;


import java.util.*;

/**
 * Each function in this class creates a different type of maze
 */
public final class MazeGenerator {

  /// Maze with no walls
  public static Maze emptyMaze(int width, int height) {
    return new Maze(width, height);
  }

  /// Maze with all rooms having just a north wall
  public static Maze allNorthWallMaze(int width, int height) {
    final Maze ret = new Maze(width, height);
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        ret.addWall(x, y, Maze.Direction.NORTH);
      }
    }
    return ret;
  }

  /// Maze with all rooms have all walls
  public static Maze allWallMaze(int width, int height) {
    final Maze ret = new Maze(width, height);
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        ret.addWall(x, y, Maze.Direction.NORTH);
        ret.addWall(x, y, Maze.Direction.EAST);
        ret.addWall(x, y, Maze.Direction.SOUTH);
        ret.addWall(x, y, Maze.Direction.WEST);
      }
    }
    return ret;
  }

  /// Maze where each room has 1 random wall
  public static Maze randomSingleWallMaze(int width, int height) {
    final Maze ret = new Maze(width, height);
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        int randIdx = (int)(Math.random() * 4);
        ret.addWall(x, y, Maze.Direction.values()[randIdx]);
      }
    }
    return ret;
  }

  /// Maze where each room has either an east or west wall
  public static Maze eastWestWallOnlyMaze(int width, int height) {
    final Maze ret = new Maze(width, height);
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        Maze.Direction wall = Math.random() < 0.5 ? Maze.Direction.WEST : Maze.Direction.EAST;
        ret.addWall(x, y, wall);
      }
    }
    return ret;
  }

  /// Maze for testing rendering. Pattern starts from top left
  // and repeats in the order of declaration: N,E,S,W,...
  public static Maze testMaze(int width, int height) {
    final Maze ret = new Maze(width, height);
    int counter = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        Maze.Direction wall = Maze.Direction.values()[counter++ % 4];
        ret.addWall(x, y, wall);
      }
    }
    return ret;
  }

  /// random depth first search maze
  public static Maze randomDFS(int width, int height) {
    final Maze ret = new Maze(width, height);
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        ret.addWall(x, y, Maze.Direction.NORTH);
        ret.addWall(x, y, Maze.Direction.EAST);
        ret.addWall(x, y, Maze.Direction.SOUTH);
        ret.addWall(x, y, Maze.Direction.WEST);
      }
    }
    DFSHelper(ret, 0, 0);
    addRandomExit(ret);
    addRandomExit(ret);
    return ret;
  }

  public static void DFSHelper(Maze maze, int x, int y) {
    int counter = 0;
    int randIdx = (int)(Math.random() * 4);
    int[] original = {x, y};
    //System.out.println("current coords " + x + " " + y);
    while (counter < 4) {
      int[] next = original.clone();
      Maze.Direction wall;
      if (randIdx < 1) {
        if (x == maze.getWidth()-1) {
          next[0] = x-1;
          wall = Maze.Direction.WEST;
        } else {
          next[0] = x+1;
          wall = Maze.Direction.EAST;
        }
      } else if (randIdx < 2) {
        if (x == 0) {
          next[0] = x+1;
          wall = Maze.Direction.EAST;
        } else {
          next[0] = x-1;
          wall = Maze.Direction.WEST;
        }
      } else if (randIdx < 3) {
        if (y == maze.getHeight()-1) {
          next[1] = y-1;
          wall = Maze.Direction.NORTH;
        } else {
          next[1] = y+1;
          wall = Maze.Direction.SOUTH;
        }
      } else {
        if (y == 0) {
          next[1] = y+1;
          wall = Maze.Direction.SOUTH;
        } else {
          next[1] = y-1;
          wall = Maze.Direction.NORTH;
        }
      }
      //System.out.println("next coords " + next[0] + " " + next[1]);
      if (!visited(maze, next)) {
        maze.removeWall(x, y, wall);
        //System.out.println("current wall removed " + x + " " + y + " " + wall);
        if (wall == Maze.Direction.NORTH) {
          maze.removeWall(next[0], next[1], Maze.Direction.SOUTH);
          //System.out.println("next wall removed " + next[0] + " " + next[1] + " " + Maze.Direction.SOUTH);
        } else if (wall == Maze.Direction.SOUTH) {
          maze.removeWall(next[0], next[1], Maze.Direction.NORTH);
          //System.out.println("next wall removed " + next[0] + " " + next[1] + " " + Maze.Direction.NORTH);
        } else if (wall == Maze.Direction.WEST) {
          maze.removeWall(next[0], next[1], Maze.Direction.EAST);
          //System.out.println("next wall removed " + next[0] + " " + next[1] + " " + Maze.Direction.EAST);
        } else {
          maze.removeWall(next[0], next[1], Maze.Direction.WEST);
          //System.out.println("next wall removed " + next[0] + " " + next[1] + " " + Maze.Direction.WEST);
        }
        DFSHelper(maze, next[0], next[1]);
      } else {
        counter++;
        randIdx = (randIdx + 1) % 4;
      }
    }
  }

  public static void addExit(Maze maze, Maze.Direction dir) {
    while (true) {
      if (dir == Maze.Direction.EAST) {
        int randIdx = (int)(Math.random() * maze.getHeight());
        if (maze.hasWall(maze.getWidth()-1, randIdx, dir)) {
          maze.removeWall(maze.getWidth()-1, randIdx, dir);
          break;
        } else {
          continue;
        }
      } else if (dir == Maze.Direction.WEST) {
        int randIdx = (int)(Math.random() * maze.getHeight());
        if (maze.hasWall(0, randIdx, dir)) {
          maze.removeWall(0, randIdx, dir);
          break;
        } else {
          continue;
        }
      } else if (dir == Maze.Direction.NORTH) {
        int randIdx = (int)(Math.random() * maze.getWidth());
        if (maze.hasWall(randIdx, 0, dir)) {
          maze.removeWall(randIdx, 0, dir);
          break;
        } else {
          continue;
        }
      } else {
        int randIdx = (int)(Math.random() * maze.getWidth());
        if (maze.hasWall(randIdx, maze.getHeight()-1, dir)) {
          maze.removeWall(randIdx, maze.getHeight()-1, dir);
          break;
        } else {
          continue;
        }
      }
    }
  }

  public static void addRandomExit(Maze maze) {
    int randIdx = (int)(Math.random() * 4);
    if (randIdx < 1) {
      addExit(maze, Maze.Direction.NORTH);
    } else if (randIdx < 2) {
      addExit(maze, Maze.Direction.SOUTH);
    } else if (randIdx < 3) {
      addExit(maze, Maze.Direction.EAST);
    } else {
      addExit(maze, Maze.Direction.WEST);
    }
  }

  public static boolean visited(Maze maze, int[] coords) {
    //System.out.println("checking if visited " + coords[0] + " " + coords[1]);
    boolean ret = false;
    if (!maze.hasWall(coords[0], coords[1], Maze.Direction.NORTH)) {
      ret = true;
    } else if (!maze.hasWall(coords[0], coords[1], Maze.Direction.SOUTH)) {
      ret = true;
    } else if (!maze.hasWall(coords[0], coords[1], Maze.Direction.EAST)) {
      ret = true;
    } else if (!maze.hasWall(coords[0], coords[1], Maze.Direction.WEST)) {
      ret = true;
    }
    //System.out.println("visited " + ret);
    return ret;
  }

  /// random binary tree maze
  public static Maze binaryTree(int width, int height) {
    final Maze ret = new Maze(width, height);
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        ret.addWall(x, y, Maze.Direction.NORTH);
        ret.addWall(x, y, Maze.Direction.EAST);
        ret.addWall(x, y, Maze.Direction.SOUTH);
        ret.addWall(x, y, Maze.Direction.WEST);
      }
    }
    int[] base = {0,0};
    int i = 0;
    HashSet<int[]> oldSum;
    HashSet<int[]> newSum = new HashSet<int[]>();
    newSum.add(base);
    while(i < width+height) {
      oldSum = newSum;
      randomize(oldSum);
      newSum = new HashSet<int[]>();
      Iterator<int[]> iter = oldSum.iterator();
      while(iter.hasNext()) {
        int[] curr = iter.next();
        //System.out.println("current coords " + curr[0] + " " + curr[1]);
        if (curr[1]+1 < height) {
          int[] left = {curr[0], curr[1]+1};
          Iterator<int[]> leftIter = newSum.iterator();
          boolean leftBool = false;
          while(leftIter.hasNext()) {
            int[] leftCurr = leftIter.next();
            if (leftCurr[0] == left[0] && leftCurr[1] == left[1]) {
              leftBool = true;
              break;
            }
          }
          if (!leftBool) {
            newSum.add(left);
          }
          if (!visited(ret, left)) {
            ret.removeWall(curr[0], curr[1], Maze.Direction.SOUTH);
            ret.removeWall(left[0], left[1], Maze.Direction.NORTH);
          }
        }
        if (curr[0]+1 < width) {
          int[] right = {curr[0]+1, curr[1]};
          Iterator<int[]> rightIter = newSum.iterator();
          boolean rightBool = false;
          while(rightIter.hasNext()) {
            int[] rightCurr = rightIter.next();
            if (rightCurr[0] == right[0] && rightCurr[1] == right[1]) {
              rightBool = true;
              break;
            }
          }
          if (!rightBool) {
            newSum.add(right);
          }
          if (!visited(ret, right)) {
            ret.removeWall(curr[0], curr[1], Maze.Direction.EAST);
            ret.removeWall(right[0], right[1], Maze.Direction.WEST);
          }
        }
      }
      i++;
    }
    addExit(ret, Maze.Direction.EAST);
    addExit(ret, Maze.Direction.SOUTH);
    return ret;
  }

  public static void randomize(HashSet<int[]> set) {
    List<int[]> temp = new ArrayList<int[]>(set);
    set.clear();
    while (!temp.isEmpty()) {
      set.add(temp.remove((int)(Math.random() * temp.size())));
    }
  }

  /// random recursive division maze
  public static Maze recursiveDivision(int width, int height) {
    final Maze ret = new Maze(width, height);
    for (int x = 0; x < width; x++) {
      ret.addWall(x, 0, Maze.Direction.NORTH);
      ret.addWall(x, height-1, Maze.Direction.SOUTH);
    }
    for (int y = 0; y < height; y++) {
      ret.addWall(0, y, Maze.Direction.WEST);
      ret.addWall(width-1, y, Maze.Direction.EAST);
    }
    recurDivHelper(ret, 0, width-1, 0, height-1);
    addRandomExit(ret);
    addRandomExit(ret);
    return ret;
  }

  public static void recurDivHelper(Maze maze, int x_start, int x_end, int y_start, int y_end) {
    if(x_start == x_end || y_start == y_end) {
      return;
    }
    if (Math.random() > 0.5) {
      int xRand = (int)(Math.random() * (x_end-x_start)) + x_start;
      for (int i = y_start; i <= y_end; i++) {
        maze.addWall(xRand, i, Maze.Direction.EAST);
      }
      int yRand = (int)(Math.random() * (y_end-y_start+1)) + y_start;
      maze.removeWall(xRand, yRand, Maze.Direction.EAST);
      recurDivHelper(maze, x_start, xRand, y_start, y_end);
      recurDivHelper(maze, xRand+1, x_end, y_start, y_end);
    } else {
      int yRand = (int)(Math.random() * (y_end-y_start)) + y_start;
      for (int i = x_start; i <= x_end; i++) {
        maze.addWall(i, yRand, Maze.Direction.SOUTH);
      }
      int xRand = (int)(Math.random() * (x_end-x_start+1)) + x_start;
      maze.removeWall(xRand, yRand, Maze.Direction.SOUTH);
      recurDivHelper(maze, x_start, x_end, y_start, yRand);
      recurDivHelper(maze, x_start, x_end, yRand+1, y_end);
    }
  }

  public static Maze randomNonMemeMaze(int width, int height) {
    final double r = Math.random();
    if (r < 0.33) return MazeGenerator.randomDFS(width, height);
    if (r < 0.66) return MazeGenerator.binaryTree(width, height);
    return MazeGenerator.recursiveDivision(width, height);
  }

}
