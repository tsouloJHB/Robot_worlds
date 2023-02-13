package com.team0021.robotworlds.server.world.Commands;

import java.util.HashMap;
import java.util.Map;
import com.team0021.robotworlds.ForColor;
import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Position;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

/**
 * Class provides implementation for the RepairCommand. It is an extention of the
 * superclass Command. This class inherits the method 'execute' to provide its 
 * own implemenation of the method.
*/
public class RepairCommand extends Command{

    
    /**
     * Class constructor for the RepairCommand.
     * @param None
    */
    public RepairCommand(){
        super("repair");
    }


    /**
     * Method provides implementation of the RepairCommand instruction.
     * @param Robot object 'target'
     * @return Boolean object 'true'
    */
    @Override
    public boolean execute(CharacterAbstract characterObject){
        String characterID = characterObject.getCharacterIdentifier();
        HashMap <String, Object> state = characterObject.getCharacterState();
        HashMap <String, Object> data = characterObject.getCharacterData();
        String shieldsValue = String.valueOf(data.get("shields"));
        String repairsValue = String.valueOf(state.get("repair"));
        int shields = Integer.parseInt(shieldsValue);
        int repairs = Integer.parseInt(repairsValue);
        // int shields = (int)data.get("shields");
        // int repairs = (int)state.get("repair");

        CharacterAbstract getTarget = null;
        HashMap<Position, CharacterAbstract> robotPositions = ServerHandler.getWorldObject().getCharacterPositions();
        for(Map.Entry<Position, CharacterAbstract> robot : robotPositions.entrySet()){
            getTarget = robot.getValue();
            if(getTarget.getCharacterIdentifier().equals(characterObject.getCharacterIdentifier())){
                ServerHandler.directCommunicationToUser( getTarget, ForColor.BoldAndYellow.format("Repairing shield..."));
            }
        }
        if(repairs != 0){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("You're in luck, now we dont have to put you on sleep.");
            }
            characterObject.setMessage(ForColor.BoldAndGreen.format("Shield repair complete."));
            state.put("repair", repairs-1);
            state.put("shields",  shields); 
        }else{
            characterObject.setMessage(ForColor.BoldAndRed.format("You're out of repairs."));
        }
        characterObject.setCharacterState(state);
        return true;
    }
}
