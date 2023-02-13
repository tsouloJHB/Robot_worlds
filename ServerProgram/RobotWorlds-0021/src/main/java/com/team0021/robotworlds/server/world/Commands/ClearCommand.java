package com.team0021.robotworlds.server.world.Commands;

import com.team0021.robotworlds.ForColor;
import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

public class ClearCommand extends Command{

    /**
     * Class constructor for the LeftCommand.
     * @param None
     */
    public ClearCommand() {
        super("clear");
    }


    /**
     * Method provides implementation of the LeftCommand instruction.
     * @param Robot object 'target'
     * @return Boolean object 'true'
    */
    @Override
    public boolean execute(CharacterAbstract characterObject) {
        characterObject.setMessage("(\033[H\033[2J");
        return true;
    }


}