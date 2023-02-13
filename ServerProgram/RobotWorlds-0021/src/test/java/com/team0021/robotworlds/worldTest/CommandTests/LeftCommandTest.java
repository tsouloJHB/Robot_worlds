package com.team0021.robotworlds.worldTest.CommandTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;
import com.team0021.robotworlds.server.world.Characters.Scout;
import com.team0021.robotworlds.server.world.Commands.Command;

public class LeftCommandTest {
    
    @Test
    @DisplayName("Running LeftCommandRedTest")
    public void LeftCommandRedTest()
    {
        CharacterAbstract characterObject = new Scout("aa.scout");
        Positions worldObject = ServerHandler.getWorldObject();
        worldObject.launchCharacter(characterObject);
        try{
            Command.create("left");
        }
        catch (IllegalArgumentException e){
            assertEquals("Are you okay ;( ?  I cannot do: \"left\". Seek help @ \"help\".", e.getLocalizedMessage());
        }

        HashMap<String, Object> state = characterObject.getCharacterState();

        assertEquals("NORMAL", state.get("status"));
        assertEquals("NORTH", characterObject.getCharacterDirection());
        assertNotEquals("Turned left.", characterObject.getMessage());
        worldObject.terminatePosition(characterObject);
    } 

}
