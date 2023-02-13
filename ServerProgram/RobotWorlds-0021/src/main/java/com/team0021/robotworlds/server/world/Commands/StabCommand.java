package com.team0021.robotworlds.server.world.Commands;

import java.util.HashMap;
import java.util.Map;

import com.team0021.robotworlds.ForColor;
import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Position;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

/**
 * Class provides implementation for the HelpCommand. It is an extention of the
 * superclass Command. This class inherits the method 'execute' to provide its 
 * own implemenation of the method.
 * @author imogano
*/
public class StabCommand extends Command {
    private Positions worldObject;
    private HashMap<String, Object> state;
    private CharacterAbstract characterObject;
   
    /**
     * Class constructor for the HelpCommand.
     * @param None
    */
    public StabCommand() {
        super("stab");
    }


    /**
     * Method provides implementation of the HelpCommand instruction.
     * @param robot character object 'target'
     * @return Boolean object 'true'
    */
    @Override
    public boolean execute(CharacterAbstract characterObject) {
        this.worldObject = ServerHandler.getWorldObject();
        this.state = characterObject.getCharacterState();
        this.characterObject = characterObject;

        int distance = 1;  //me
        String currentDirection = (String) this.state.get("direction"); //me
        Position characterPosition = characterObject.getCharacterPosition(); //me
        
        int newX = characterPosition.getX(); //myX
        int newY = characterPosition.getY(); //myY

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

        Position positionToStab = new Position(newX, newY);    

        if(worldObject.isPathBlockedByRobot(characterPosition, positionToStab)){
            stabRobot(positionToStab);
        }else{
            characterObject.setMessage("You missed your target");
        }
        
        return true;
    }

    private void stabRobot(Position positionToStab){//get the new position with the obstruction
        HashMap<String, Object> state = new HashMap<String, Object>(); //him

        String theStabbersNameF = "";
        String playerToStabNameF = "";

        //Iterate through all robot positions in the world to get robot in positionToStab
        for(Position victimsPosition:this.worldObject.characterPositions.keySet()) 
        {
            CharacterAbstract playerToStab = this.worldObject.getCharacterPositions().get(victimsPosition); 
            //Get a random player from the world and make sure it's not you
            if(playerToStab.getCharacterIdentifier() == characterObject.getCharacterIdentifier()){
                continue;
            }

            theStabbersNameF = ForColor.BoldAndRed.format("["+getIdentifier(characterObject.getCharacterIdentifier()+"]"));
            playerToStabNameF = ForColor.BoldAndRed.format("["+getIdentifier(playerToStab.getCharacterIdentifier()+"]"));
            String theStabbersName = getIdentifier(characterObject.getCharacterIdentifier());
            String playerToStabName = getIdentifier(playerToStab.getCharacterIdentifier());

            if( playerToStab.getCharacterPosition().getX() == positionToStab.getX() ||  playerToStab.getCharacterPosition().getY() == positionToStab.getY()){
                String shields = String.valueOf(playerToStab.getCharacterState().get("shields"));
                int shieldsCount = Integer.parseInt(shields);
                shieldsCount = shieldsCount - 1;

                if(shieldsCount <= 0){
                    this.worldObject.terminatePosition(playerToStab);
                    ServerHandler.killClientThread(playerToStabName);
                    state = playerToStab.getCharacterState();
                    state.put("shields", Integer.toString(shieldsCount));
                    state.put("status", "DIED");
                    playerToStab.setCharacterState(state);
                    ServerHandler.directCommunicationToUser(playerToStab, ForColor.BoldAndRed.format("You were stabbed to death by ") + 
                    ForColor.BoldAndYellow.format("["+theStabbersName+"]"));
                    characterObject.setMessage(ForColor.BoldAndYellow.format("You stabbed ")+playerToStabNameF+ForColor.BoldAndYellow.format(" to death."));

                    String messageToAll = ForColor.BoldAndWhite.format("[INCOMMING]") + ForColor.BoldAndGreen.format("[UPDATE] > ")+
                    theStabbersNameF+ForColor.BoldAndYellow.format(" just stabbed ")+playerToStabNameF+ForColor.BoldAndYellow.format(" to death!");
                    alertMessage(playerToStab, characterObject, messageToAll);
                    break;
                }else{
                    state = playerToStab.getCharacterState();
                    state.put("shields", Integer.toString(shieldsCount));
                    playerToStab.setCharacterState(state);
                    ServerHandler.directCommunicationToUser(playerToStab, ForColor.BoldAndYellow.format("You got stabbed by ")+ theStabbersNameF);
                    characterObject.setMessage(ForColor.BoldAndGreen.format("You stabbed ")+ playerToStabNameF);
                    break;
                }
            }
        }
    }

    private void alertMessage(CharacterAbstract deadOne, CharacterAbstract killer, String messageToAll){
        CharacterAbstract getTarget = null;
        
        HashMap<Position, CharacterAbstract> robotPositions = ServerHandler.getWorldObject().getCharacterPositions();
        for(Map.Entry<Position, CharacterAbstract> robot : robotPositions.entrySet()){
            getTarget = robot.getValue();
            if(!getTarget.getCharacterIdentifier().equals(killer.getCharacterIdentifier()) &&
            !getTarget.getCharacterIdentifier().equals(deadOne.getCharacterIdentifier()))
                ServerHandler.directCommunicationToUser(getTarget, messageToAll);
        }
    }


    private String getIdentifier(String senderIdentifier){

        if(senderIdentifier.contains("sniper")){
            senderIdentifier = senderIdentifier.replace("sniper.", "");
        }else
        if(senderIdentifier.contains("defender")){
            senderIdentifier = senderIdentifier.replace("defender.", "");
        }else
        if(senderIdentifier.contains("scout")){
            senderIdentifier = senderIdentifier.replace("scout.", "");
        }else
        if(senderIdentifier.contains("allrounder")){
            senderIdentifier = senderIdentifier.replace("allrounder.", "");
        }
        return senderIdentifier;
    }
} 