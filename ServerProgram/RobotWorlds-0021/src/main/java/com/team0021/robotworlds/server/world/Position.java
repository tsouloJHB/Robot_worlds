package com.team0021.robotworlds.server.world;

/**
 * Class provides implementation for the Position class. It is used to 
 * encapsulate x, y values and indicate where two objects of the class are equal.
 * @author imogano
*/
public class Position {
    private final int x;
    private final int y;


    /**
     * Class constructor invoked using x, y coordinates/values.
     * @param x coordinate
     * @param y coordinate
    */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }


    /**
     * Function used to retrieve the x value of a Position object.
     * @return x coordinate
     */ 
    public int getX() {
        return x;
    }


    /**
     * Function used to retrieve the y value of a Position object.
     * @return y coordinate
     */ 
    public int getY() {
        return y;
    }


    /**
     * Function used to compare two objects of the Position classs instance.
     * @param Generic object 
     * @return Boolean condition; true if equal and false on else
     */ 
    @Override
    public boolean equals(Object o) {
        Position position = (Position) o;
        boolean xcondition = this.x == position.getX();
        boolean ycondition = this.y == position.getY();
        return xcondition && ycondition;

    }


    /**
     * Tests whether the x,y coordinate of the class fall within the constrained
     * area.
     * @param topLeft
     * @param bottomRight
     * @return Boolean condition; true if equal and false on else
    */
    public boolean isIn(Position topLeft, Position bottomRight) {
        boolean withinTop = this.y <= topLeft.getY();
        boolean withinBottom = this.y >= bottomRight.getY();
        boolean withinLeft = this.x >= topLeft.getX();
        boolean withinRight = this.x <= bottomRight.getX();
        return withinTop && withinBottom && withinLeft && withinRight;
    }
}


