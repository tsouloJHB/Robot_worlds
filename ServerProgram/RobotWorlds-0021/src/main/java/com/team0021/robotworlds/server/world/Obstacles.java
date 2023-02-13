package com.team0021.robotworlds.server.world;

import java.util.Stack;

/**
 * Defines an interface for obstacles you want to place in your world.
 */
public interface Obstacles {
    Stack<Position> getObstacles();

    /**
     * Checks if this obstacle blocks access to the specified position.
     * @param position the position to check
     * @return return `true` if the x,y coordinate falls within the obstacle's area
     */
    boolean blocksPosition(Position position);

    /**
     * Checks if this obstacle blocks the path that goes from coordinate (x1, y1) to (x2, y2).
     * Since our robot can only move in horizontal or vertical lines (no diagonals yet), we can assume that either x1==x2 or y1==y2.
     * @param a first position
     * @param b second position
     * @return `true` if this obstacle is in the way
     */
    boolean blocksPath(Position a, Position b);
}
