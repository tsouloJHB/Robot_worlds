package com.team0021.robotworlds.worldTest;

import com.team0021.robotworlds.server.ServerHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Server {
    private final PrintStream standardOut = System.out;
    private final InputStream standardIn = System.in;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        // System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
        System.setIn(standardIn);
    }

    @Test
    public void testGreeting(){
        InputStream mockedInput = new ByteArrayInputStream("off\n".getBytes());
        ServerHandler server = new ServerHandler(mockedInput);
        String[] args = {"1", "2"};
        ServerHandler.main(args);
        System.out.println("asdf");
        String actualOutput = "outputStreamCaptor.toString().trim().split()";
        String expectedOutput = "";
        assertEquals("expectedOutput","actualOutput");
    }

}
