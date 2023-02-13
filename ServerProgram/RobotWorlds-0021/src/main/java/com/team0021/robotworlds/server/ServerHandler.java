package com.team0021.robotworlds.server;

import java.lang.Runnable;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team0021.robotworlds.ForColor;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

 
import java.io.*;  


public class ServerHandler {
    
    private static ServerHandler server;

    private Socket clientSocket; 
    private static int portNumber;
    private ServerSocket serverSocket;
    private static Positions positionsObject =  new Positions();
    private HashMap<String, Stack> teams = new HashMap<String,Stack>();
    private static HashMap<CharacterAbstract, Socket> socketNamePair;
    public HashMap<String,CharacterAbstract> playerObjects = new HashMap<String,CharacterAbstract>();


	private PrintStream sender;
    private BufferedReader receiver;

    // response fields
    private static String serverResponse;
    private static HashMap<String, String> result = new HashMap<String, String>();
    private static HashMap<String, Object> data = new HashMap<String, Object>();
    private static HashMap<String, Object> state = new HashMap<String, Object>();
    
    // request fields
    private String characterIdentifier;
    private String teamName;
    private String argument;
    private String command;
    private String teamOne = "";
    private String teamTwo = "";
    private static String worldState = "Available";

    private static String configDir = ".AdminConfig.json";

    private int countActivatedPlayers = 0;

    private int countDeactivatedPlayers = 0;

    private static HashMap<String, Runnable> charIdWordServerPair = new HashMap<String, Runnable>();

    public ServerHandler(){}

    public ServerHandler(InputStream mockInput){System.setIn(mockInput);}

    public static HashMap<String, Runnable> getCharIdWordServerPair(){
        return charIdWordServerPair;
    }

    public static void killClientThread(String charId){
        if(charIdWordServerPair.containsKey(charId)){
            WorldsServer threadObject = (WorldsServer) charIdWordServerPair.get(charId);
            threadObject.setGameState(false);
            charIdWordServerPair.remove(charId);
        }
    }

    public static Positions getWorldObject(){
        return positionsObject; 
    }


    public static String getWorldAvailability(){
        return worldState;
    }


    public static ServerHandler getServerHandler(){
        return server; 
    }


    public int getNumberOfActivePlayers(){
        return Thread.activeCount()-2;
    }


    public int getNumberOfDeActivatedPlayers(){
        return this.countActivatedPlayers - getNumberOfActivePlayers();
    }

    public static void setSocketPair(CharacterAbstract characterObject, Socket clientSocket){
        if(socketNamePair == null){
            socketNamePair = new HashMap<CharacterAbstract, Socket>();
        }
        socketNamePair.put(characterObject, clientSocket);
    }

    public static HashMap<CharacterAbstract, Socket> setSocketPair(){
        return socketNamePair;
    }

    private static void serverSerializeResponse(){

        ObjectMapper mapper = new ObjectMapper();

        ServerDataSerialize dataSerializeObject = new ServerDataSerialize(); 

        try {

            dataSerializeObject.compileString(result,data,state);
            
            serverResponse = mapper.writeValueAsString(dataSerializeObject);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }   
    }



    private void serverDeserializeRequest(BufferedReader receiver){

        ObjectMapper mapper = new ObjectMapper();

        ServerDataDeserialize dataDeserializedObject = null;
        
        try {
            dataDeserializedObject = mapper.readValue(receiver.readLine(), ServerDataDeserialize.class);
            this.teamName = dataDeserializedObject.robotName;
            this.command = dataDeserializedObject.command;
            this.argument = dataDeserializedObject.argument;
            this.characterIdentifier = this.teamName+"."+this.argument;
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e){
            System.out.println("[ServerHandler]: Read a null upon deserialization.\n"+
            "Client program exited incorrectly without using \"off\".");
            this.teamName = "";
            this.command = "off";
            this.argument = "";
            this.characterIdentifier = this.teamName+"."+this.argument;
        }   
    }
    


    private static void serverSendResponseToClient(PrintStream sender){
        sender.println(serverResponse);
    }


