package com.team0021.robotworlds.server.world.Commands;

import java.util.HashMap;

import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

/**
 * Class provides implementation for the ShutdownCommand. It is an extention of 
 * the superclass Command. This class inherits the method 'execute' to provide 
 * its own implemenation of the method.
 * @author imogano
*/
public class ShutdownCommand extends Command {


    /**
     * Class constructor for the ShutdownCommand.
    */
    public ShutdownCommand() {
        super("off");
    }
    
    
    /**
     * Method provides implementation of the ShutdownCommand instruction.
     * @param Robot object 'target'
     * @return Boolean object 'true'
    */
   
    @Override
    public boolean execute(CharacterAbstract characterObject) {
        Positions worldObject = ServerHandler.getWorldObject();
        worldObject.terminatePosition(characterObject);
        HashMap<String, Object> state = characterObject.getCharacterState();
        state.put("status", "EXIT");
        characterObject.setCharacterState(state);
        characterObject.setMessage("Shutting down...");
        return false;
    }
}
