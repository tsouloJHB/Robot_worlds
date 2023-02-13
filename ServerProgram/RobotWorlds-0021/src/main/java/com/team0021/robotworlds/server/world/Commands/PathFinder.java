package com.team0021.robotworlds.server.world.Commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;

import javax.print.DocFlavor.STRING;

import com.team0021.robotworlds.server.world.Position;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

public class PathFinder {
    private int side;
    private int index;
    private String response;
    private String characterIdentifier;

    private Positions worldObject;
    
    private Stack<Position> visited = new Stack();
    
    private HashMap<String, String> characterResult;
    private HashMap<String, Object> characterState;
    private HashMap<String, Object> characterData;
    
    private Position characterPosition;
    private CharacterAbstract characterObject;


    public PathFinder(Positions worldObject){
        this.worldObject = worldObject;
        this.visited = new Stack();
        this.response = "";
    }


    private void setSide(String edge){
        switch(edge){
            case "up":{this.side = 200; break;}
            case "right":{this.side = 100; break;}
            case "left":{this.side = -100; break;}
            default:{this.side = -200; break;}
        }
    }


    private void setIndex(){
        switch(this.side){
            case 200:{this.index = 1; break;}
            case 100:{this.index = 0; break;}
            case -100:{this.index = 0; break;}
            default:{this.index = 1; break;}
        }
    }


  /**
     * Function used to create a string of commands needed to execute.
     * @param instruction Used to describe the command to execute
    */
    private void doCommand(String instruction){
        Command nextMove;
        nextMove = Command.create(instruction);
        nextMove.execute(this.characterObject);

        this.characterResult = characterObject.getCharacterResult();
        this.characterState = characterObject.getCharacterState();
        this.characterData = characterObject.getCharacterData();

        this.response += "\n[SONIC WORLD]"+this.characterObject.toString();
        // System.out.println(response);
    }


    /**
     * Used to determine how to change the direction based on current coordinate
     * and next coordinate for North facing objects
     * @param x1 X value of the current coordinate
     * @param y1 Y value of the current coordinate
     * @param x2 X value of the next coordinate
     * @param y2 Y value of the next coordinate
     */
    private void facingNorth(int x1, int y1, int x2, int y2)
    {
        if(x1 == x2){
            if(y1>y2)
            {
                doCommand("turn right");
                doCommand("turn right");
            }
        }
        else if(y1 == y2){
            if(x1>x2)
                doCommand("turn left");
            else if (x1<x2)
                doCommand("turn right");
        }    
    }



    /**
     * Used to determine how to change the direction based on current coordinate
     * and next coordinate for East facing objects
     * @param x1 X value of the current coordinate
     * @param y1 Y value of the current coordinate
     * @param x2 X value of the next coordinate
     * @param y2 Y value of the next coordinate
     */
    private void facingEast(int x1, int y1, int x2, int y2)
    {
        if(x1 == x2){
            if(y1>y2){
                doCommand("turn right");
            }
            else if (y1<y2){
                doCommand("turn left");
            }
        }     
        else if(y1 == y2){
            if(x1>x2){
                doCommand("turn right");
                doCommand("turn right");
            }
        }
    }


    /**
     * Used to determine how to change the direction based on current coordinate
     * and next coordinate for West facing objects
     * @param x1 X value of the current coordinate
     * @param y1 Y value of the current coordinate
     * @param x2 X value of the next coordinate
     * @param y2 Y value of the next coordinate
     */
    private void facingWest(int x1, int y1, int x2, int y2)
    {
        if(x1 == x2){

            if(y1>y2){
                doCommand("turn left");
            }
            else if (y1<y2){
                doCommand("turn right");
            }
            
        }  
        else if(y1 == y2){
            if (x1<x2){
                doCommand("turn left");
                doCommand("turn left");
            }
        }
    }