    /**
     * Function used to update the fields used for serialization before we send 
     * a response to the user.
     * @param result String object used to update the result field in the result map.
     * @param message String object used to update the message field in the result map.
     * @param status String object used to update the status field in the state map.
    */
    private static void updateFields(String resultObj, String messageObj, String statusObj){
        messageObj =  ForColor.BoldAndWhite.format("Welcome to Robot Wars verification process!\n\nResult: "+
        resultObj+"\n"+messageObj);
        result.put("result",resultObj);
        result.put("message", messageObj);

        data.put("Worldstate", "");
        if(resultObj.equals("FAILED"))
            data.put("Worldstate", worldState);

        state.put("status", statusObj);
    }


   /***
    * Functions used to grant a launch request permission to join the world.
    * It will return a boolean true shoud permission be granted, a boolean false
    * is returned upon failure to meet requirements.
    * @param newClient Socket object
    * @return boolean true, or false
    * @throws IOException if a client connection is interrupted.
   */
    private boolean grantUserPermission(Socket newClient) throws IOException{
        PrintStream sender;
        BufferedReader receiver; 
        boolean permission = true;

        String result = "testing", message = "testing", status = "Ready";
        sender = new PrintStream(newClient.getOutputStream());
        receiver = new BufferedReader(new InputStreamReader(newClient.getInputStream()));
        
        serverDeserializeRequest(receiver);
        String[] verifyTeam = {this.argument,this.teamName};
        
        if(this.command.equals("test connection")){
            result = "testing";
            message = "testing";
            status = "Ready";
            permission =  false;
            
            updateFields(result, message, status);

            serverSerializeResponse();
    
            serverSendResponseToClient(sender);
            
            return permission;
        }
        
        if(permission){
            result = "FAILED";
            if(this.teams.size() <= 2 ){
                Stack<String> teamMates;

                if (!this.teams.containsKey(verifyTeam[1]) && this.teams.size() < 2){
                    teamMates = new Stack<String>();
                    this.teams.put(verifyTeam[1], teamMates);
                }


                if(this.teams.containsKey(verifyTeam[1])){
                    teamMates = this.teams.get(verifyTeam[1]);
                    if(teamMates.size()<4 && !teamMates.contains(verifyTeam[0])){
                        teamMates.add(verifyTeam[0]);
                        this.teams.put(verifyTeam[1], teamMates);
                        result = "OK";
                        message = 
                        ForColor.BoldAndWhite.format("\t - Only two teams are allowed.\n")+
                        ForColor.BoldAndWhite.format("\t - The chosen name for your team is: ")+
                        ForColor.BoldAndGreen.format(this.teamName)+".\n"+
                        ForColor.BoldAndWhite.format("\t - A team is allowed to have one and only one type of character.\n")+
                        ForColor.BoldAndWhite.format("\t - Your character identifier: ")+
                        ForColor.BoldAndGreen.format(this.characterIdentifier)+  
                        ForColor.BoldAndWhite.format(" has been launched into the world successfully.\n\t - Goodluck and enjoy the game!\n\n");
                    }
                    else{
                        message =
                        ForColor.BoldAndWhite.format(" - Unfortunately the character \"")+
                        ForColor.BoldAndRed.format(this.characterIdentifier.replace(this.teamName,"").replace(".",""))+ 
                        ForColor.BoldAndWhite.format("\" has been taken.\n")+
                        ForColor.BoldAndWhite.format(" - Your character identifier: ")+
                        ForColor.BoldAndRed.format(this.characterIdentifier)+
                        ForColor.BoldAndWhite.format(" will not be launched into the world. \n\n")+
                        ForColor.BoldAndWhite.format(" - Tip: Use a different character.\n\n");
                        permission = false;
                    }
                }
                else{
                    String teamNames="";

                    for(String teamname:this.teams.keySet())
                        teamNames += " - "+teamname+"\n";

                    message =
                    ForColor.BoldAndWhite.format(" - The character identifier: ")+
                    ForColor.BoldAndRed.format(this.characterIdentifier)+
                    ForColor.BoldAndWhite.format(" will not be launched into the world.\n")+
                    ForColor.BoldAndWhite.format(" - The teams available are:\n")+
                    ForColor.BoldAndGreen.format(teamNames)+"\n"+
                    ForColor.BoldAndWhite.format(" - Tip: Try another team.\n\n");
                    permission = false;
                }
            }
            else {
                message =
                ForColor.BoldAndWhite.format(" - The character identifier: ")+
                ForColor.BoldAndRed.format(this.characterIdentifier)+
                ForColor.BoldAndWhite.format(" will not be launched into the world.\n")+
                ForColor.BoldAndWhite.format(" - This world has reached the maximum number of players.\n")+
                ForColor.BoldAndWhite.format(" - Tip: Don't try again soon.\n\n");
                permission = false;
            }

        }

        updateFields(result, message, status);

        serverSerializeResponse();

        serverSendResponseToClient(sender);


        return permission;
    }

