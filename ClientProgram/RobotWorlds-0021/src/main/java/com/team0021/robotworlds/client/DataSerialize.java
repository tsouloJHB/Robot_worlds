package com.team0021.robotworlds.client;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataSerialize implements Serializable {
    public String robotName;
    public String command;
    public String argument;


    public void compileString(String robotName ,String command, String argument)
    {
        this.robotName = robotName;
        this.command = command;
        this.argument = argument;    
    }
    
}
