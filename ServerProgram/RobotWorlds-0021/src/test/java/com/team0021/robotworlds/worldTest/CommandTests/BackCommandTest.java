package com.team0021.robotworlds.worldTest.CommandTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Position;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;
import com.team0021.robotworlds.server.world.Characters.Scout;
import com.team0021.robotworlds.server.world.Commands.Command;

public class BackCommandTest {
    
    @Test
    @DisplayName("Running BackCommandRedTest")
    public void BackCommandRedTest()
    {
        CharacterAbstract characterObject = new Scout("aa.scout");
        Positions worldObject = ServerHandler.getWorldObject();
        worldObject.launchCharacter(characterObject);
        Position beforeTurning = characterObject.getCharacterPosition();
        int x1 = beforeTurning.getX();
        int y1 =  beforeTurning.getY();
        try{
            Command.create("go opposite direction 11");
        }
        catch (IllegalArgumentException e){
            assertEquals("Are you okay ;( ?  I cannot do: \"go opposite direction 11\". Seek help @ \"help\".", e.getLocalizedMessage());
        }
        HashMap<String, Object> state = characterObject.getCharacterState();
        Position afterTurning = characterObject.getCharacterPosition();
        int x2 = afterTurning.getX();
        int y2 =  afterTurning.getY();
        assertEquals(x1,x2);
        assertEquals(y1,y2); 
        assertEquals("NORMAL", state.get("status"));
        assertNotEquals("Moved back by 11 steps.", characterObject.getMessage());
        worldObject.terminatePosition(characterObject);
    } 


    @Test
    @DisplayName("Running BackCommandGreenTest")
    public void BackCommandGreenTest()
    {
        CharacterAbstract characterObject = new Scout("aa.scout");
        Positions worldObject = ServerHandler.getWorldObject();
        worldObject.launchCharacter(characterObject);
        Position beforeTurning = characterObject.getCharacterPosition();
        int x1 = beforeTurning.getX();
        int y1 =  beforeTurning.getY();
        Command newCommand =  Command.create("back 0");
        assertTrue(newCommand.execute(characterObject));
        HashMap<String, Object> state = characterObject.getCharacterState();      
        assertEquals("NORMAL", state.get("status"));
        assertEquals("Moved back by 0 steps.", characterObject.getMessage());
        worldObject.terminatePosition(characterObject);
    } 
}
