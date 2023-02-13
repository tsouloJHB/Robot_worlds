package com.team0021.robotworlds.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.AllRounder;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;
import com.team0021.robotworlds.server.world.Characters.Defender;
import com.team0021.robotworlds.server.world.Characters.Scout;
import com.team0021.robotworlds.server.world.Characters.Sniper;
import com.team0021.robotworlds.server.world.Commands.Command;

public class WorldsServer implements Runnable {
	private Socket clientSocket;
	private PrintStream sender;
    private BufferedReader receiver;


    // response fields
    private String serverResponse;
    private HashMap<String, String> result = new HashMap<String, String>();
    private HashMap<String, Object> data = new HashMap<String, Object> ();
    private HashMap<String, Object> state = new HashMap<String, Object>();
    
    // request fields
    private String clientRequest;
    private String robotName; 
    private String argument;
    private String command;

    private Positions worldObject;

    private String teamName;
    private String characterType; 
    private CharacterAbstract characterObject;
    private String characterIdentifier;
    public List<CharacterAbstract> characterObjects = new ArrayList<CharacterAbstract>();

    private boolean gameRunningState = true;


	public WorldsServer(Socket clientSocketArgument, String characterIdentifier){
        
        this.clientSocket = clientSocketArgument;
        this.characterType = characterIdentifier.substring(characterIdentifier.indexOf(".")+1);
        this.teamName = characterIdentifier.substring(0,characterIdentifier.indexOf("."));
        this.characterIdentifier = this.teamName+"."+this.characterType;
        this.worldObject = ServerHandler.getWorldObject();
        initializeWorldForThread();
	}

    private CharacterAbstract createNewCharacter(String characterIdentifier){
        if (characterIdentifier.contains("sniper"))
            return new Sniper(characterIdentifier);

        else if (characterIdentifier.contains("allrounder"))
            return new AllRounder(characterIdentifier);
       
        else if (characterIdentifier.contains("defender"))
            return new Defender(characterIdentifier);
    
        return new Scout(characterIdentifier);
    }

    public void setGameState(boolean condition){
        this.gameRunningState = condition;
    }

    private void initializeWorldForThread(){
        this.characterObject = createNewCharacter(this.characterIdentifier);

        this.worldObject.launchCharacter(this.characterObject);
        
        ServerHandler.setSocketPair(this.characterObject, this.clientSocket);

        this.result = this.characterObject.getCharacterResult();

        this.data = this.characterObject.getCharacterData();

        this.state =this.characterObject.getCharacterState();
       
    }


    private void serverRecieveRequest() throws IOException{
        this.clientRequest = this.receiver.readLine();
    }


    private void serverDeserializeRequest() throws IOException{

        ObjectMapper mapper = new ObjectMapper();

        ServerDataDeserialize dataSerializeObject;

        try {
            dataSerializeObject = mapper.readValue(this.clientRequest, ServerDataDeserialize.class);
            this.characterIdentifier = dataSerializeObject.robotName;
            this.command = dataSerializeObject.command;
            this.argument = dataSerializeObject.argument;
        } catch (JsonMappingException e) {
            e.printStackTrace();
            gameRunningState = false;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            gameRunningState = false;
        } 
        catch (IllegalArgumentException e){
            System.out.println("[WorldsServer]: Read a null upon deserialization.\n"+
            "Client program exited incorrectly without using \"off\".");
            this.characterIdentifier = "";
            this.command = "off";
            this.argument = "";
        }    
    }
    

    private void serverDoCommand(){
        try{
            Command command = Command.create(this.command.toLowerCase()+" "+this.argument.toLowerCase());
            gameRunningState = command.execute(this.characterObject);
            this.data.put("Worldstate", ServerHandler.getWorldAvailability());
        }
        catch (IllegalArgumentException e){
            this.result.put("message", e.getLocalizedMessage());
            this.result.put("result", "FAILED");

        }
        catch (NullPointerException e){
            System.out.println("[WorldsServer]: Read a null upon in serverDocommand.\n"+
            "Client program exited incorrectly without using \"off\".");
            gameRunningState = false;           
        }
    }
 

    private void serverSerializeResponse(){
        
        ObjectMapper mapper = new ObjectMapper();
        
        ServerDataSerialize dataSerializeObject = new ServerDataSerialize();
        
        try {

            dataSerializeObject.compileString(this.result, this.data,this.state);
        
            this.serverResponse = mapper.writeValueAsString(dataSerializeObject);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }  
    }
    

    private void serverSendResponseToClient(){
        this.sender.println(this.serverResponse);
    }


    private void closeRunningForClientThread() throws IOException{
        this.receiver.close();
		this.sender.close();
        this.clientSocket.close();
    }


    private void implementRobotsWorldForClientThread()throws IOException{
        while(gameRunningState) {

            serverRecieveRequest();

            if(!this.gameRunningState){
                break;
            }

            serverDeserializeRequest();

            serverDoCommand();
            
            serverSerializeResponse();
            
            serverSendResponseToClient();
            
        } 
        HashMap<CharacterAbstract, Socket> socketPair = ServerHandler.getSocketNamePair();
        if(socketPair.containsKey(characterObject)){
            socketPair.remove(characterObject);
        }
        ServerHandler.setSocketNamePair(socketPair);
        ServerHandler.broadcastToEveryoneUsing(" "+this.teamName+"."+
        this.characterType+" just got out.");
        System.out.println("[WorldsServer]: "+this.teamName+"."+
                        this.characterType+" got out.");
    }


    private void initilializeSocketForClient()throws IOException{
        this.sender = new PrintStream(clientSocket.getOutputStream());
        this.receiver = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
    }


    @Override
	public void run(){
		try{
            initilializeSocketForClient();
            
            implementRobotsWorldForClientThread();

            closeRunningForClientThread();

	    }
	    catch (IOException e){
            System.out.println("[WorldsServer]: Socket connection interrupted: \""+     
                    this.characterIdentifier+"\" thread ended.");
        }
	}

}