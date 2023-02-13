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

public class RightCommandTest {

    @Test
    @DisplayName("Running RightGreenTest")
    public void RightGreenTest()
    {
        CharacterAbstract characterObject = new Scout("aa.scout");
        Positions worldObject = ServerHandler.getWorldObject();
        worldObject.launchCharacter(characterObject);
        String beforeTurning = characterObject.getCharacterDirection();
        Command newCommand =  Command.create("turn right");
        assertTrue(newCommand.execute(characterObject));
        String afterTurning = characterObject.getCharacterDirection();
        HashMap<String, Object> state = characterObject.getCharacterState();
        // assertEquals("NORTH", beforeTurning);
        // assertEquals("EAST", afterTurning);
        assertEquals("Turned right.", characterObject.getMessage());
        worldObject.terminatePosition(characterObject);
    } 
}