    /**
     * Used to determine how to change the direction based on current coordinate
     * and next coordinate for South facing objects
     * @param x1 X value of the current coordinate
     * @param y1 Y value of the current coordinate
     * @param x2 X value of the next coordinate
     * @param y2 Y value of the next coordinate
     */
    private void facingSouth(int x1, int y1, int x2, int y2)
    {
        if(x1 == x2){
            if (y1<y2){
                doCommand("turn right");
                doCommand("turn right");
            }
        }
        else if(y1 == y2){
            if(x1>x2)
                doCommand("turn right");
            else if (x1<x2)
                doCommand("turn left");
        }
        
    }



    /***
     * Function used to determine which function to call to change direction
     * using current coordinate and next coordinate
     * @param x1 X value of the current coordinate
     * @param y1 Y value of the current coordinate
     * @param x2 X value of the next coordinate
     * @param y2 Y value of the next coordinate
    */
    private void changeDirection(int x1, int y1, int x2, int y2){
        switch((String)this.characterState.get("direction")){
            case "NORTH":
                facingNorth(x1, y1,  x2, y2);
                break;
            case "EAST":
                facingEast(x1, y1, x2, y2);
                break;
            case "SOUTH":
                facingSouth(x1, y1, x2, y2);
                break;
            default:
                facingWest(x1, y1, x2, y2);
                break;
        }
    }


    /***
     * Function used to move the turtle based on a list of coordinates.
     * @param pathCoordinates Stack object used to list coordinates to use to  
     * move turtle
     * @return Boolean true if it was able to reach target, false on else
     */
    public boolean automateMovement()
    {
        int countForward = 0;
        int currentX;
        int currentY;
        int nextX;
        int nextY;
        int i = 0;
        
        Position nextCoordinate;
        Position currentCoordinate;

        Stack<Position> pathCoordinates = this.visited;

        Position goal = pathCoordinates.lastElement();
        pathCoordinates.add(new Position(1000,300));
        currentCoordinate = pathCoordinates.get(i);
        currentX = currentCoordinate.getX();
        currentY = currentCoordinate.getY();

        nextCoordinate = pathCoordinates.get(i+1);
        nextX = nextCoordinate.getX();
        nextY = nextCoordinate.getY();
        while (i<=pathCoordinates.size()-2)
        {
            changeDirection(currentX, currentY, nextX, nextY);
            
            while(currentX == nextX && i<=pathCoordinates.size()-2)
            {
                currentCoordinate = pathCoordinates.get(i);
                currentX = currentCoordinate.getX();
                currentY = currentCoordinate.getY();

                nextCoordinate = pathCoordinates.get(i+1);
                nextX = nextCoordinate.getX();
                nextY = nextCoordinate.getY();
                countForward += 1;
                if (countForward == 100)
                    {doCommand("Forward 100");countForward=0;}
                i+=1;
            }
            if (countForward > 0)
                {doCommand("Forward "+countForward);countForward=0;}
            
            changeDirection(currentX, currentY, nextX, nextY);
        
            while(currentY == nextY && i<=pathCoordinates.size()-2)
            {
                currentCoordinate = pathCoordinates.get(i);
                currentX = currentCoordinate.getX();
                currentY = currentCoordinate.getY();

                nextCoordinate = pathCoordinates.get(i+1);
                nextX = nextCoordinate.getX();
                nextY = nextCoordinate.getY();
                countForward += 1;
                if (countForward == 100)
                    {doCommand("Forward 100");countForward=0;}
                i+=1;
            }

            if (countForward > 0)
                {doCommand("Forward "+countForward);countForward=0;}
            
        }
        // doCommand("Forward 1");
        if (goal.equals(this.characterPosition))
        {
            return true;
        }
        return false;
    }


