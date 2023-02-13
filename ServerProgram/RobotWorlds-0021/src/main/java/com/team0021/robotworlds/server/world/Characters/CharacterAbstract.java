package com.team0021.robotworlds.server.world.Characters;

import java.util.HashMap;

import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Direction;
import com.team0021.robotworlds.server.world.Position;

public abstract class CharacterAbstract implements Character{
    private String characterIdentifier;

    public CharacterAbstract(String characterIdentifier){
        this.characterIdentifier = characterIdentifier;
    }

    @Override
    public abstract HashMap<String, String> getCharacterResult();

    
    @Override
    public abstract HashMap<String, Object> getCharacterData();


    @Override
    public abstract HashMap<String, Object> getCharacterState();

    @Override
    public abstract void setCharacterResult(HashMap<String, String> result);

    
    @Override
    public abstract void setCharacterData(HashMap<String, Object> data);


    @Override
    public abstract void setCharacterState(HashMap<String, Object> state);

    
    @Override
    public abstract void setCharacterPosition(Position newPosition);

    
    @Override
    public abstract Position getCharacterPosition();

    @Override
    public abstract String getCharacterDirection();

    @Override
    public abstract String getTeamName();

    @Override
    public abstract void setMessage(String commandResponse);

    @Override
    public abstract String getCharacterIdentifier();

    @Override
    public abstract CharacterType getCharacterType();

    @Override
    public abstract String getMessage();

    @Override
    public abstract void showHelp();

}

