package com.team0021.robotworlds.server.world;

import java.util.Stack;
import java.util.Random;

public class SquareObstacles implements Obstacles 
{
    private Stack<Position> obstacleList = new Stack<Position>();
    private Random randomSeed;

    public SquareObstacles(Random randomSeed){
        this.randomSeed = randomSeed;
    }

    /**
     * @return the list of obstacles, or an empty list if no obstacles exist.
     */
    @Override
    public Stack<Position> getObstacles(){
        if(this.obstacleList.size() < 1){
            int obstaclesNumber = this.randomSeed.nextInt(90)+1;
            int obstacleX;
            int obstacleY;
            Position obstacleCoordinate;
            for (int i=0; i<obstaclesNumber;i++)
            {
                obstacleX =  this.randomSeed.nextInt(200)-100;
                obstacleY =  this.randomSeed.nextInt(200)-100;
                obstacleCoordinate = new Position(obstacleX, obstacleY);
                this.obstacleList.add(obstacleCoordinate);
            }
        }
        return this.obstacleList;
    }


    /**
     * Checks if this obstacle blocks access to the specified position.
     * @param position the position to check
     * @return return `true` if the x,y coordinate falls within the obstacle's area
     */
    @Override
    public boolean blocksPosition(Position position) {
        int testX = position.getX();
        int testY = position.getY();
        for(Position obs: this.obstacleList){
            if (testX >= obs.getX() && testX<= obs.getX()+4 && testY>= obs.getY()&& testY<= obs.getY()+4)
                    return true;
        }
        return false;
    }


     /**
     * Checks if this obstacle blocks the path that goes from coordinate
     *  (x1, y1) to (x2, y2).
     * Since our robot can only move in horizontal or vertical lines 
     * (no diagonals yet), we can assume that either x1==x2 or y1==y2.
     * @param a first position
     * @param b second position
     * @return `true` if this obstacle is in the way
     */
    @Override
    public boolean blocksPath(Position a, Position b) {
        
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
                if(blocksPosition(tempPosition)){
                    return true;
                }
            }
            
        }

        else if(currentY == finalY){
            minValue = Math.min(currentX, finalX);
            maxValue = Math.max(currentX, finalX);
            Position tempPosition;
            for (int i=minValue; i<=maxValue;i++){
                tempPosition = new Position(i, currentY);
                if(blocksPosition(tempPosition)){
                    return true;
                }
            }
        
        }
        return false;
    }
}
