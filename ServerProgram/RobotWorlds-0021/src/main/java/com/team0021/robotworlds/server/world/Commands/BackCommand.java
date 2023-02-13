package com.team0021.robotworlds.server.world.Commands;

import java.util.HashMap;
import java.util.Map;

import com.team0021.robotworlds.ForColor;
import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Position;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

/**
 * Class provides implementation for the BackCommand. It is an extention of the
 * superclass Command. This class inherits the method 'execute' to provide its 
 * own implemenation of the method.
 * @author imogano
*/
public class BackCommand extends Command {


    /**
     * Class constructor for the BackCommand.
     * @param None
    */
    public BackCommand(String argument) {
        super("back", argument);
    }


    /**
     * Method provides implementation of the BackCommand instruction.
     * @param Robot object 'target'
     * @return Boolean object 'true'
    */
    @Override
    public boolean execute(CharacterAbstract characterObject) {
        String nrStepsValue = String.valueOf(getArgument()); 
        int nrSteps = Integer.parseInt(nrStepsValue);
        Positions worldObject = ServerHandler.getWorldObject();
        String victim = getIdentifier(characterObject.getCharacterIdentifier());
        switch (worldObject.updatePosition(characterObject, -nrSteps)){
            case SUCCESS:{
                characterObject.setMessage("Moved back by "+nrSteps+" steps.");
                tellTale(characterObject, nrSteps);
                break;
            }
            case FAILED_PITFALL:{
                characterObject.setMessage(ForColor.BoldAndRed.format("Sorry, you fell into a pitfall."));
                String messageToAll = ForColor.BoldAndWhite.format("[INCOMMING]") + ForColor.BoldAndGreen.format("[UPDATE] > ")+
                ForColor.BoldAndYellow.format("Psst... Guess what? "+victim+ForColor.BoldAndYellow.format(" just fell into a pitfall, LOL :D"));
                alertMessage(characterObject, messageToAll);
                return false;
            }
            case FAILED_ROBOT:{
                characterObject.setMessage("Sorry, there is a robot in the way.");
                break;
            }
            case FAILED_OBSTRUCTED:{
                characterObject.setMessage("Sorry, there is an obstacle in the way.");
                break;
            }
            case HEALTH_PACK:{
                characterObject.setMessage(ForColor.BoldAndGreen.format("You found a health pack!"));
                String messageToAll = ForColor.BoldAndWhite.format("[INCOMMING]") + ForColor.BoldAndRed.format("[ALERT] > ")+
                ForColor.BoldAndYellow.format("Unfortunately, the Health Pack was just picked up, maybe next time!");
                alertMessage(characterObject, messageToAll);
                break;
            }
            case FAILED_MINE:{
                
                String status = (String) characterObject.getCharacterState().get("status");
                if (status.equals("DIED")){
                    characterObject.setMessage(ForColor.BoldAndRed.format("Alas... You got killed by a mine in battle."));
                    String messageToAll = ForColor.BoldAndWhite.format("[INCOMMING]") + ForColor.BoldAndRed.format("[ALERT] > ")+
                    ForColor.BoldAndYellow.format("WoooooOOOH!!! ")+victim+ForColor.BoldAndYellow.format(" just blew up into tiny little pieces, must be a mine. LOL :D");
                    alertMessage(characterObject, messageToAll);
                }else{
                    characterObject.setMessage(ForColor.BoldAndYellow.format("Be more careful soldier! you just took mine damage."));
                }
                break;
            }
            default:{
                characterObject.setMessage("Sorry, I cannot go outside my safe zone.");
                break;
            }
        }
        return true;
    }


    private void alertMessage(CharacterAbstract lucky, String messageToAll){
        CharacterAbstract getTarget = null;
        
        HashMap<Position, CharacterAbstract> robotPositions = ServerHandler.getWorldObject().getCharacterPositions();
        for(Map.Entry<Position, CharacterAbstract> robot : robotPositions.entrySet()){
            getTarget = robot.getValue();
            if(!getTarget.getCharacterIdentifier().equals(lucky.getCharacterIdentifier()))
                ServerHandler.directCommunicationToUser(getTarget, messageToAll);
        }
    }


    private void tellTale(CharacterAbstract characterObject, int nrSteps){
        CharacterAbstract getTarget = null;
        HashMap<Position, CharacterAbstract> robotPositions = ServerHandler.getWorldObject().getCharacterPositions();
    
        if(nrSteps >= 80)
        {   
            String tag = ForColor.BoldAndWhite.format("[INCOMMING]") + ForColor.BoldAndRed.format("[ALERT] > ");
            String coOrdinates = ForColor.BoldAndYellow.format(" ("+characterObject.getCharacterPosition().getX()+", "
                                +characterObject.getCharacterPosition().getY()+") ");
            String messageToAll = ForColor.BoldAndGreen.format("Psst.. Someone just made a big move to these co-ordinates:")+
                    coOrdinates + ForColor.BoldAndGreen.format(" Go check it out!");
    
            for(Map.Entry<Position, CharacterAbstract> robot : robotPositions.entrySet()){
                getTarget = robot.getValue();
                if(!getTarget.getCharacterIdentifier().equals(characterObject.getCharacterIdentifier())){
                    ServerHandler.directCommunicationToUser(getTarget, tag + messageToAll);
                }
            }           
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