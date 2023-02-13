package com.team0021.robotworlds.server.world.Commands;

import java.util.HashMap;
import com.team0021.robotworlds.ForColor;
import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

public class SpyCommand extends Command {

    private String edge;
    private Positions worldObject;

    public SpyCommand() {
        super("spy");
        this.worldObject = ServerHandler.getWorldObject();

    }

    @Override
    public boolean execute(CharacterAbstract characterObject) {
        String characterIdentifier = characterObject.getCharacterIdentifier();
        HashMap<String, String> result = characterObject.getCharacterResult();
        if(!characterIdentifier.contains("scout")){
            String message = ForColor.BoldAndRed.format("This command is reserved for the scout only.");
            result.put("message", message);
            result.put("result", "FAILED");
        }
        else{
            String teamName = characterIdentifier.replace(".", "").replace("scout","").trim();
            Command newCommand = Command.create("report");
            newCommand.execute(characterObject);
            result = characterObject.getCharacterResult();
            String response = "[SCOUT]: I spy with my little eye <*~*> \n"+result.get("message");
            if(!response.contains("(")){
                response = "[SCOUT]: Got nothing <*~*>.";
            }
            ServerHandler.broadCastToTeamOnly(teamName,response);
            result.put("message","(\033[H\033[2JMission accomplished.");
            result.put("result", "OK");

        }
        characterObject.setCharacterResult(result);
        return true;
    }
    
}
