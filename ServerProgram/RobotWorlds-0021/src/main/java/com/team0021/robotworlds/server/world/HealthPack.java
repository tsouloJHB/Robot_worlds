package com.team0021.robotworlds.server.world;

import java.util.Stack;
import com.team0021.robotworlds.ForColor;
import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

import java.util.Random;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

public class HealthPack {
    
    private Stack<Position> healthPacksList = new Stack<Position>();
    private Random randomSeed;
    private Positions positions;
    private int healthNum = 4;
    public HashMap<Integer,Boolean> dropTimes = new HashMap<Integer,Boolean>() ;

    public HealthPack(Random randomSeed,Positions position){
        this.randomSeed = randomSeed;
        this.positions = position;
        for(int x = 1;x< healthNum;x++){
            this.dropTimes.put(x, true);
        }
    }

    public boolean placeHealthPacks(){
        int average  = 0;
        int sum = 0;
        int count = 0;
        for (CharacterAbstract robots: positions.characterPositions.values()){
            sum = sum + (int)robots.getCharacterState().get("shields");
            count = count+1;
        }
        average = sum/count;
        for(int drop:dropTimes.keySet()){  
            if(average == drop && dropTimes.get(drop) == true){
                dropTimes.put(drop, false);
                return true;
            }
        }
       
        return false;
    }

    public boolean checkObjectsPosition(Position newPosition){
        if(positions.isPositionBlockedByApitfall(newPosition) ||
           positions.isPositionBlockedByAnObstacle(newPosition)||
           positions.isPositionBlockedByAmine(newPosition)||
           isPositionBlockedByRobot(newPosition)){
           return false; 
        }
        return true;
    }

    public boolean isPositionBlockedByRobot(Position newPosition){
        int x1 = newPosition.getX();
        int y1 = newPosition.getY();
        Set<Position> positionSet = positions.characterPositions.keySet();

        for(Position key:positionSet) {
            int x2 = key.getX();
            int y2 = key.getY();
            if(x1 == x2 && y1==y2){
                return true;
            }
        }
        return false;  
    }

    public Stack<Position> getObstacles(){
       
       
        
        int obstacleX;
        int obstacleY;
        Position obstacleCoordinate;
        obstacleX = this.randomSeed.nextInt(200)-100;
        obstacleY = this.randomSeed.nextInt(200)-100;
        obstacleCoordinate = new Position(obstacleX, obstacleY);

       
        while(checkObjectsPosition(obstacleCoordinate) == false){
            obstacleX = this.randomSeed.nextInt(200)-100;
            obstacleY = this.randomSeed.nextInt(200)-100;
            obstacleCoordinate = new Position(obstacleX, obstacleY); 
        }
       
        this.healthPacksList.add(obstacleCoordinate);
        String messageToTeam = ForColor.BoldAndWhite.format("[INCOMMING]") + ForColor.BoldAndRed.format("[ALERT] > ")+
        ForColor.BoldAndYellow.format("A health Pack has just been dropped at ")+
        ForColor.BoldAndGreen.format("("+ obstacleX+", "+obstacleY+")")+
        ForColor.BoldAndYellow.format(" You better hurry! It won't be there for long.");
        alertMessage(messageToTeam);
      
        return this.healthPacksList;
    }

    public void removeHealthPack(Position position){
        if(this.healthPacksList.contains(position))
            
            healthPacksList.remove(position);
               
    }

	
	public boolean blocksPosition(Position position) {
        if(this.healthPacksList.contains(position))
            return true;
		return false;
	}
    


    public Stack<Position> getHealthPackList() {
        return this.healthPacksList;
    }

 

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

    private void alertMessage(String messageToAll){
        CharacterAbstract getTarget = null;
        
        HashMap<Position, CharacterAbstract> robotPositions = ServerHandler.getWorldObject().getCharacterPositions();
        for(Map.Entry<Position, CharacterAbstract> robot : robotPositions.entrySet()){
            getTarget = robot.getValue();
                ServerHandler.directCommunicationToUser(getTarget, messageToAll);
        }
    }
}