    public static HashMap<CharacterAbstract, Socket> getSocketNamePair(){
        return socketNamePair;
    }

    public static void setSocketNamePair(HashMap<CharacterAbstract, Socket> socketNamePairObj){
        socketNamePair = socketNamePairObj;
    }

    public static void broadcastToEveryoneUsing(String messageForAll){
        
        for (CharacterAbstract charObj: getSocketNamePair().keySet()){
            result = charObj.getCharacterResult();
            state = charObj.getCharacterState();
            data = charObj.getCharacterData();
            result.put("message", "[INCOMMING]"+messageForAll);
            data.put("Worldstate", worldState);
            Socket clientSocket = socketNamePair.get(charObj);
            PrintStream sender;
            try {
                sender = new PrintStream(clientSocket.getOutputStream());
                serverSerializeResponse();
                serverSendResponseToClient(sender);
            } catch (IOException e) {
                ForColor.printThis("\nClient connection got interrupted in ServerHandler.broadcastToEveryoneUsing: line 294",false);
            }
        }        
    }
    
    public static void directCommunicationToUser(CharacterAbstract player, String message){
        
        for (CharacterAbstract charObj: getSocketNamePair().keySet()){
            if (charObj.equals(player)){

                result = charObj.getCharacterResult();
                state = charObj.getCharacterState();
                data = charObj.getCharacterData();
                result.put("message", message);
                data.put("Worldstate", worldState);
                charObj.setCharacterResult(result); //optional
                charObj.setCharacterData(data);// optional
                Socket clientSocket = socketNamePair.get(charObj);
                PrintStream sender;
                try {
                    sender = new PrintStream(clientSocket.getOutputStream());
                    serverSerializeResponse();
                    serverSendResponseToClient(sender);
                } catch (IOException e) {

                    ForColor.printThis("\nClient connection got interrupted in ServerHandler.broadcastToEveryoneUsing: line 294",false);
                }
            }
        }
    }
    
    public static void broadCastToTeamOnly(String teamName, String messageForAll) {
        for (CharacterAbstract charObj: getSocketNamePair().keySet()){
            String charId = charObj.getCharacterIdentifier();
            if(charId.contains(teamName)){
                result = charObj.getCharacterResult();
                state = charObj.getCharacterState();
                data = charObj.getCharacterData();
                result.put("message", "[INCOMMING]"+messageForAll);
                data.put("Worldstate", worldState);
                Socket clientSocket = socketNamePair.get(charObj);
                PrintStream sender;
                try {
                    sender = new PrintStream(clientSocket.getOutputStream());
                    serverSerializeResponse();
                    serverSendResponseToClient(sender);
                } catch (IOException e) {
                    
                    ForColor.printThis("\nClient connection got interrupted in ServerHandler.broadcastToEveryoneUsing: line 294",false);
                }
            }
        }
    }


