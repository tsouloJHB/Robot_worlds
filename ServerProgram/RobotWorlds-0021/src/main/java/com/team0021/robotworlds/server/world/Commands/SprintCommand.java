package com.team0021.robotworlds.server.world.Commands;

import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

/**
 * Class provides implementation for the SprintCommand. It is an extention of 
 * the superclass Command. This class inherits the method 'execute' to provide 
 * its own implemenation of the method.
 * @author imogano
*/
public class SprintCommand extends Command {

    
    /**
     * Class constructor for the ShutdownCommand.
     * @param None
    */
    public SprintCommand(String argument) {
        super("sprint", argument);
    }


    /**
     * Method provides implementation of the SprintCommand instruction.
     * @param Robot object 'target'
     * @return Boolean object 'true'
    */
    @Override
    public boolean execute(CharacterAbstract characterObject){
        int nrSteps = Integer.parseInt(getArgument());
        boolean condition = true;
        String message = "";
        Command fr;
        while (nrSteps>0)
        {
            fr = new ForwardCommand(String.valueOf(nrSteps));

            condition = fr.execute(characterObject);
            if (nrSteps>=1 && !characterObject.getMessage().contains("Sorry"))
                message+=characterObject.getMessage()+"\n";

            nrSteps -= 1;

            if (nrSteps == 0 || characterObject.getMessage().contains("Sorry"))
                break;
        }
        message+= "Sprint done.";
        characterObject.setMessage(message);
        return condition;
    }

}