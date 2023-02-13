package com.team0021.robotworlds.server.world.Commands;

import com.team0021.robotworlds.ForColor;
import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Position;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

public class Look extends Command{
    public Look() {
        super("look");
    }

    /**
     * Method provides implementation of the RightCommand instruction.
     * @param Robot object 'target'
     * @return Boolean object 'true'
    */
    @Override
    public boolean execute(CharacterAbstract characterObject) {
        Positions worldObject = ServerHandler.getWorldObject();
        Position currentPosition = characterObject.getCharacterPosition();
        String message = "\n";
        message+=worldObject.getVisibilityNorth(currentPosition);
        message+=worldObject.getVisibilityEast(currentPosition);
        message+=worldObject.getVisibilitySouth(currentPosition);
        message+=worldObject.getVisibilityWest(currentPosition);
        if(message.equals("\n")){
            message=
            ForColor.BoldAndWhite.format("\n\tNo ")+
            ForColor.BoldAndRed.format("mine")+ForColor.BoldAndWhite.format(" in sight.")+
            ForColor.BoldAndWhite.format("\n\tNo ")+ForColor.BoldAndBlue.format("robot")+ForColor.BoldAndWhite.format(" in sight.")+
            ForColor.BoldAndWhite.format("\n\tNo ")+ForColor.BoldAndPurple.format("pitfall")+ForColor.BoldAndWhite.format(" in sight.")+
            ForColor.BoldAndWhite.format("\n\tNo ")+ForColor.BoldAndYellow.format("obstacle")+ForColor.BoldAndWhite.format(" in sight.")+
            ForColor.BoldAndWhite.format("\n\tNo ")+ForColor.BoldAndGreen.format("health pack")+ForColor.BoldAndWhite.format(" in sight.")+
            ForColor.BoldAndPurple.format("\n\tTip")+
            ForColor.BoldAndWhite.format(": Use ")+
            ForColor.BoldAndYellow.format("\"Report\"")+
            ForColor.BoldAndWhite.format(" for a 360 sweep around you. Stay clear!");
        }
        characterObject.setMessage(message);
        return true;
    }
}
