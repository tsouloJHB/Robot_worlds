package com.team0021.robotworlds.client;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team0021.robotworlds.ForColor;

public class UserInput implements Runnable{
    private PrintStream sendData;
    private Scanner scannerObject; 


    public UserInput(PrintStream sendData){
        this.sendData = sendData;
        this.scannerObject = new Scanner(System.in);
    }
 
    // response fields
    private HashMap<String, Object> state = new HashMap<>();
    
    // request fields
    private String robotName; 
    private String argument;
    private String command;


    // Serialized message for server
    private String requestForServer;



    private String getUserInput(){
        String userIn = "";
        while(userIn.length() < 1){
            userIn = this.scannerObject.nextLine(); 
        }
        return userIn;
    }


    public void sendMessage() throws IOException{
        this.sendData.println(this.requestForServer);
    }


    private void clientSerializeRequest() throws IOException{
        DataSerialize clientData = new DataSerialize();
        ObjectMapper mapper = new ObjectMapper();
        clientData.compileString(this.robotName, this.command, this.argument);
        this.requestForServer = mapper.writeValueAsString(clientData);      
    }


    @Override
    public void run() {
        try{
            this.command = "";
            do {
                this.command = getUserInput();
                this.argument = "";
                String[] lookForArguments = this.command.split(" ", 2);
                if (lookForArguments.length == 2) {
                    this.command = lookForArguments[0];
                    this.argument = lookForArguments[1];
                }

                clientSerializeRequest();

                sendMessage();

                state = RobotClient.getState();
                
                if (state.get("status").equals("EXIT")||
                    state.get("status").equals("DIED") )
                    break;

            }while(true);
        } 
        catch (IOException e) 
        {ForColor.printThis("\n"+ForColor.BoldAndBlue.format("[RobotClient]: " )+ForColor.warning.format("Server connection cannot be found.\n"), true);}
        catch (NullPointerException e) 
        {ForColor.printThis("\n"+ForColor.BoldAndBlue.format("[RobotClient]: ")+ForColor.warning.format("Sorry, server ended the session.\n"), true);}
        this.scannerObject.close();
        System.exit(0);
    }
}
