package com.team0021.robotworlds.server.world.Characters;

import java.util.HashMap;

import com.team0021.robotworlds.server.world.Position;

public interface Character {

    
    HashMap<String, String> getCharacterResult();

    
    HashMap<String, Object> getCharacterData();


    HashMap<String, Object> getCharacterState();


    void setCharacterResult(HashMap<String, String> result);

    
    void setCharacterData(HashMap<String, Object> data);


    void setCharacterState(HashMap<String, Object> state);

    
    void setCharacterPosition(Position newPosition);

    
    Position getCharacterPosition();


    String getCharacterDirection();


    String getTeamName();


    void setMessage(String commandResponse);


    String getMessage();


    String getCharacterIdentifier();


    CharacterType getCharacterType();

    void showHelp();
}
