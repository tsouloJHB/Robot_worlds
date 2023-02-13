package com.team0021.robotworlds.server.world.Commands;

import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

/**
 * Class provides implementation for the LeftCommand. It is an extention of the
 * superclass Command. This class inherits the method 'execute' to provide its 
 * own implemenation of the method.
 * @author imogano
*/
public class LeftCommand extends Command{


    /**
     * Class constructor for the LeftCommand.
     * @param None
    */
    public LeftCommand() {
        super("left");
    }


    /**
     * Method provides implementation of the LeftCommand instruction.
     * @param Robot object 'target'
     * @return Boolean object 'true'
    */
    @Override
    public boolean execute(CharacterAbstract characterObject) {
        Positions worldObject = ServerHandler.getWorldObject();
        worldObject.updateDirection(characterObject, false);
        characterObject.setMessage("Turned left.");
        return true;
    }
}
