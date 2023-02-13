package com.team0021.robotworlds.worldTest;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.team0021.robotworlds.server.world.Obstacles;
import com.team0021.robotworlds.server.world.Pitfalls;
import com.team0021.robotworlds.server.world.Position;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.SquareObstacles;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class TestWorld {

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
    @DisplayName("Testing pitfalls red")
    public void testPitFallsRed()
    {
        Random newMock = new MockRandom(101);

        Position newPosition =  new Position(100,100);

        Obstacles newPitObstacles = new Pitfalls(newMock); 

        newPitObstacles.getObstacles();
        assertFalse(newPitObstacles.blocksPosition(newPosition));
        

        assertFalse(newPitObstacles.blocksPath(newPosition, new Position(1, 100)));

    }


    @Test
    @DisplayName("Testing pitfalls green")
    public void testPitFallsGreen()
    {
        Random newMock = new MockRandom(101);

        Position newPosition =  new Position(1,1);

        Obstacles newPitObstacles = new Pitfalls(newMock); 

        newPitObstacles.getObstacles();

        assertEquals(1, newPitObstacles.getObstacles().get(0).getX());

        assertEquals(1, newPitObstacles.getObstacles().get(0).getY());
        
        assertTrue(newPitObstacles.blocksPosition(newPosition));

        assertTrue(newPitObstacles.blocksPath(new Position(1, -1), new Position(1, 10)));

    }


    @Test
    @DisplayName("Testing obstacles red")
    public void testObstaclesRed()
    {
        Random newMock = new MockRandom(101);

        Position newPosition =  new Position(1001,1);

        Obstacles newObstacle = new SquareObstacles(newMock); 

        newObstacle.getObstacles();

        assertFalse(newObstacle.blocksPosition(newPosition));

        assertFalse(newObstacle.blocksPath(newPosition, new Position(1,-1006)));

    }


    @Test
    @DisplayName("Testing obstacles green")
    public void testObstaclesGreen()
    {
        Random newMock = new MockRandom(101);

        Position newPosition =  new Position(1,1);

        Obstacles newObstacle = new SquareObstacles(newMock); 

        newObstacle.getObstacles();

        assertEquals(1, newObstacle.getObstacles().get(0).getX());

        assertEquals(1, newObstacle.getObstacles().get(0).getY());
        
        assertTrue(newObstacle.blocksPosition(newPosition));

        assertTrue(newObstacle.blocksPath(new Position(1, -1), new Position(1, 10)));

    }


    @Test
    @DisplayName("Testing world setup")
    public void testWorldSetUp(){
        MockRandom mockSeed = new MockRandom(50);

        Positions worldObjects = new Positions(mockSeed);
        assertEquals(new Position(-100,200),worldObjects.getworldTopLeftCorner());
        assertEquals(new Position(100,-200),worldObjects.getworldBottomRightCorner());
        assertEquals(400, worldObjects.getworldLength());
        assertEquals(200, worldObjects.getworldWidth());
    }
}
