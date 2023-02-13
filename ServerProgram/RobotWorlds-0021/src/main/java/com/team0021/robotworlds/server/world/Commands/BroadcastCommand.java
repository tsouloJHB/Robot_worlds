package com.team0021.robotworlds.server.world.Commands;
import java.util.HashMap;
import java.util.Map;

import com.team0021.robotworlds.ForColor;
import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Position;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

public class BroadcastCommand extends Command{
    public BroadcastCommand(String argument) {
        super("#", argument);
        
    }

    @Override
    public boolean execute(CharacterAbstract characterObject) {
        String argument = getArgument();
        String[] message = argument.split(" ", 2);
        
        String targetMessage = message[1].trim();
        String playerIdentifier = characterObject.getCharacterIdentifier();
        String teamIdentifier = characterObject.getCharacterIdentifier();

        if(playerIdentifier.contains("sniper")){
            playerIdentifier = playerIdentifier.replace("sniper.", "");
            teamIdentifier = teamIdentifier.replace("sniper.", "").replace(".sniper", "");
        }else
        if(playerIdentifier.contains("defender")){
            playerIdentifier = playerIdentifier.replace("defender.", "");
            teamIdentifier = teamIdentifier.replace("defender.", "").replace(".defender", "");
        }else
        if(playerIdentifier.contains("scout")){
            playerIdentifier = playerIdentifier.replace("scout.", "");
            teamIdentifier = teamIdentifier.replace("scout.", "").replace(".scout", "");
        }else
        if(playerIdentifier.contains("allrounder")){
            playerIdentifier = playerIdentifier.replace("allrounder.", "");
            teamIdentifier = teamIdentifier.replace("allrounder.", "").replace(".allrounder", "");
        }
    

        playerIdentifier = ForColor.BoldAndPurple.format("["+playerIdentifier+"] > ");
        String confirmation = ForColor.BoldAndGreen.format("Message Sent!");
        int playerCount = 0;
        CharacterAbstract getTarget = null;
        HashMap<Position, CharacterAbstract> robotPositions = ServerHandler.getWorldObject().getCharacterPositions();

        /**
        * Allows communication to everyone in the world...
        */
        if(message.length > 1 && message[0].contains("#all")){
            //Capturing player identifier for player broadcasting
            targetMessage = ForColor.BoldAndGreen.format(targetMessage);
            for(Map.Entry<Position, CharacterAbstract> robot : robotPositions.entrySet()){
                getTarget = robot.getValue();
                if(getTarget.getCharacterIdentifier() != characterObject.getCharacterIdentifier()){
                    ServerHandler.directCommunicationToUser(getTarget, playerIdentifier + targetMessage);
                    playerCount++;
                }
            }
            if (playerCount > 0) {
                characterObject.setMessage(confirmation);}
            else{
                characterObject.setMessage(ForColor.BoldAndYellow.format("There is no one else in this world..."));
            }
        }      
        /**
        * Allows communication to your own team members...
        */
       else if(message.length > 1 && message[0].contains("#team")){
            targetMessage = ForColor.BoldAndGreen.format(targetMessage);
            for(Map.Entry<Position, CharacterAbstract> robot : robotPositions.entrySet()){
                getTarget = robot.getValue();
                if(getTarget.getCharacterIdentifier() != characterObject.getCharacterIdentifier() && getTarget.getCharacterIdentifier().contains(teamIdentifier)){
                    ServerHandler.directCommunicationToUser(getTarget, playerIdentifier + targetMessage);
                    playerCount++;
                }
            }
            if (playerCount > 0) {
                characterObject.setMessage(confirmation);}
            else{
                characterObject.setMessage(ForColor.BoldAndYellow.format("You are the only person in this team..."));
            }
       }
        return true;
    }

}
