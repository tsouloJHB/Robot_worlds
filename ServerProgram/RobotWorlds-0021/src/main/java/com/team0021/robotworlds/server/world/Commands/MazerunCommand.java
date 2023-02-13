package com.team0021.robotworlds.server.world.Commands;

import java.util.HashMap;

import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

public class MazerunCommand extends Command {
    private Positions worldObject;
    private String edge;

    public MazerunCommand(String argument) {
        super("mazerun", argument);
        this.edge = argument;
        this.worldObject = ServerHandler.getWorldObject();
    }

    @Override
    public boolean execute(CharacterAbstract characterObject) {
        String characterIdentifier = characterObject.getCharacterIdentifier();
        HashMap<String, String> result = characterObject.getCharacterResult();
        if(!characterIdentifier.contains("scout")){
            result.put("message","This command is reserved for the scout only.");
            result.put("result", "FAILED");
        }
        else{
            PathFinder mazerun = new PathFinder(this.worldObject);
            String response = mazerun.doMazerun(characterObject, edge);
            result.put("message",response);
            result.put("result", "OK");

        }
        characterObject.setCharacterResult(result);
        return true;
    }
    
}