        /**
     * Used to filter deadends in the list of coordinates used to get a target.
     * @param targetPosition
     * @param index  
     * @param edge
     * @return A Stack object listing coordinates that can be used to get to 
     * target
    */
    private Stack<Position> carvePathOut(){
        int cx, cy, nx, ny;
        Stack<Position> move = new Stack();
        Stack<Position> filteredList = this.visited;

        move.add(filteredList.lastElement());
        
        while (filteredList.size()>1){
            cx = filteredList.lastElement().getX();
            cy = filteredList.lastElement().getY();
            filteredList.pop();
            nx = filteredList.lastElement().getX();
            ny = filteredList.lastElement().getY();
            if(cx==nx && Math.abs(cy-ny) == 1 || 
                    cy==ny && Math.abs(cx-nx) == 1 && 
                    !move.contains(filteredList.lastElement())){
                move.add(filteredList.lastElement());
            }
            else{
                while(filteredList.size()>1){
                    filteredList.pop();
                    nx = filteredList.lastElement().getX();
                    ny = filteredList.lastElement().getY();
                    if (cy==ny && Math.abs(cx-nx)== 1 || 
                            cx==nx && Math.abs(cy-ny) == 1 && 
                            !move.contains(filteredList.lastElement())){
                        move.add(filteredList.lastElement());
                        break;}
                }
            }
            if (this.characterPosition == filteredList.lastElement()){
                break;
            }
        }
        Collections.reverse(move);
        return move;
    }






        /***
     * Function used to prioritise y+1 coordinate when looking for neighbours.
     * @param newPosition Position object used to look for neighbours
     * @return List object listing point relative to newPosition coordinate
    */
    private Stack<Position> findNeighboursPrioritiseUp(Position newPosition){
        Stack<Position> returnNeighbours = new Stack();
        int x = newPosition.getX();
        int y = newPosition.getY();

        if (y-1 <= 200 && y-1 >= -200 && 
                !clearPath(new Position(x, y),new Position(x, y-1))){
            returnNeighbours.add(new Position(x, y-1));
        }       
        if (x-1 >= -100 && x-1 <= 100 && 
                !clearPath(new Position(x, y),new Position(x-1, y))){
            returnNeighbours.add(new Position(x-1, y));
        }
        if (x+1 >= -100 && x+1 <= 100 && 
                !clearPath(new Position(x, y),new Position(x+1, y))){
            returnNeighbours.add(new Position(x+1, y));
        }
        if (y+1 <= 200 && y+1 >= -200 && 
                !clearPath(new Position(x, y),new Position(x, y+1))){
            returnNeighbours.add(new Position(x, y+1));
        }
               
        return returnNeighbours;
    }


     /***
     * Function used to prioritise x+1 coordinate when looking for neighbours.
     * @param newPosition Position object used to look for neighbours
     * @return List object listing point relative to newPosition coordinate
    */
    private Stack<Position> findNeighboursPrioritiseRight(Position newPosition){
        Stack<Position> returnNeighbours = new Stack();
        int x = newPosition.getX();
        int y = newPosition.getY();

              
        if (x-1 >= -100 && x-1 <= 100 && 
                !clearPath(new Position(x, y),new Position(x-1, y))){
            returnNeighbours.add(new Position(x-1, y));
        }

        if (y+1 <= 200 && y+1 >= -200 && 
                !clearPath(new Position(x, y),new Position(x, y+1))){
            returnNeighbours.add(new Position(x, y+1));
        } 

        if (y-1 <= 200 && y-1 >= -200 && 
                !clearPath(new Position(x, y),new Position(x, y-1))){
            returnNeighbours.add(new Position(x, y-1));
        }

        if (x+1 >= -100 && x+1 <= 100 && 
                !clearPath(new Position(x, y),new Position(x+1, y))){
            returnNeighbours.add(new Position(x+1, y));
        }
               
        return returnNeighbours;
    }


    /***
     * Function used to prioritise x-1 coordinate when looking for neighbours.
     * @param newPosition Position object used to look for neighbours
     * @return List object listing point relative to newPosition coordinate
    */
    private Stack<Position> findNeighboursPrioritiseLeft(Position newPosition){
        Stack<Position> returnNeighbours = new Stack();
        int x = newPosition.getX();
        int y = newPosition.getY();

        if (x+1 >= -100 && x+1 <= 100 && 
                !clearPath(new Position(x, y),new Position(x+1, y))){
            returnNeighbours.add(new Position(x+1, y));
        }      
                
        if (y+1 <= 200 && y+1 >= -200 && 
                !clearPath(new Position(x, y),new Position(x, y+1))){
            returnNeighbours.add(new Position(x, y+1));
        } 

        if (y-1 <= 200 && y-1 >= -200 && 
                !clearPath(new Position(x, y),new Position(x, y-1))){
            returnNeighbours.add(new Position(x, y-1));
        }

        if (x-1 >= -100 && x-1 <= 100 && 
                !clearPath(new Position(x, y),new Position(x-1, y))){
            returnNeighbours.add(new Position(x-1, y));
        }
        
               
        return returnNeighbours;
    }


