package com.team0021.robotworlds.server.world.Commands;

import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

/**
 * Class provides implementation for the RightCommand. It is an extention of the
 * superclass Command. This class inherits the method 'execute' to provide its 
 * own implemenation of the method.
 * @author imogano
*/
public class RightCommand extends Command{


    /**
     * Class constructor for the RightCommand.
     * @param None
    */
    public RightCommand() {
        super("right");
    }

    
    /**
     * Method provides implementation of the RightCommand instruction.
     * @param Robot object 'target'
     * @return Boolean object 'true'
    */
    @Override
    public boolean execute(CharacterAbstract characterObject) {
        Positions worldObject = ServerHandler.getWorldObject();
        worldObject.updateDirection(characterObject, true);
        characterObject.setMessage("Turned right.");
        return true;
    }
}
