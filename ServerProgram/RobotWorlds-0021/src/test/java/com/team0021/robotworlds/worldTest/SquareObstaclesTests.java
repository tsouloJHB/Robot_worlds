package com.team0021.robotworlds.worldTest;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;
import java.util.Stack;

import com.team0021.robotworlds.server.world.Obstacles;
import com.team0021.robotworlds.server.world.Position;
import com.team0021.robotworlds.server.world.SquareObstacles;

import org.junit.Test;

public class SquareObstaclesTests {
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
        Obstacles obstacleObj = new SquareObstacles(newRand);
        Stack<Position> expectedPitList = new Stack<Position>();
        expectedPitList.add(new Position(5,5));
        Stack<Position> actualPitList = obstacleObj.getObstacles();
        assertFalse(expectedPitList.get(0).equals(actualPitList.get(0)));
        assertFalse(actualPitList.size()>1);
   }

    @Test
    public void testGetPitFallListGreen()
    {
        MockRandom newRand = new MockRandom(0);
        Obstacles obstacleObj = new SquareObstacles(newRand);
        Stack<Position> expectedPitList = new Stack<Position>();
        expectedPitList.add(new Position(-100,-100));
        Stack<Position> actualPitList = obstacleObj.getObstacles();
        assertTrue(expectedPitList.get(0).equals(actualPitList.get(0)));
        assertTrue(actualPitList.size()==1);
    }


    @Test
    public void testBlocksPositionRed(){
        MockRandom newRand = new MockRandom(0);
        Obstacles obstacleObj = new SquareObstacles(newRand);
        obstacleObj.getObstacles();
        assertFalse(obstacleObj.blocksPosition(new Position(1,1)));
        assertFalse(obstacleObj.blocksPosition(new Position(2,2)));
        assertFalse(obstacleObj.blocksPosition(new Position(3,3)));
        assertFalse(obstacleObj.blocksPosition(new Position(4,4)));
        assertFalse(obstacleObj.blocksPosition(new Position(5,5)));
    }

    @Test
    public void testBlocksPositionGreen(){
        MockRandom newRand = new MockRandom(0);
        Obstacles obstacleObj = new SquareObstacles(newRand);
        obstacleObj.getObstacles();
        assertTrue(obstacleObj.blocksPosition(new Position(-100,-100)));
        assertTrue(obstacleObj.blocksPosition(new Position(-99,-99)));
        assertTrue(obstacleObj.blocksPosition(new Position(-98,-98)));
        assertTrue(obstacleObj.blocksPosition(new Position(-97,-97)));
        assertTrue(obstacleObj.blocksPosition(new Position(-96,-96)));
    }


    @Test
    public void testblocksPathRed(){
        MockRandom newRand = new MockRandom(0);
        Obstacles obstacleObj = new SquareObstacles(newRand);
        obstacleObj.getObstacles();
        assertFalse(obstacleObj.blocksPath(new Position(-90,-90),new Position(-50,-50)));
        assertFalse(obstacleObj.blocksPath(new Position(-91,-90),new Position(-50,-50)));
        assertFalse(obstacleObj.blocksPath(new Position(-92,-90),new Position(-50,-50)));
        assertFalse(obstacleObj.blocksPath(new Position(-93,-90),new Position(-50,-50)));
        assertFalse(obstacleObj.blocksPath(new Position(-94,-90),new Position(-50,-50)));
    }


    @Test
    public void testblocksPathGreen(){
        MockRandom newRand = new MockRandom(0);
        Obstacles obstacleObj = new SquareObstacles(newRand);
        obstacleObj.getObstacles();
        assertTrue(obstacleObj.blocksPath(new Position(-100,-90),new Position(-100,-120)));
        assertTrue(obstacleObj.blocksPath(new Position(-90,-100),new Position(-120,-100)));
        assertTrue(obstacleObj.blocksPath(new Position(-100,-100),new Position(-90,-100)));
        assertTrue(obstacleObj.blocksPath(new Position(-100,-120),new Position(-100,-90)));

    }
}