    /***
     * Function used to prioritise y-1 coordinate when looking for neighbours.
     * @param newPosition Position object used to look for neighbours
     * @return List object listing point relative to newPosition coordinate
    */
    private Stack<Position> findNeighboursPrioritiseDown(Position newPosition){
        Stack<Position> returnNeighbours = new Stack();
        int x = newPosition.getX();
        int y = newPosition.getY();

        if (y+1 <= 200 && y+1 >= -200 && 
                !clearPath(new Position(x, y),new Position(x, y+1))){
            returnNeighbours.add(new Position(x, y+1));
        }       
        if (x-1 >= -100 && x-1 <= 100 && 
                !clearPath(new Position(x, y),new Position(x-1, y))){
            returnNeighbours.add(new Position(x-1, y));
        }
        if (x+1 >= -100 && x+1 <= 100 && 
                !clearPath(new Position(x, y),new Position(x+1, y))){
            returnNeighbours.add(new Position(x+1, y));
        }
        if (y-1 <= 200 && y-1 >= -200 && 
                !clearPath(new Position(x, y),new Position(x, y-1))){
            returnNeighbours.add(new Position(x, y-1));
        }  
        return returnNeighbours;
    }



    private boolean clearPath(Position oldPosition, Position newPosition) {
        boolean condition = false;
        if(this.worldObject.isPathBlockedByAnObstacle(oldPosition, newPosition)){condition = true;}
        if(this.worldObject.isPathBlockedByApitfall(oldPosition, newPosition)){condition = true;}
        if(this.worldObject.isPositionBlockedByRobot(this.characterIdentifier, newPosition)){condition = true;}
        return condition;
    }


    private void findTarget(){
        Stack<Position> que = new Stack();
        Stack<Position> returnNeighboursList = new Stack();
        
        Position currentPos = this.characterPosition;
        
        que.add(currentPos);
        
        while(que.size()>0){
            
            if (this.side == 200)
                returnNeighboursList = findNeighboursPrioritiseUp(currentPos);
            else if (this.side  == 100)
                returnNeighboursList =findNeighboursPrioritiseRight(currentPos);
            else if (this.side  == -100)
                returnNeighboursList = findNeighboursPrioritiseLeft(currentPos);
            else if (this.side  == -200)
                returnNeighboursList = findNeighboursPrioritiseDown(currentPos);
            

            
            for (int i=0; i<returnNeighboursList.size();i++){
                if(!this.visited.contains(returnNeighboursList.get(i))){
                    que.add(returnNeighboursList.get(i));
                    this.visited.add(returnNeighboursList.get(i));    
                }
            }

            currentPos = que.pop();
            Stack<Integer> tempList = new Stack();
            tempList.add(currentPos.getX());
            tempList.add(currentPos.getY());
            if (tempList.get(index) == this.side){
                break;
            }
        }
    }



    public String doMazerun(CharacterAbstract characterObject, String edge){
        this.characterObject = characterObject;
        this.characterIdentifier =characterObject.getCharacterIdentifier();
        this.characterPosition = characterObject.getCharacterPosition();
        this.characterResult= characterObject.getCharacterResult();
        this.characterState = characterObject.getCharacterState();
        this.characterData = characterObject.getCharacterData();
        
        Stack<Integer> tempList = new Stack<Integer>();
        setSide(edge);
        setIndex();

        tempList.add(this.characterPosition.getX());
        tempList.add(this.characterPosition.getY());
        if (tempList.get(index) != side){
            findTarget();
            this.visited = carvePathOut();
            automateMovement();
        }
        return this.response += "\n[SONIC WORLD]: Glad to be of service.";
    }

}
