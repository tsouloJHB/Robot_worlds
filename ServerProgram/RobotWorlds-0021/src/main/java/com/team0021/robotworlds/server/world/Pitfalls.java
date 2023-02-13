package com.team0021.robotworlds.server.world;

import java.util.Stack;
import java.util.Random;

public class Pitfalls implements Obstacles 
{
    private Stack<Position> pitfallsList = new Stack<Position>();
    private Random randomSeed;


    public Pitfalls(Random randomSeed){
        this.randomSeed = randomSeed;
    }


	@Override
    public Stack<Position> getObstacles(){
        if(this.pitfallsList.size() < 1){
            int obstaclesNumber = this.randomSeed.nextInt(50)+1;
            int obstacleX;
            int obstacleY;
            Position obstacleCoordinate;
            for (int i=0; i<obstaclesNumber;i++)
            {
                obstacleX = this.randomSeed.nextInt(200)-100;
                obstacleY = this.randomSeed.nextInt(200)-100;
                obstacleCoordinate = new Position(obstacleX, obstacleY);
                this.pitfallsList.add(obstacleCoordinate);
            }
        }
        return this.pitfallsList;
	}


	@Override
	public boolean blocksPosition(Position position) {
        if(this.pitfallsList.contains(position))
            return true;
		return false;
	}

    
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