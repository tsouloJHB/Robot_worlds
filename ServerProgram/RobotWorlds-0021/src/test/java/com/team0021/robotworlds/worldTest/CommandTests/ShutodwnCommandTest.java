package com.team0021.robotworlds.worldTest.CommandTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;
import com.team0021.robotworlds.server.world.Characters.Sniper;
import com.team0021.robotworlds.server.world.Commands.Command;

public class ShutodwnCommandTest {
 
    @Test
    @DisplayName("Testing ShutodwnCommandRedTest")
    public void ShutodwnCommandRedTest()
    {
        CharacterAbstract characterObject = new Sniper("aa.sniper");
        Positions worldObject = ServerHandler.getWorldObject();
        worldObject.launchCharacter(characterObject);
        try{
            Command.create("bye");
        }
        catch (IllegalArgumentException e){
            assertEquals("Are you okay ;( ?  I cannot do: \"bye\". Seek help @ \"help\".", e.getLocalizedMessage());
        }
        HashMap<String, Object> state = characterObject.getCharacterState();
        HashMap<String, String> result = characterObject.getCharacterResult();

        assertEquals("NORMAL", state.get("status"));
        assertFalse(worldObject.getCharacterPositions().isEmpty());
        assertNotEquals("Shutting down...", characterObject.getMessage());
        worldObject.terminatePosition(characterObject);
    } 


    @Test
    @DisplayName("Testing ShutodwnCommandGreenTest")
    public void ShutodwnCommandGreenTest(){
        CharacterAbstract characterObject = new Sniper("aa.sniper");
        Positions worldObject = ServerHandler.getWorldObject();
        worldObject.launchCharacter(characterObject);
        Command newCommand =  Command.create("off");
        newCommand.execute(characterObject);
        HashMap<String, Object> state = characterObject.getCharacterState();
        HashMap<String, String> result = characterObject.getCharacterResult();

        assertEquals("EXIT", state.get("status"));
        assertEquals("OK", result.get("result"));
        assertEquals("Shutting down...", characterObject.getMessage());
    } 
    
}
