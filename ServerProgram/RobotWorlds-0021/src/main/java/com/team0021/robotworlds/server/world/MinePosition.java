package com.team0021.robotworlds.server.world;

/**
 * Class provides implementation for the MinePosition class. It is used to 
 * encapsulate x, y values and indicate where two objects of the class are equal.
*/
public class MinePosition {
    private final int x;
    private final int y;

    /**
     * Class constructor invoked using x, y coordinates/values.
     * @param x coordinate
     * @param y coordinate
    */
    public MinePosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    

    /**
     * Function used to retrieve the x value of a MinePosition object.
     * @return x coordinate
     */ 
    public int getX() {
        return x;
    }


    /**
     * Function used to retrieve the y value of a MinePosition object.
     * @return y coordinate
     */ 
    public int getY() {
        return y;
    }


    /**
     * Functions used check whether a position is blocked by a Mine.
     * @param newPosition The new position we will be using to check.
     * @return boolean object; true if position is occupied by a Mine. 
     * false if position is not occupied by a Mine.
    */
    public boolean isPositionBlockedByMine(Position newMinePosition){
        int x1 = newMinePosition.getX();
        int y1 = newMinePosition.getY();
        boolean condition = false;
        if(x1 == this.x && y1 == this.y){
            condition = true;
        }
        return condition;
    }


    /**
     * Functions used check whether a path is blocked by a Mine.
     * @param a The current position of the robot.
     * @param b The new position of the robot.
     * @return boolean object; true if path is blocked by a Mine. 
     * false if path is not blocked by a Mine.
    */
    public boolean isPathBlockedByMine(Position a, Position b){
        int currentX = a.getX();
        int currentY = a.getY();
        int finalX = b.getX();
        int finalY = b.getY();
        int minValue, maxValue;

        if(currentX == finalX){
            minValue = Math.min(currentY, finalY);
            maxValue = Math.max(currentY, finalY);
            Position tempPosition;
            
            for (int i=minValue; i<=maxValue;i++){
                
                tempPosition = new Position(currentX, i);
                if(isPositionBlockedByMine(tempPosition)){
                    return true;
                }
            }
        }

        if(currentY == finalY){
            minValue = Math.min(currentX, finalX);
            maxValue = Math.max(currentX, finalX);
            Position tempPosition;
            
            for (int i=minValue; i<=maxValue;i++){
                
                tempPosition = new Position(i, currentY);
                if(isPositionBlockedByMine(tempPosition)){
                    return true;
                }
            }
        }
        return false;
    }
}
