package com.team0021.robotworlds.worldTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Obstacles;
import com.team0021.robotworlds.server.world.Pitfalls;
import com.team0021.robotworlds.server.world.Position;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;
import com.team0021.robotworlds.server.world.Characters.Scout;
import com.team0021.robotworlds.server.world.Commands.Command;

public class PitfallsTest {

    public class MockRandom extends Random{
        private int nextIntMock;

        public MockRandom(int nextIntMock){
            super();
            this.nextIntMock = nextIntMock;
        }


        @Override
        public int nextInt(int range){
            return this.nextIntMock;
        }

    }

    @Test
    public void testGetPitFallListRed()
    {
        MockRandom newRand = new MockRandom(0);
        Obstacles pitFallObj = new Pitfalls(newRand);
        Stack<Position> expectedPitList = new Stack<Position>();
        expectedPitList.add(new Position(5,5));
        Stack<Position> actualPitList = pitFallObj.getObstacles();
        assertFalse(expectedPitList.get(0).equals(actualPitList.get(0)));
        assertFalse(actualPitList.size()>1);
   }

    @Test
    public void testGetPitFallListGreen()
    {
        MockRandom newRand = new MockRandom(0);
        Obstacles pitFallObj = new Pitfalls(newRand);
        Stack<Position> expectedPitList = new Stack<Position>();
        expectedPitList.add(new Position(-100,-100));
        Stack<Position> actualPitList = pitFallObj.getObstacles();
        assertTrue(expectedPitList.get(0).equals(actualPitList.get(0)));
        assertTrue(actualPitList.size()==1);
    }


    @Test
    public void testBlocksPositionRed(){
        MockRandom newRand = new MockRandom(0);
        Obstacles pitFallObj = new Pitfalls(newRand);
        pitFallObj.getObstacles();
        assertFalse(pitFallObj.blocksPosition(new Position(1,1)));
        assertFalse(pitFallObj.blocksPosition(new Position(2,2)));
        assertFalse(pitFallObj.blocksPosition(new Position(3,3)));
        assertFalse(pitFallObj.blocksPosition(new Position(4,4)));
        assertFalse(pitFallObj.blocksPosition(new Position(5,5)));
    }

    @Test
    public void testBlocksPositionGreen(){
        MockRandom newRand = new MockRandom(0);
        Obstacles pitFallObj = new Pitfalls(newRand);
        pitFallObj.getObstacles();
        assertTrue(pitFallObj.blocksPosition(new Position(-100,-100)));
    }


    @Test
    public void testblocksPathRed(){
        MockRandom newRand = new MockRandom(0);
        Obstacles pitFallObj = new Pitfalls(newRand);
        pitFallObj.getObstacles();
        assertFalse(pitFallObj.blocksPath(new Position(-90,-90),new Position(-50,-50)));
        assertFalse(pitFallObj.blocksPath(new Position(-91,-90),new Position(-50,-50)));
        assertFalse(pitFallObj.blocksPath(new Position(-92,-90),new Position(-50,-50)));
        assertFalse(pitFallObj.blocksPath(new Position(-93,-90),new Position(-50,-50)));
        assertFalse(pitFallObj.blocksPath(new Position(-94,-90),new Position(-50,-50)));
    }


    @Test
    public void testblocksPathGreen(){
        MockRandom newRand = new MockRandom(0);
        Obstacles pitFallObj = new Pitfalls(newRand);
        pitFallObj.getObstacles();
        assertTrue(pitFallObj.blocksPath(new Position(-100,-90),new Position(-100,-120)));
        assertTrue(pitFallObj.blocksPath(new Position(-90,-100),new Position(-120,-100)));
        assertTrue(pitFallObj.blocksPath(new Position(-100,-100),new Position(-90,-100)));
        assertTrue(pitFallObj.blocksPath(new Position(-100,-120),new Position(-100,-90)));

    }
}
