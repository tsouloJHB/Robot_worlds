package com.team0021.robotworlds.server.world;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

import com.team0021.robotworlds.server.world.Commands.UpdateResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.team0021.robotworlds.server.ServerHandler;


/**
 * Class provides implementation for the Positions class. The class is used to 
 * encapsulate the coordinates the robot(s) are occupying. It maps the robot's 
 * position to the robot name.
 * @author imogano
*/
public class Positions {
    
    private Random randomSeed;

    private int worldLength;
    private int worldwidth;
    private Position topLeft;
    private Position bottonRight;
    private Obstacles worldPitfalls;
    private HealthPack healthPacks;
    private Obstacles worldObstacles;
    private Position oldPosition;
    private Stack<MinePosition> minesList;
    private HashMap<Position, String> robotPositions;
    public HashMap<Position, CharacterAbstract> characterPositions = new HashMap<Position, CharacterAbstract>() ;
    

    HashMap<String, Stack<String>> robotsReport = new HashMap<String, Stack<String>>();
    HashMap<String, Stack<String>> obstacleReport = new HashMap<String, Stack<String>>();
    HashMap<String, Stack<String>> pitfalsReport = new HashMap<String, Stack<String>>();
    HashMap<String, Stack<String>> minesReport = new HashMap<String, Stack<String>>();
    HashMap<String, Stack<String>> healthPackReport = new HashMap<String, Stack<String>>();



    // Consideration
    private String robotName;
    private Direction robotDirection;

    private MinePosition removeMinePosition = null;
    
    /**
     * Enum used to track direction
     */
    enum Direction {
        NORTH, SOUTH, WEST, EAST
    }

    /**
     * Class constructor invoked using no parameters.
    */
    public Positions(){
        this.randomSeed = new Random();
        this.minesList = new Stack();
        this.worldPitfalls = new Pitfalls(this.randomSeed);
        this.worldObstacles = new SquareObstacles(this.randomSeed);
        this.healthPacks = new HealthPack(this.randomSeed,this);
        this.robotPositions = new HashMap<Position, String>();
        this.characterPositions = new HashMap<Position, CharacterAbstract>();

        readFromJson();
    }


