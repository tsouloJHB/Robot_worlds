package com.team0021.robotworlds.server.world.Commands;

import java.util.HashMap;
import java.util.Map;
import java.lang.Thread;
import com.team0021.robotworlds.ForColor;
import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.MinePosition;
import com.team0021.robotworlds.server.world.Position;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;


/**
 * Class provides implementation for the MineCommand. It is an extention of the
 * superclass Command. This class inherits the method 'execute' to provide its 
 * own implemenation of the method.
*/
public class MineCommand extends Command {
    HashMap <String, String> result;
    HashMap <String, Object> state;
    HashMap <String, Object> data;
    private Positions worldObject = ServerHandler.getWorldObject();
    private String characterID;

    
    /**
     * Class constructor for the MineCommand.
     * @param None
    */
    public MineCommand(){
        super("mine");
    }


    /**
     * Method provides implementation of the MineCommand instruction.
     * @param Robot object 'target'
     * @return Boolean object 'true'
    */
    @Override
    public boolean execute(CharacterAbstract characterObject){
        characterID = characterObject.getCharacterIdentifier();
        this.result = characterObject.getCharacterResult();
        this.state = characterObject.getCharacterState();
        this.data = characterObject.getCharacterData();
        int mines = (int)state.get("mine");

        int currentX = characterObject.getCharacterPosition().getX();
        int currentY = characterObject.getCharacterPosition().getY();
       

        CharacterAbstract getTarget = null;
        HashMap<Position, CharacterAbstract> robotPositions = ServerHandler.getWorldObject().getCharacterPositions();
        if(!characterID.contains("defender") && !characterID.contains("allrounder")){
            String message = ForColor.BoldAndRed.format("This command is reserved for the defender and allrounder.");
            result.put("message", message);
            result.put("result", "FAILED");
        }else{
            if(mines < 1){
                characterObject.setMessage(ForColor.BoldAndRed.format("Out of mines. Reload."));
            }else{
                for(Map.Entry<Position, CharacterAbstract> robot : robotPositions.entrySet()){
                    getTarget = robot.getValue();
                    if(getTarget.getCharacterIdentifier().equals(characterObject.getCharacterIdentifier())){
                        ServerHandler.directCommunicationToUser( getTarget, ForColor.BoldAndYellow.format("Setting up mine..."));
                    }
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    System.out.println("You're in luck, now we dont have to put you on sleep.");
                }
                state.put("mine", mines-1);
                for(Map.Entry<Position, CharacterAbstract> robot : robotPositions.entrySet()){
                    getTarget = robot.getValue();
                    if(getTarget.getCharacterIdentifier().equals(characterObject.getCharacterIdentifier())){
                        ServerHandler.directCommunicationToUser(getTarget, ForColor.BoldAndGreen.format("Mine setup complete."));
                    }
                }
                ForwardCommand forward = new ForwardCommand("1");
                forward.execute(characterObject);
                MinePosition newMinePosition = new MinePosition(currentX,currentY);
                worldObject.setMineObjectToList(newMinePosition); 
            }
        }
        characterObject.setCharacterState(state);
        return true;
    }
}

