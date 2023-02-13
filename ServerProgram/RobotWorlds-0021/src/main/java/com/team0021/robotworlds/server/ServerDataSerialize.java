package com.team0021.robotworlds.server;

import java.util.HashMap;

public class ServerDataSerialize {

    public HashMap<String, String> result;
    public HashMap<String, Object> data;
    public HashMap<String, Object> state;

    public void compileString(HashMap<String,String> result2 ,HashMap<String, Object> data, HashMap<String, Object> state)
    {
        this.result = result2; // Message on success
        this.data = data; // Map encapsulated as a string 
        this.state = state; // Map encapsulated as a string   
    }

}