    /**
     * Class constructor invoked using a Random class object.
     * @param Random object randomSeed used to provide an alternative to 
     * randomSeed.
    */
    public Positions(Random randomSeed){
        this.randomSeed = randomSeed;
        this.minesList = new Stack();
        this.worldPitfalls = new Pitfalls(this.randomSeed);
        this.worldObstacles = new SquareObstacles(this.randomSeed);
        this.robotPositions = new HashMap<Position, String>();
        this.characterPositions = new HashMap<Position, CharacterAbstract>();

        readFromJson();
    }


    
    public void launchCharacter(CharacterAbstract characterObject){
        Position newPosition = createNewPosition(characterObject.getCharacterIdentifier());
        characterObject.setCharacterPosition(newPosition);
        this.characterPositions.put(newPosition, characterObject);
        System.out.println("[WORLD]: Launched "+characterObject.getCharacterIdentifier()+" successfully.");
    }


    
    private void readFromJson()
    {
        // See the json file to makes sense of the comments below.

        JSONParser jsonParser = new JSONParser();
        String configDir = 
        ".PositionsConfig.json";
        
        try (FileReader reader = new FileReader(configDir))
        {
            JSONObject fileJson = (JSONObject) jsonParser.parse(reader);

            JSONObject setupJson = (JSONObject) fileJson.get("setup");

            //first inner scope "dimensions" under "setup" in the json file
            JSONObject dimensionsJson = (JSONObject) setupJson.get("dimensions");

            long longToIntA = (long) dimensionsJson.get("width");
            long longToIntB = (long) dimensionsJson.get("length");

            this.worldwidth = (int) longToIntA;
            this.worldLength = (int) longToIntB;

            JSONArray xAndy = (JSONArray) dimensionsJson.get("topLeft");
            long x = (long) xAndy.get(0);
            long y = (long) xAndy.get(1);

            this.topLeft  = new Position((int)x, (int)y);

            xAndy = (JSONArray) dimensionsJson.get("bottonRight");
            x = (long) xAndy.get(0);
            y = (long) xAndy.get(1);

            this.bottonRight = new Position((int)x, (int)y);
            
            this.robotDirection = Direction.NORTH;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



    public Position getworldTopLeftCorner(){
        return this.topLeft;
    }



    public Position getworldBottomRightCorner(){
        return this.bottonRight;
    }



    public int getworldWidth(){
        return this.worldwidth;
    }



    public int getworldLength(){
        return this.worldLength;
    }

    //Ignore the red. It will compile
    public HashMap<String, Object> getVisibilityReport(Position currentPosition)
    {
        HashMap<String, Object> visibilityReport = new HashMap<String, Object>();

        HashMap<String, Object> topLeftReport = new HashMap<String, Object>();
        HashMap<String, Object> topRightReport = new HashMap<String, Object>();
        HashMap<String, Object> bottomLeftReport = new HashMap<String, Object>();
        HashMap<String, Object> bottomRightReport = new HashMap<String, Object>();

        getRobotsInRange(currentPosition);

        getObstaclesInRange(currentPosition);

        getPitfallsInRange(currentPosition);

        getMinesInRange(currentPosition);

        getHealthPackInRange(currentPosition);

        for (String key : new String[] {"Top Right","Top Left","Bottom Right","Bottom Left"})  
        {
            if (key.equals("Top Right") && robotsReport.get("Top Right")!=null)
                topRightReport.put("Robots",robotsReport.get("Top Right"));

            else if (key.equals("Top Left") && robotsReport.get("Top Left")!=null)
                topLeftReport.put("Robots",robotsReport.get("Top Left"));

            else if (key.equals("Bottom Right") && robotsReport.get("Bottom Right")!=null)
                bottomLeftReport.put("Robots",robotsReport.get("Bottom Right"));

            else if (key.equals("Bottom Left") && robotsReport.get("Bottom Left")!=null)
                bottomRightReport.put("Robots",robotsReport.get("Bottom Left"));



            if (key.equals("Top Right") && obstacleReport.get("Top Right")!=null)
                topRightReport.put("Obstacles",obstacleReport.get("Top Right"));

            else if (key.equals("Top Left") && obstacleReport.get("Top Left")!=null)
                topLeftReport.put("Obstacles",obstacleReport.get("Top Left"));

            else if (key.equals("Bottom Right") && obstacleReport.get("Bottom Right")!=null)
                bottomLeftReport.put("Obstacles",obstacleReport.get("Bottom Right"));

            else if (key.equals("Bottom Left") && obstacleReport.get("Bottom Left")!=null)
                bottomRightReport.put("Obstacles",obstacleReport.get("Bottom Left"));



            if (key.equals("Top Right") && pitfalsReport.get("Top Right")!=null)
                topRightReport.put("Pitfalls",pitfalsReport.get("Top Right"));

            else if (key.equals("Top Left") && pitfalsReport.get("Top Left")!=null)
                topLeftReport.put("Pitfalls",pitfalsReport.get("Top Left"));

            else if (key.equals("Bottom Right") && pitfalsReport.get("Bottom Right")!=null)
                bottomLeftReport.put("Pitfalls",pitfalsReport.get("Bottom Right"));

            else if (key.equals("Bottom Left") && pitfalsReport.get("Bottom Left")!=null)
                bottomRightReport.put("Pitfalls",pitfalsReport.get("Bottom Left"));

            
            if (key.equals("Top Right") && minesReport.get("Top Right")!=null)
                topRightReport.put("Mines",minesReport.get("Top Right"));

            else if (key.equals("Top Left") && minesReport.get("Top Left")!=null)
                topLeftReport.put("Mines",minesReport.get("Top Left"));

            else if (key.equals("Bottom Right") && minesReport.get("Bottom Right")!=null)
                bottomLeftReport.put("Mines",minesReport.get("Bottom Right"));

            else if (key.equals("Bottom Left") && minesReport.get("Bottom Left")!=null)
                bottomRightReport.put("Mines",minesReport.get("Bottom Left"));


            if (key.equals("Top Right") && healthPackReport.get("Top Right")!=null)
                topRightReport.put("Health Pack",healthPackReport.get("Top Right"));

            else if (key.equals("Top Left") && healthPackReport.get("Top Left")!=null)
                topLeftReport.put("Health Pack",healthPackReport.get("Top Left"));

            else if (key.equals("Bottom Right") && healthPackReport.get("Bottom Right")!=null)
                bottomLeftReport.put("Health Pack",healthPackReport.get("Bottom Right"));

            else if (key.equals("Bottom Left") && healthPackReport.get("Bottom Left")!=null)
                bottomRightReport.put("Health Pack",healthPackReport.get("Bottom Left"));
        }   

        // visibilityReport.put("Top Left",topLeftReport);
        // visibilityReport.put("Top Right",topRightReport);
        // visibilityReport.put("Bottom Left",bottomLeftReport);
        // visibilityReport.put("Bottom Right",bottomRightReport);

        visibilityReport.put("Top Left",topRightReport);
        visibilityReport.put("Top Right",topLeftReport);
        visibilityReport.put("Bottom Left",bottomRightReport);
        visibilityReport.put("Bottom Right",bottomLeftReport);


        return visibilityReport;
    }


    public void setMineObjectToList(MinePosition newMine){
        this.minesList.add(newMine);
    }


    public Stack<Position>  getObstacleList(){
        return this.worldObstacles.getObstacles();
    }



    public Stack<Position>  getPitfallList(){
        return this.worldPitfalls.getObstacles();
    }


    public Stack<Position>  getHealthPackList(){
        return this.healthPacks.getObstacles();
    }



    public Set<Position> getRobotsPositions(){
        return this.robotPositions.keySet();
    }



    public HashMap<String, Stack<String>> getObstaclesInRange(Position currentPosition){
        int roboX = currentPosition.getX();
        int roboY = currentPosition.getY();

        Stack<String> obstaclesInRangeNorthWest = new Stack<String>();
        Stack<String> obstaclesInRangeNorthEast = new Stack<String>();
        Stack<String> obstaclesInRangeSouthWest = new Stack<String>();
        Stack<String> obstaclesInRangeSouthEast = new Stack<String>();

        for (Position obstacleObject: getObstacleList()){
            int x = obstacleObject.getX();
            int y = obstacleObject.getY();
            
            if(x<=roboX && x>=roboX-5 && y>=roboY && y<= roboY+5){
                obstaclesInRangeNorthWest.add("("+x+","+y+")");
            }
            if(x>=roboX && x<=roboX+5 && y>=roboY && y<= roboY+5){
                obstaclesInRangeNorthEast.add("("+x+","+y+")");
            }
            if(x>=roboX && x<=roboX+5 && y<=roboY && y>= roboY-5){
                obstaclesInRangeSouthEast.add("("+x+","+y+")");
            }
            if(x<=roboX && x>=roboX-5 && y<=roboY && y>= roboY-5){
                obstaclesInRangeSouthWest.add("("+x+","+y+")");
            }
        }
        
        obstacleReport.put("Top Right", obstaclesInRangeNorthWest);
        obstacleReport.put("Top Left", obstaclesInRangeNorthEast);
        obstacleReport.put("Bottom Right", obstaclesInRangeSouthWest);
        obstacleReport.put("Bottom Left", obstaclesInRangeSouthEast);
        
        return obstacleReport;
    }



    public HashMap<String, Stack<String>> getPitfallsInRange(Position currentPosition){
        int roboX = currentPosition.getX();
        int roboY = currentPosition.getY();

        Stack<String> pitFallsInRangeNorthWest = new Stack<String>();
        Stack<String> pitFallsInRangeNorthEast = new Stack<String>();
        Stack<String> pitFallsInRangeSouthWest = new Stack<String>();
        Stack<String> pitFallsInRangeSouthEast = new Stack<String>();

        for (Position pitfallObject: getPitfallList()){
            int x = pitfallObject.getX();
            int y = pitfallObject.getY();

            if(x<=roboX && x>=roboX-5 && y>=roboY && y<= roboY+5){
                pitFallsInRangeNorthWest.add("("+x+","+y+")");
            }
            if(x>=roboX && x<=roboX+5 && y>=roboY && y<= roboY+5){
                pitFallsInRangeNorthEast.add("("+x+","+y+")");
            }
            if(x>=roboX && x<=roboX+5 && y<=roboY && y>= roboY-5){
                pitFallsInRangeSouthEast.add("("+x+","+y+")");
            }
            if(x<=roboX && x>=roboX-5 && y<=roboY && y>= roboY-5){
                pitFallsInRangeSouthWest.add("("+x+","+y+")");
            }
        }

        pitfalsReport.put("Top Right", pitFallsInRangeNorthWest);
        pitfalsReport.put("Top Left", pitFallsInRangeNorthEast);
        pitfalsReport.put("Bottom Right", pitFallsInRangeSouthWest);
        pitfalsReport.put("Bottom Left", pitFallsInRangeSouthEast);

        return pitfalsReport;
    }



    public HashMap<String, Stack<String>> getRobotsInRange(Position currentPosition){
        int roboX = currentPosition.getX();
        int roboY = currentPosition.getY();

        Stack<String> robotsInRangeNorthWest = new Stack<String>();
        Stack<String> robotsInRangeNorthEast = new Stack<String>();
        Stack<String> robotsInRangeSouthWest = new Stack<String>();
        Stack<String> robotsInRangeSouthEast = new Stack<String>();

        for (Position robotlObject: getRobotPositions()){
            int x = robotlObject.getX();
            int y = robotlObject.getY();

            if (roboX == x && roboY == y){
                continue;
            }


            if(x<=roboX && x>=roboX-5 && y>=roboY && y<= roboY+5){

                robotsInRangeNorthWest.add("("+x+","+y+")");
            }
            if(x>=roboX && x<=roboX+5 && y>=roboY && y<= roboY+5){
                
                robotsInRangeNorthEast.add("("+x+","+y+")");
            }
            if(x>=roboX && x<=roboX+5 && y<=roboY && y>= roboY-5){
                robotsInRangeSouthEast.add("("+x+","+y+")");
            }
            if(x<=roboX && x>=roboX-5 && y<=roboY && y>= roboY-5){
                robotsInRangeSouthWest.add("("+x+","+y+")");
            }

        }

        robotsReport.put("Top Right", robotsInRangeNorthWest);
        robotsReport.put("Top Left", robotsInRangeNorthEast);
        robotsReport.put("Bottom Right", robotsInRangeSouthWest);
        robotsReport.put("Bottom Left", robotsInRangeSouthEast);

        return robotsReport;
    }

    public Stack<MinePosition> getMinesList(){
        return this.minesList;
    }


    public HashMap<String, Stack<String>> getMinesInRange(Position currentPosition){
        int roboX = currentPosition.getX();
        int roboY = currentPosition.getY();

        Stack<String> robotsInRangeNorthWest = new Stack<String>();
        Stack<String> robotsInRangeNorthEast = new Stack<String>();
        Stack<String> robotsInRangeSouthWest = new Stack<String>();
        Stack<String> robotsInRangeSouthEast = new Stack<String>();

        for (MinePosition robotlObject: getMinesList()){
            int x = robotlObject.getX();
            int y = robotlObject.getY();

            if (roboX == x && roboY == y){
                continue;
            }


            if(x<=roboX && x>=roboX-5 && y>=roboY && y<= roboY+5){

                robotsInRangeNorthWest.add("("+x+","+y+")");
            }
            if(x>=roboX && x<=roboX+5 && y>=roboY && y<= roboY+5){
                
                robotsInRangeNorthEast.add("("+x+","+y+")");
            }
            if(x>=roboX && x<=roboX+5 && y<=roboY && y>= roboY-5){
                robotsInRangeSouthEast.add("("+x+","+y+")");
            }
            if(x<=roboX && x>=roboX-5 && y<=roboY && y>= roboY-5){
                robotsInRangeSouthWest.add("("+x+","+y+")");
            }

        }

        robotsReport.put("Top Right", robotsInRangeNorthWest);
        robotsReport.put("Top Left", robotsInRangeNorthEast);
        robotsReport.put("Bottom Right", robotsInRangeSouthWest);
        robotsReport.put("Bottom Left", robotsInRangeSouthEast);

        return robotsReport;
    }


    public HashMap<String, Stack<String>> getHealthPackInRange(Position currentPosition){
        int roboX = currentPosition.getX();
        int roboY = currentPosition.getY();

        Stack<String> robotsInRangeNorthWest = new Stack<String>();
        Stack<String> robotsInRangeNorthEast = new Stack<String>();
        Stack<String> robotsInRangeSouthWest = new Stack<String>();
        Stack<String> robotsInRangeSouthEast = new Stack<String>();

        for (Position robotlObject: this.healthPacks.getHealthPackList()){
            int x = robotlObject.getX();
            int y = robotlObject.getY();

            if (roboX == x && roboY == y){
                continue;
            }


            if(x<=roboX && x>=roboX-5 && y>=roboY && y<= roboY+5){

                robotsInRangeNorthWest.add("("+x+","+y+")");
            }
            if(x>=roboX && x<=roboX+5 && y>=roboY && y<= roboY+5){
                
                robotsInRangeNorthEast.add("("+x+","+y+")");
            }
            if(x>=roboX && x<=roboX+5 && y<=roboY && y>= roboY-5){
                robotsInRangeSouthEast.add("("+x+","+y+")");
            }
            if(x<=roboX && x>=roboX-5 && y<=roboY && y>= roboY-5){
                robotsInRangeSouthWest.add("("+x+","+y+")");
            }

        }

        robotsReport.put("Top Right", robotsInRangeNorthWest);
        robotsReport.put("Top Left", robotsInRangeNorthEast);
        robotsReport.put("Bottom Right", robotsInRangeSouthWest);
        robotsReport.put("Bottom Left", robotsInRangeSouthEast);

        return robotsReport;
    }


    /**
     * Function used to continually find a random x and y coordinate to place 
     * the robot. It will typically be invoked when a robot is launched into the
     * world.
     * @param robotName used to map a coordinate to a the name of the robot.
    */
    private Position createNewPosition(String characterIdentifier){
        int x = this.randomSeed.nextInt(200)-100;
        int y = this.randomSeed.nextInt(400)-200;
        Position newPosition =  new Position(x, y);

        while(!setPosition(characterIdentifier, newPosition)){
            x = this.randomSeed.nextInt(200)-100;
            y = this.randomSeed.nextInt(400)-200;

            while(x==0 && y==0){
                x = this.randomSeed.nextInt(200)-100;
                y = this.randomSeed.nextInt(400)-200;
            }
            removeMinePosition = null;
            newPosition =  new Position(x, y);
        }
        return newPosition;
    }
    

    
    public void terminatePosition(CharacterAbstract characterObject){
        String characterIdentifier = characterObject.getCharacterIdentifier();
        if(!this.robotPositions.isEmpty())
        {
            this.robotPositions.forEach(
                (key, value) -> 
                {
                    if (value.equals(characterIdentifier))
                        this.oldPosition = key;
                });
            this.robotPositions.remove(this.oldPosition);
            this.characterPositions.remove(this.oldPosition);
        }
    }


    /**
     * Functions used to map a coordinate to a robot name.
     * @param newPosition The new position we will be assigning to the robot.
     * @param robotName Name used to refer to a robot instance.
     * @return boolean object; true if position was not previously occupied and 
     * false if position is currently occupied by a robot.
    */
    private boolean setPosition(String characterIdentifier, Position newPosition){

        if(!this.robotPositions.isEmpty()&& 
                isPositionBlockedByRobot(characterIdentifier,newPosition) ||
                isPositionBlockedByApitfall(newPosition) || 
                isPositionBlockedByAmine(newPosition)||
                isPositionBlockedByAnObstacle(newPosition))
            return false;

        if(!this.robotPositions.isEmpty() && 
                this.robotPositions.values().contains(characterIdentifier))
                {
                    this.robotPositions.forEach(
                        (key, value) -> 
                        {
                            if (value.equals(characterIdentifier)){
                                this.oldPosition = key;
                            }
                        });
                    this.robotPositions.remove(this.oldPosition);
                }

        this.robotPositions.put(newPosition, characterIdentifier);

        return true;    
    }


    private boolean setCharacterPosition(CharacterAbstract characterObject, Position newPosition){
        
        if(!this.characterPositions.isEmpty() && 
                this.characterPositions.values().contains(characterObject))
            {
                this.characterPositions.forEach(
                    (key, value) -> 
                    {
                        if (value.equals(characterObject))
                            this.oldPosition = key;
                    });
                this.characterPositions.remove(this.oldPosition);
            }
        this.characterPositions.put(newPosition, characterObject);
        return true;    
    }


    public HashMap<Position, CharacterAbstract> getCharacterPositions(){
        return this.characterPositions;
    }


    /**
     * Function invoked using no parameters. It is used to return the coordinates
     * the robot(s) are currently occupying.
     * @return a set of coordinates that are currently occupied by Robots.
    */
    public Set<Position> getRobotPositions(){
        return robotPositions.keySet();
    }



    /**
     * Functions used check whether a position is blocked by an obstacle.
     * @param newPosition The new position we will be using to check.
     * @return boolean object; true if position is occupied by an obstacle 
     * false if position is not occupied by an obstacle.
    */
    public boolean isPositionBlockedByAnObstacle(Position newPosition){
        return this.worldObstacles.blocksPosition(newPosition);
    }


    public boolean isPositionBlockedByAmine(Position newPosition){
        for(MinePosition mineObj:this.minesList){
            if(mineObj.isPositionBlockedByMine(newPosition)){
                return true;
            }
        }
        return false;
    }
    

    public boolean isPathBlockedByAmine(Position oldPosition, Position newPosition){
        for(MinePosition mineObj:this.minesList){
            if(mineObj.isPathBlockedByMine(oldPosition,newPosition)){
                this.removeMinePosition = mineObj;
                return true;
            }
        }
        return false;
    }

    


    public boolean isPathBlockedByAnObstacle(Position oldPosition, Position newPosition){
        return this.worldObstacles.blocksPath(oldPosition, newPosition);
    }


    public boolean isPositionBlockedByApitfall(Position newPosition){
        return this.worldPitfalls.blocksPosition(newPosition);
    }


    public boolean isPathBlockedByApitfall(Position oldPosition, Position newPosition){
        return this.worldPitfalls.blocksPath(oldPosition, newPosition);
    }


    public boolean isPositionBlockedByAHealthPacks(Position newPosition){
        return this.healthPacks.blocksPosition(newPosition);
    }


    public boolean isPathBlockedByAHealthPacks(Position oldPosition, Position newPosition){
        return this.healthPacks.blocksPath(oldPosition, newPosition);
    }

    /**
     * Functions used check whether a position is blocked by a robot.
     * @param newPosition The new position we will be using to check.
     * @return boolean object; true if position is occupied by a robot 
     * false if position is not occupied by a robot.
    */
    public boolean isPositionBlockedByRobot(String charId, Position newPosition){
        int x1 = newPosition.getX();
        int y1 = newPosition.getY();
        Set<Position> positionSet = characterPositions.keySet();

        for(Position key:positionSet) {
            int x2 = key.getX();
            int y2 = key.getY();
            if(x1 == x2 && y1==y2 && !charId.equals(characterPositions.get(key).getCharacterIdentifier())){
                return true;
            }
        }
        return false;  
    }

    
    public boolean isPathBlockedByRobot(Position oldPosition, Position newPosition){
        int currentX = oldPosition.getX();
        int currentY = oldPosition.getY();
        int finalX = newPosition.getX();
        int finalY = newPosition.getY();
        String charId = "";

        if(this.characterPositions.containsKey(oldPosition)){
            charId = this.characterPositions.get(oldPosition).getCharacterIdentifier(); 
        }

        int minValue, maxValue;

        if(currentX == finalX){
            minValue = Math.min(currentY, finalY);
            maxValue = Math.max(currentY, finalY);
            Position tempPosition;
            
            for (int i=minValue; i<=maxValue;i++){
                
                tempPosition = new Position(currentX, i);
                if(isPositionBlockedByRobot(charId, tempPosition)){
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
                if(isPositionBlockedByRobot(charId,tempPosition)){
                    return true;
                }
            }
        }

        return false;
    }    


    public boolean getCheckPath(Position oldPosition, Position newPosition){
        if(isPathBlockedByApitfall(oldPosition, newPosition) || 
        isPathBlockedByAnObstacle(oldPosition, newPosition) ||
        isPathBlockedByAmine(oldPosition, newPosition) ||
        isPathBlockedByRobot(oldPosition, newPosition))
            return true;
        return false;
    }


    public UpdateResponse updatePosition(CharacterAbstract characterObject, int nrSteps) {
        HashMap<String, Object> state = characterObject.getCharacterState();

        String currentDirection = (String) state.get("direction");

        Position characterPosition = characterObject.getCharacterPosition();
       
        String characterIdentifier = characterObject.getCharacterIdentifier();
        String teamName = characterIdentifier.substring(0,characterIdentifier.indexOf("."));
        String role = characterIdentifier.replace(teamName,"").replace(".", "").trim();

        int shields = (int)state.get("shields");    
        int newX = characterPosition.getX();
        int newY = characterPosition.getY();
        
        if ("NORTH".equals(currentDirection)) {
            newY = newY + nrSteps;
        }
        else if ("EAST".equals(currentDirection)) {
            newX = newX + nrSteps;
        }
        else if ("SOUTH".equals(currentDirection)) {
            newY = newY - nrSteps;
        }
        else {
            newX = newX - nrSteps;
        }

        Position newPosition = new Position(newX, newY);

        if(!newPosition.isIn(topLeft, bottonRight)){
            return UpdateResponse.FAILED_OUTSIDE_WORLD;
        }

        if (isPathBlockedByAnObstacle(characterPosition,newPosition)){
            return UpdateResponse.FAILED_OBSTRUCTED;
        }
        
        if(isPathBlockedByAmine(characterPosition,newPosition)){
            if(shields<=3)
            {
                terminatePosition(characterObject);
                state.put("status", "DIED");
                characterObject.setCharacterState(state);
                ServerHandler.killClientThread(role+"."+teamName);
            }else{
                state.put("shields", shields-3);
            }
            if(this.removeMinePosition!=null){
                this.minesList.remove(this.removeMinePosition);
                removeMinePosition = null;
            }
            setPosition(characterIdentifier,newPosition);
            setCharacterPosition(characterObject,newPosition);
            characterObject.setCharacterPosition(newPosition);
            return UpdateResponse.FAILED_MINE;
        } 

        if (isPathBlockedByApitfall(characterPosition,newPosition)){
            terminatePosition(characterObject);
            state.put("status", "DIED");
            characterObject.setCharacterState(state);
            ServerHandler.killClientThread(role+"."+teamName);
            return  UpdateResponse.FAILED_PITFALL;
        }
        if (isPathBlockedByRobot(characterPosition,newPosition)){
            return UpdateResponse.FAILED_ROBOT;
        }
        if(isPathBlockedByAHealthPacks(characterPosition, newPosition)){
            state.put("shields", 5);
            characterObject.setCharacterState(state);
            setPosition(characterIdentifier,newPosition);
            setCharacterPosition(characterObject,newPosition);
            this.healthPacks.removeHealthPack(newPosition);
            characterObject.setCharacterPosition(newPosition);
            return UpdateResponse.HEALTH_PACK;
        }
        setPosition(characterIdentifier,newPosition);
        setCharacterPosition(characterObject,newPosition);
        characterObject.setCharacterPosition(newPosition);
        return UpdateResponse.SUCCESS;
    }

    public void updateDirection(CharacterAbstract characterObject, boolean turnRight) {
        HashMap<String, Object> state = characterObject.getCharacterState();
        
        String currentDirection = (String) state.get("direction");

        switch (currentDirection.toUpperCase().trim())
        {
            case "NORTH":
            {
                if(turnRight)
                {
                    currentDirection = "EAST";
                }
                else
                {
                    currentDirection = "WEST";
                }
                break;
            }
            case "EAST":
            {
                if(turnRight)
                {
                    currentDirection = "SOUTH";
                }
                else
                {
                    currentDirection = "NORTH";
                }
                break;
            }
            case "WEST":
            {
                if(turnRight)
                {
                    currentDirection = "NORTH";
                }
                else
                {
                    currentDirection = "SOUTH";
                }
                break;
            }
            default:
            {
                if(turnRight)
                {
                    currentDirection = "WEST";
                }
                else
                {
                    currentDirection = "EAST";
                }
                break;
            }
        }
        state.put("direction", currentDirection);
        characterObject.setCharacterState(state);
    }


    public String getVisibilityNorth(Position currentPosition) {
        String visibilityNorth = "North\n";
        int x1 = currentPosition.getX();
        int y1 = currentPosition.getY();
        
        for(Position obs:getObstacleList()){
            int x2 = obs.getX();
            int y2 = obs.getY();
            if(x1 == x2 && y2>y1 && y2<=y1+5){
                if(!visibilityNorth.contains("Obstacle(s)")){
                    visibilityNorth += "Obstacle(s)\n";
                }
                visibilityNorth+=" -> at ("+x2+","+y2+") to ("+(x2+4)+","+(y2+4)+")\n";
            }
        }

        for(Position pit:getPitfallList()){
            int x2 = pit.getX();
            int y2 = pit.getY();
            if(x1 == x2 && y2>y1 && y2<=y1+5){
                if(!visibilityNorth.contains("Pitfall(s)")){
                    visibilityNorth += "Pitfall(s)\n";
                }
                visibilityNorth+="("+x2+","+y2+")\n";
            }
        }

        for(Position pit:getRobotPositions()){
            int x2 = pit.getX();
            int y2 = pit.getY();
            if(x1 == x2 && y2>y1 && y2<=y1+5){
                if(!visibilityNorth.contains("Robot(s)")){
                    visibilityNorth += "Robot(s)\n";
                }
                visibilityNorth+=" -> ("+x2+","+y2+")\n";
            }
        }

        for(MinePosition mine:getMinesList()){
            int x2 = mine.getX();
            int y2 = mine.getY();
            if(x1 == x2 && y2>y1 && y2<=y1+5){
                if(!visibilityNorth.contains("Mine(s)")){
                    visibilityNorth += "Mine(s)\n";
                }
                visibilityNorth+=" -> ("+x2+","+y2+")\n";
            }
        }

        for(Position healthPack:this.healthPacks.getHealthPackList()){
            int x2 = healthPack.getX();
            int y2 = healthPack.getY();
            if(x1 == x2 && y2>y1 && y2<=y1+5){
                if(!visibilityNorth.contains("Health Pack(s)")){
                    visibilityNorth += "Health Pack(s)\n";
                }
                visibilityNorth+=" -> ("+x2+","+y2+")\n";
            }
        }

        if (visibilityNorth.split("\n").length == 1){
            visibilityNorth="";
        }
        return visibilityNorth;
    }

    public String getVisibilityEast(Position currentPosition) {
        String visibilityEast = "East\n";
        int x1 = currentPosition.getX();
        int y1 = currentPosition.getY();
        
        for(Position obs:getObstacleList()){
            int x2 = obs.getX();
            int y2 = obs.getY();
            if(y1 == y2 && x2>x1 && x2<=x1+5){
                if(!visibilityEast.contains("Obstacles")){
                    visibilityEast += "Obstacle(s)\n";
                }
                visibilityEast+=" -> at ("+x2+","+y2+") to ("+(x2+4)+","+(y2+4)+")\n";
            }
        }

        for(Position pit:getPitfallList()){
            int x2 = pit.getX();
            int y2 = pit.getY();
            if(y1 == y2 && x2>x1 && x2<=x1+5){
                if(!visibilityEast.contains("Pitfalls")){
                    visibilityEast += "Pitfall(s)\n";
                }
                visibilityEast+=" -> at ("+x2+","+y2+")\n";
            }
        }

        for(Position pit:getRobotPositions()){
            int x2 = pit.getX();
            int y2 = pit.getY();
            if(y1 == y2 && x2>x1 && x2<=x1+5){
                if(!visibilityEast.contains("Robot(s)")){
                    visibilityEast += "Robot(s)\n";
                }
                visibilityEast+=" -> at ("+x2+","+y2+")\n";
            }
        }

        for(MinePosition mine: getMinesList()){
            int x2 = mine.getX();
            int y2 = mine.getY();
            if(y1 == y2 && x2>x1 && x2<=x1+5){
                if(!visibilityEast.contains("Mine(s)")){
                    visibilityEast += "Mine(s)\n";
                }
                visibilityEast+=" -> at ("+x2+","+y2+")\n";
            }
        }

        for(Position healthPack:this.healthPacks.getHealthPackList()){
            int x2 = healthPack.getX();
            int y2 = healthPack.getY();
            if(y1 == y2 && x2>x1 && x2<=x1+5){
                if(!visibilityEast.contains("Health Pack(s)")){
                    visibilityEast += "Health Pack(s)\n";
                }
                visibilityEast+=" -> ("+x2+","+y2+")\n";
            }
        }

        if (visibilityEast.split("\n").length == 1){
            visibilityEast="";
        }

        return visibilityEast;
    }

    public String getVisibilitySouth(Position currentPosition) {
        String visibilitySouth = "South\n";
        int x1 = currentPosition.getX();
        int y1 = currentPosition.getY();
        
        for(Position obs:getObstacleList()){
            int x2 = obs.getX();
            int y2 = obs.getY();
            if(x1 == x2 && y2<y1 && y2>=y1-5){
                if(!visibilitySouth.contains("Obstacles")){
                    visibilitySouth += "Obstacle(s)\n";
                }
                visibilitySouth+=" -> at ("+x2+","+y2+") to ("+(x2+4)+","+(y2+4)+")\n";
            }
        }

        for(Position pit:getPitfallList()){
            int x2 = pit.getX();
            int y2 = pit.getY();
            if(x1 == x2 && y2<y1 && y2>=y1-5){
                if(!visibilitySouth.contains("Pitfalls")){
                    visibilitySouth+= "Pitfall(s)\n";
                }

                visibilitySouth+=" -> at ("+x2+","+y2+")\n";
            }
        }

        for(Position pit:getRobotPositions()){
            int x2 = pit.getX();
            int y2 = pit.getY();
            if(x1 == x2 && y2<y1 && y2>=y1-5){
                if(!visibilitySouth.contains("Robot(s)")){
                    visibilitySouth += "Robot(s)\n";
                }

                visibilitySouth+=" -> at ("+x2+","+y2+")\n";
            }
        }

        for(MinePosition mine: getMinesList()){
            int x2 = mine.getX();
            int y2 = mine.getY();
            if(x1 == x2 && y2<y1 && y2>=y1-5){
                if(!visibilitySouth.contains("Mine(s)")){
                    visibilitySouth += "Mine(s)\n";
                }
                visibilitySouth+=" -> at ("+x2+","+y2+")\n";
            }
        }

        for(Position healthPack:this.healthPacks.getHealthPackList()){
            int x2 = healthPack.getX();
            int y2 = healthPack.getY();
            if(x1 == x2 && y2<y1 && y2>=y1-5){
                if(!visibilitySouth.contains("Health Pack(s)")){
                    visibilitySouth += "Health Pack(s)\n";
                }
                visibilitySouth+=" -> ("+x2+","+y2+")\n";
            }
        }

        if (visibilitySouth.split("\n").length == 1){
            visibilitySouth="";
        }
        return visibilitySouth;
    }

    public boolean getPlaceHealthPack(){
        return this.healthPacks.placeHealthPacks();
    }


    public String getVisibilityWest(Position currentPosition) {
        String visibilityWest = "West\n";
        int x1 = currentPosition.getX();
        int y1 = currentPosition.getY();
        
        for(Position obs:getObstacleList()){
            int x2 = obs.getX();
            int y2 = obs.getY();
            if(y1 == y2 && x2<x1 && x2>=x1-5){
                if(!visibilityWest.contains("Obstacles")){
                    visibilityWest += "Obstacle(s)\n";
                }
                visibilityWest+=" -> at ("+x2+","+y2+") to ("+(x2+4)+","+(y2+4)+")\n";
            }
        }

        for(Position pit:getPitfallList()){
            int x2 = pit.getX();
            int y2 = pit.getY();
            if(y1 == y2 && x2<x1 && x2>=x1-5){
                if(!visibilityWest.contains("Pitfalls")){
                    visibilityWest+= "Pitfall(s)\n";
                }
                visibilityWest+=" -> at ("+x2+","+y2+")\n";
            }
        }

        for(Position pit:getRobotPositions()){
            int x2 = pit.getX();
            int y2 = pit.getY();
            if(y1 == y2 && x2<x1 && x2>=x1-5){
                if(!visibilityWest.contains("Robot(s)")){
                    visibilityWest += "Robot(s)\n";
                }
                visibilityWest+=" -> at ("+x2+","+y2+")\n";
            }
        }

        for(MinePosition mine:getMinesList()){
            int x2 = mine.getX();
            int y2 = mine.getY();
            if(y1 == y2 && x2<x1 && x2>=x1-5){
                if(!visibilityWest.contains("Mine(s)")){
                    visibilityWest += "Mine(s)\n";
                }
                visibilityWest+=" -> at ("+x2+","+y2+")\n";
            }
        }

        for(Position healthPack:this.healthPacks.getHealthPackList()){
            int x2 = healthPack.getX();
            int y2 = healthPack.getY();
            if(x1 == x2 && y2<y1 && y2>=y1-5){
                if(!visibilityWest.contains("Health Pack(s)")){
                    visibilityWest += "Health Pack(s)\n";
                }
                visibilityWest+=" -> ("+x2+","+y2+")\n";
            }
        }

        if (visibilityWest.split("\n").length == 1){
            visibilityWest="";
        }
        return visibilityWest;
    }

}