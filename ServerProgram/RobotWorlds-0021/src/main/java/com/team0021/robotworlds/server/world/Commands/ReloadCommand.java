package com.team0021.robotworlds.server.world.Commands;

import com.team0021.robotworlds.ForColor;
import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

public class ReloadCommand extends Command {
    private String characterID;

    public ReloadCommand(){
        super("reload");
    }

    
    @Override
    public boolean execute(CharacterAbstract characterObject) {
        characterID = characterObject.getCharacterIdentifier();
        int reload = (int)characterObject.getCharacterState().get("reload");

        if(reload > 0){
            if(characterID.contains("defender")){
                int mines = (int)characterObject.getCharacterData().get("mine");
                characterObject.setMessage(ForColor.BoldAndGreen.format("Ammo reloaded!"));
                characterObject.getCharacterState().put("reload", reload-1);
                characterObject.getCharacterState().put("mine", mines);
            }else{
                int shots = (int)characterObject.getCharacterData().get("shots");
                characterObject.setMessage(ForColor.BoldAndGreen.format("Ammo reloaded!"));
                characterObject.getCharacterState().put("reload", reload-1);
                characterObject.getCharacterState().put("shots", shots);
            }
            
        }else{
            characterObject.setMessage(ForColor.BoldAndRed.format("You can't reload anymore!"));
        }
        return true;
    }    
}
