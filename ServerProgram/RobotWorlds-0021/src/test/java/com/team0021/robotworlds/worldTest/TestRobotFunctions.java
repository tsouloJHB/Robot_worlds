package com.team0021.robotworlds.worldTest;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.team0021.robotworlds.server.world.Position;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;
import com.team0021.robotworlds.server.world.Characters.Scout;
import com.team0021.robotworlds.server.world.Characters.Sniper;

import org.junit.Test;

public class TestRobotFunctions {

    public class MockRandom extends Random
    {
        private int nextIntMock;

        public MockRandom(int nextIntMock){
            super();
            this.nextIntMock = nextIntMock;
        }


        @Override
        public int nextInt(int range)
        {
            return this.nextIntMock;
        }

    }

    @Test
    public void testGetRobotPositionsRed(){
        Random newMock = new MockRandom(100);

        Positions newWorld = new Positions(newMock);
        
        CharacterAbstract characterObject = new Sniper("launcher");

        newWorld.launchCharacter(characterObject);

        assertFalse(newWorld.getCharacterPositions().isEmpty());
        assertFalse(newWorld.isPathBlockedByRobot(new Position(0,-10), new Position(0,-90)));


    }
}
