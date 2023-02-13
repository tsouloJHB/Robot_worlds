package com.team0021.robotworlds.server.world.Commands;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.team0021.robotworlds.ForColor;
import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Position;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

public class DirectComCommand extends Command {
    
    public DirectComCommand(String argument) {
        super("#", argument);
        
    }

    @Override
    public boolean execute(CharacterAbstract characterObject) {
        String argument = getArgument();
        String[] message = argument.split(" ", 2);
        message[0] = message[0].toLowerCase();
        String targetIdentifier = message[0].replace("#", ""); //get target identifier as username.role - the user the message is going to
        targetIdentifier = targetIdentifier.toLowerCase().trim();
        String teamIdentifier = characterObject.getCharacterIdentifier(); //the target team the message going to
        String senderIdentifier = characterObject.getCharacterIdentifier(); //who the message is coming from
        String targetMessage = "";
        
        
        if(senderIdentifier.contains("sniper")){
            senderIdentifier = senderIdentifier.replace("sniper.", "");
            teamIdentifier = teamIdentifier.replace("sniper.", "").replace(".sniper", "");
        }else
        if(senderIdentifier.contains("defender")){
            senderIdentifier = senderIdentifier.replace("defender.", "");
            teamIdentifier = teamIdentifier.replace("defender.", "").replace(".defender", "");
        }else
        if(senderIdentifier.contains("scout")){
            senderIdentifier = senderIdentifier.replace("scout.", "");
            teamIdentifier = teamIdentifier.replace("scout.", "").replace(".scout", "");
        }else
        if(senderIdentifier.contains("allrounder")){
            senderIdentifier = senderIdentifier.replace("allrounder.", "");
            teamIdentifier = teamIdentifier.replace("allrounder.", "").replace(".allrounder", "");
        }


        CharacterAbstract getTarget = null;
        String confirmation = ForColor.BoldAndGreen.format("Message Sent!");
        HashMap<Position, CharacterAbstract> robotPositions = ServerHandler.getWorldObject().getCharacterPositions();
        /**
         * A function that sends direct messages to the serverside...
         */
        if(message.length > 1 && message[0].contains("#admin")){
            senderIdentifier = ForColor.BoldAndYellow.format("["+senderIdentifier+"] > ");
            String serverMessage = senderIdentifier + ForColor.BoldAndGreen.format(message[1].trim());
            

            System.out.println(serverMessage);
            characterObject.setMessage(confirmation);
        }
        /**
         * Allows communication to everyone available in the world...
         */
        else if(message.length > 1 && message[0].contains("#") && !message[0].contains("#admin")){
            senderIdentifier = ForColor.BoldAndPurple.format("["+senderIdentifier+"] > ");
            for(Map.Entry<Position, CharacterAbstract> robot : robotPositions.entrySet()){
                getTarget = robot.getValue();
                if(validateTargetIdentifier(targetIdentifier)){
                    if(getTarget.getCharacterIdentifier().contains(targetIdentifier) && !getTarget.getCharacterIdentifier().equals(characterObject.getCharacterIdentifier())){
                        targetMessage = message[1].trim(); //Get user direct message to send over to target
                        targetMessage = ForColor.BoldAndGreen.format(targetMessage); //make the message pretty
                        ServerHandler.directCommunicationToUser(getTarget, senderIdentifier + targetMessage); //pass in the the target, player message with player/senders identifier
                        characterObject.setMessage(confirmation);
                    }
                }
            }
        }
        return true;
    }

    //Validate User Identifier 
    private boolean validateTargetIdentifier(String targetIdentifier){
        String subRegex = "^[a-z]{5,15}\\.";
        String regex = subRegex+"scout$|" +subRegex+"defender$|"+subRegex+"allrounder$|"+subRegex+"sniper$";
        Pattern myPattern = Pattern.compile(regex);
        Matcher myMatch = myPattern.matcher(targetIdentifier);
        return myMatch.matches();
    }
}