    private void start(int port) throws IOException{
        
        this.serverSocket = new ServerSocket(port);
        
        Runnable serverListener = new BackgroundTasks("serverside");
        Thread backgroundTask = new Thread(serverListener);
        backgroundTask.start();
        
        while (true){
            this.clientSocket = this.serverSocket.accept();
            
            if(grantUserPermission(this.clientSocket)){
                //Thread created for each client when permission to enter the world is granted
                Runnable runnableObject = new WorldsServer(this.clientSocket, this.characterIdentifier);
                Thread newThread = new Thread(runnableObject);
                charIdWordServerPair.put(this.characterIdentifier, runnableObject);
                this.countActivatedPlayers += 1;
                newThread.start();
                String messageForAll = " "+ForColor.BoldAndGreen.format("["+this.characterIdentifier+"]")+
                ForColor.BoldAndWhite.format(" just joined the world. Yay!");
                broadcastToEveryoneUsing(messageForAll);

            }   
            if(getNumberOfActivePlayers()<1 && !(this.countActivatedPlayers <  7)){
                System.out.println(ForColor.JustWhite.format("Game has no players alive."));
                break;
            }
        }
    }


    private void stop() throws IOException{
        this.clientSocket.close();
        this.serverSocket.close();
    }


    public static String getConnectionInfo() throws SocketException, UnknownHostException{
        return retrieveIP(portNumber);
    }


    //Gets the interface for a LAN/WAN connection and displays the IP Address for that interface
    public static String displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        Enumeration<InetAddress> inetAddresses =  netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            //Validates if the IP Address is one in the correct format
            if(validateIP(inetAddress.getHostAddress())){
                return inetAddress.getHostAddress();
            }
        }
        return ForColor.BoldAndRed.format("NETWORK IP ADDRESS NOT FOUND");
    }

    //Gets the network interface of specified type and passes the list to displayInterfaceInformation
    public static String retrieveIP(int portNumber) throws SocketException, UnknownHostException{
        String networkIP = "";
        try {
            //Get access to 'vpn' - random generated interface only linux
            NetworkInterface nets = NetworkInterface.getByName("ztwdjeh7sp");
            //Gets and displays your network ip address
            networkIP = displayInterfaceInformation(nets);
        } catch (Exception e) {
            try {
                //Get access to 'en0' - wireless interface only mac
                NetworkInterface nets = NetworkInterface.getByName("en0");
                networkIP = displayInterfaceInformation(nets);
            } catch (Exception e1) {
                try {
                    //Get access to 'en1' - ethernet(lan) interface only mac
                    NetworkInterface nets = NetworkInterface.getByName("en1");
                    networkIP = displayInterfaceInformation(nets);                    
                } catch (Exception e2) {
                    try {
                        //Get access to 'eno1' - ethernet(lan) interface only linux
                        NetworkInterface nets = NetworkInterface.getByName("eno1");
                        networkIP = displayInterfaceInformation(nets);
                    } catch (Exception e3) {
                        try {
                            //Get access to 'wlo1' - wireless interface only linux
                            NetworkInterface nets = NetworkInterface.getByName("wlo1");
                            networkIP = displayInterfaceInformation(nets);    
                        } catch (Exception e4) {
                            //Default to the loca; ip address
                            InetAddress address = InetAddress.getLocalHost();
                            networkIP = address.getHostAddress();
                        }
                    }
                }
            }
        }
        return ForColor.JustWhite.format("IP_ADDRESS : PORT -> " + networkIP + ":" + portNumber+".");
    }

    
    //Validate IP Address
    public static boolean validateIP(String ipAddress){
        String zeroTo255= "(\\d{1,2}|(0|1)\\d{2}|2[0-4]\\d|25[0-5])";
        String regex = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;
        Pattern myPattern = Pattern.compile(regex);
        Matcher myMatch = myPattern.matcher(ipAddress);

        return myMatch.matches();
    }


    private static void readPortNumber(){
        JSONParser jsonParser = new JSONParser();
        
        try (FileReader reader = new FileReader(configDir))
        {
            JSONObject fileJson = (JSONObject) jsonParser.parse(reader);

            long readPortNumber = (long) fileJson.get("port");

            portNumber = (int) readPortNumber;

            retrieveIP(portNumber);
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        readPortNumber();
        
        try{
            server = new ServerHandler();

            server.start(portNumber);

            server.stop();
        }
        catch(IOException e){
            System.out.println(ForColor.BoldAndPurple.format("[ServerHander]:")+
            ForColor.BoldAndRed.format("Socket connection interrupted.\n"+
            "Failed to execute goal.  - Please check if the server program is running anywhere else."));
        } 
    
    }

}
