package com.team0021.robotworlds.server.world.Commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.swing.text.AttributeSet.CharacterAttribute;

import com.team0021.robotworlds.ForColor;
import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Position;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;


/**
 * Class extends the Command class. Provides implementation for the fire command.
 * @author imogano, tsoulo
*/
public class FireCommand extends Command {

    private Positions worldObject;
    private HashMap<String, Object> state;
    private CharacterAbstract characterObject;    
    public FireCommand() {
        super("fire");
    }  

    /**
     * Function used to provide implementation for the shoot function.
     * It's setup is similiar to that of update position.
     * We take the character positon and increment the position with the value 
     * stored in the distance field, then check if the path is blocked by anything.
     * If it is, then the appropriate steps are followed.
     * @param The charcter object of the person using the fire command
     * @return Boolean object (true) to enable the player to contiunue playing the game
     * 
    */
    @Override
    public boolean execute(CharacterAbstract characterObject) {

        this.characterObject = characterObject;
        this.state = characterObject.getCharacterState();
        this.worldObject = ServerHandler.getWorldObject();

        String currentDirection = (String) this.state.get("direction");

        int distance = (int)characterObject.getCharacterData().get("distance");
        Position characterPosition = characterObject.getCharacterPosition();
       
        String shotsValue = String.valueOf(characterObject.getCharacterState().get("shots"));
        int shots = Integer.parseInt(shotsValue);
    
        int newX = characterPosition.getX();
        int newY = characterPosition.getY();
        
        
        if ("NORTH".equals(currentDirection)) {
            newY = newY + distance;
        }
        else if ("EAST".equals(currentDirection)) {
            newX = newX + distance;
        }
        else if ("SOUTH".equals(currentDirection)) {
            newY = newY - distance;
        }
        else {
            newX = newX - distance;
        }

        Position newPosition = new Position(newX, newY);
        if ( shots < 1 ){
            characterObject.setMessage(ForColor.BoldAndRed.format("You're out of ammo! Please reload.")); 
        }
        else{
            String messageToAll = 
            ForColor.BoldAndGreen.format("HOLY PRETZEL!!!! Shots are being fired at: ")+
            ForColor.BoldAndYellow.format("("+newX+", "+newY+")")+ 
            ForColor.BoldAndGreen.format(". Go check it out! They won't be expecting you!");
            shotAlerts(characterObject, messageToAll);

            characterObject.getCharacterState().put("shots", shots-1);

            if (worldObject.isPathBlockedByAnObstacle(characterPosition, newPosition)){
                characterObject.setMessage("You hit a wall."); 
            }
    
            else if (worldObject.isPathBlockedByRobot(characterPosition, newPosition)){
                shotRobot(characterPosition);
            }

            else{
                characterObject.setMessage(ForColor.BoldAndRed.format("You missed your target.")); 
            }
        }
       
        return true;
        
    }
    

    /**
     * Function used to update appropriate fields for the shoot function.
     * It makes use of the position of the shooter to determine where or who 
     * to shoot. It then broadcasts to everyone in the game when the target was hit.
     * If you die, again it will broadcast to everyone that a soldier died.
     * @param The positions of the layer that is using the fire command.
     * @return Boolean used to break the loop
    */
    private boolean shotRobot(Position newPosition){
    
       HashMap<Position, CharacterAbstract> characterPositions = this.worldObject.characterPositions;
       String charId = this.characterObject.getCharacterIdentifier();
      
        for(Position pos:characterPositions.keySet()){
            CharacterAbstract player = this.worldObject.characterPositions.get(pos);
            String playerId = player.getCharacterIdentifier();
            if(checkParam(playerId, pos, charId, newPosition)){
                String shieldsValue = String.valueOf(player.getCharacterState().get("shields"));
                int shields = Integer.parseInt(shieldsValue);
                
                String teamName = charId.substring(0,charId.indexOf("."));
                String role = charId.replace(teamName,"").replace(".", "").trim();
                String killerName = role;//and role actually prints out the team name e.g. apple
                String[] charIdArr = {teamName, role};

                teamName = playerId.substring(0,playerId.indexOf("."));
                role = playerId.replace(teamName,"").replace(".", "").trim();
                String victimName = role;
                String[] playerIdArr = {teamName, role};

                HashMap<String, Object> playerState = player.getCharacterState();
                HashMap<String, String> playerResult = player.getCharacterResult();

                
                String messageToAll;
                String messageToTeam;
                String deathPlace = ForColor.BoldAndGreen.format("("+pos.getX()+","+pos.getY()+")");

                String killer = ForColor.BoldAndRed.format("["+charIdArr[1]+"."+charIdArr[0]+"]");
                String victim = ForColor.BoldAndRed.format("["+playerIdArr[0]+"."+playerIdArr[1]+"]");

                messageToTeam = ForColor.BoldAndWhite.format("[INCOMMING]") + ForColor.BoldAndGreen.format("[ALERT] > ")+
                ForColor.BoldAndYellow.format(" Soldier down! Your team mate ")+victim+ForColor.BoldAndYellow.format(" has just been shot dead by ")
                + killer +ForColor.BoldAndYellow.format(" at ")+deathPlace+ForColor.BoldAndYellow.format("... Farewell!");

                messageToAll = ForColor.BoldAndWhite.format("[INCOMMING]") + ForColor.BoldAndGreen.format("[UPDATE] > ")+
                killer+ForColor.BoldAndYellow.format(" just shot ")+victim+ForColor.BoldAndYellow.format(" to death!");


                shields-=1;
                playerState.put("shields",shields);


                if(shields <= 0){  
                    this.worldObject.terminatePosition(player); //Remove this line when you want a corpse
                    ServerHandler.killClientThread(playerIdArr[1]+"."+playerIdArr[0]);
                    playerState.put("status", "DIED");
                    player.setCharacterState(playerState);
                    ServerHandler.directCommunicationToUser(player, ForColor.BoldAndYellow.format("Sorry soldier, the road ends here for you. You were killed by: ")+ killer);
                    this.characterObject.setMessage(ForColor.BoldAndYellow.format("Bullseye! You just killed ")+ victim);
                    alertTeam(player, this.characterObject, messageToTeam, victimName);
                    alertEnemies(player, this.characterObject, messageToAll, killerName);

                }else{
                    player.setCharacterState(playerState);
                    ServerHandler.directCommunicationToUser(player, ForColor.BoldAndYellow.format("You've just been shot by ")+ killer);
                    this.characterObject.setMessage(ForColor.BoldAndGreen.format("Nice! You just shot ")+ victim);
                }
                
                player.setCharacterState(playerState);
                if(worldObject.getPlaceHealthPack()){
                    ServerHandler.getWorldObject().getHealthPackList();             
                }
                return true;
            }
            
        }
        return false;
    }

