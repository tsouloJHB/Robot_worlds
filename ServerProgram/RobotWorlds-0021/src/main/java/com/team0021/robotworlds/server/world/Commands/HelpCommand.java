package com.team0021.robotworlds.server.world.Commands;

import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

/**
 * Class provides implementation for the HelpCommand. It is an extention of the
 * superclass Command. This class inherits the method 'execute' to provide its 
 * own implemenation of the method.
 * @author imogano
*/
public class HelpCommand extends Command {


    /**
     * Class constructor for the HelpCommand.
     * @param None
    */
    public HelpCommand() {
        super("help");
    }


    /**
     * Method provides implementation of the HelpCommand instruction.
     * @param Robot object 'target'
     * @return Boolean object 'true'
    */
    @Override
    public boolean execute(CharacterAbstract characterObject) {
        characterObject.showHelp();
        return true;
    }
}   