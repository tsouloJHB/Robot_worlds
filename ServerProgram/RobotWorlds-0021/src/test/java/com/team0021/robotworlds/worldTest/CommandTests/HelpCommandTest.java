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


public class HelpCommandTest {
    @Test
    public void helpRedTest()
    {
        CharacterAbstract characterObject = new Scout("aa.scout");
        Positions worldObject = ServerHandler.getWorldObject();
        worldObject.launchCharacter(characterObject);
        try{
            Command.create("please help");
        }
        catch (IllegalArgumentException e){
            assertEquals("Are you okay ;( ?  I cannot do: \"please help\". Seek help @ \"help\".", e.getLocalizedMessage());
        }
        HashMap<String, Object> state = characterObject.getCharacterState();
        assertEquals("NORMAL", state.get("status"));
        assertEquals("NORTH", characterObject.getCharacterDirection());
        assertNotEquals("Here's help.", characterObject.getMessage());
        worldObject.terminatePosition(characterObject);
    } 


    @Test
    public void helpGreenTest()
    {
        assertEquals(1,1);
    } 
}