    private void alertTeam(CharacterAbstract deadOne, CharacterAbstract killer, String messageToAll, String teamName){
        CharacterAbstract getTarget = null;

        HashMap<Position, CharacterAbstract> robotPositions = ServerHandler.getWorldObject().getCharacterPositions();
        for(Map.Entry<Position, CharacterAbstract> robot : robotPositions.entrySet()){
            getTarget = robot.getValue();
            if( !getTarget.getCharacterIdentifier().equals(killer.getCharacterIdentifier()) &&
            !getTarget.getCharacterIdentifier().equals(deadOne.getCharacterIdentifier()) &&
             getTarget.getCharacterIdentifier().contains(teamName)
            ){

                ServerHandler.directCommunicationToUser(getTarget, messageToAll);
            }
        }
    }


    private void alertEnemies(CharacterAbstract deadOne, CharacterAbstract killer, String messageToAll, String teamName){
        CharacterAbstract getTarget = null;

        HashMap<Position, CharacterAbstract> robotPositions = ServerHandler.getWorldObject().getCharacterPositions();
        for(Map.Entry<Position, CharacterAbstract> robot : robotPositions.entrySet()){
            getTarget = robot.getValue();
            if( !getTarget.getCharacterIdentifier().equals(killer.getCharacterIdentifier()) &&
            !getTarget.getCharacterIdentifier().equals(deadOne.getCharacterIdentifier()) &&
             getTarget.getCharacterIdentifier().contains(teamName)
            ){

                ServerHandler.directCommunicationToUser(getTarget, messageToAll);
            }
        }
    }


    private void shotAlerts(CharacterAbstract killer, String messageBack){

        /***
        * Had to move this message otherwise shot message echoes even to the one being killed
        * Gun echoes to all other users in the game when shots are fired
        */
        String tag = ForColor.BoldAndWhite.format("[INCOMMING]") + ForColor.BoldAndRed.format("[ALERT] > ");
        HashMap<Position, CharacterAbstract> robotPositions = this.worldObject.getCharacterPositions();

        for(Position robot : robotPositions.keySet()){
            CharacterAbstract getTarget = robotPositions.get(robot);
            if(!getTarget.getCharacterIdentifier().equals(killer.getCharacterIdentifier())){
                ServerHandler.directCommunicationToUser(getTarget, tag + messageBack);
            }
        }
    }


    private boolean checkParam(String playerId, Position pos, String charId, Position newPosition) {
        return 
        pos.getX()>=newPosition.getX() && pos.getX()<=newPosition.getX()+5 &&
        pos.getY()>=newPosition.getY() && pos.getY()<=newPosition.getY()+5 &&
        !charId.equals(playerId)||
        pos.getX()<=newPosition.getX() && pos.getX()>=newPosition.getX()-5 &&
        pos.getY()<=newPosition.getY() && pos.getY()>=newPosition.getY()-5 &&
        !charId.equals(playerId)||
        pos.getX()>=newPosition.getX() && pos.getX()<=newPosition.getX()+5 &&
        pos.getY()<=newPosition.getY() && pos.getY()>=newPosition.getY()-5 &&
        !charId.equals(playerId)||
        pos.getX()<=newPosition.getX() && pos.getX()>=newPosition.getX()-5 &&
        pos.getY()>=newPosition.getY() && pos.getY()<=newPosition.getY()+5 &&
        !charId.equals(playerId);
    } 
}
