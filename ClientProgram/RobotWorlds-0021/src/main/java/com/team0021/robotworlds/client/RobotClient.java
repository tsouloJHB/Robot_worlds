package com.team0021.robotworlds.client;

import java.lang.Runnable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team0021.robotworlds.ForColor;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.InetSocketAddress;

public class RobotClient {

    private Socket clientSocket;
    private PrintStream sendData;
    private BufferedReader serverResponseData;

    private String robotName; 
    private String argument;
    private String command;
    
    private String requestForServer;
    
    private String serverResponse;
    
    private String characterClass;
    
    private JSONObject jsonObject = new JSONObject();
    
    private final DataSerialize clientData = new DataSerialize();
    
    private static HashMap<String, Object> data = new HashMap<>();
    private static HashMap<String, Object> state = new HashMap<>();
    private static HashMap<String, String> result = new HashMap<>();
    
    private static final String roboClientStr = "[RobotClient]: ";
    
    private static final String configDir = ".RobotClientConfig.json";

    public void startConnection(String ip, int port, int timeout) throws IOException{
        SocketAddress serverAddress = new InetSocketAddress(ip, port);
        clientSocket = new Socket();
        clientSocket.connect(serverAddress, timeout);
        sendData = new PrintStream(clientSocket.getOutputStream(), true);
        serverResponseData = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }


    public void startConnection(String ip, int port) throws IOException{
        clientSocket = new Socket(ip, port);
        sendData = new PrintStream(clientSocket.getOutputStream(), true);
        serverResponseData = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }


    public void stopConnection() throws IOException{
        serverResponseData.close();
        sendData.close();
        clientSocket.close();
    }


    private void clientSerializeRequest() throws IOException{

        this.clientData.compileString(this.robotName, this.command, this.argument);

        ObjectMapper mapper = new ObjectMapper();
        
        this.requestForServer = mapper.writeValueAsString(this.clientData);      
    }


    public void sendMessage() throws IOException{
        sendData.println(this.requestForServer);
    }


    public void receiveMessage() throws IOException{
        this.serverResponse = this.serverResponseData.readLine();
    }


    public static void updateGameFields(HashMap<String, String> resultObj, 
                                    HashMap<String, Object> stateObj, 
                                    HashMap<String, Object> dataObj){
        result = resultObj;
        data = dataObj;       
        state = stateObj; 
    }


    private void clientDeserializeResponse(){
        // what you need is in this.serverResponse
        try {
            ObjectMapper mapper = new ObjectMapper();

            DataDeserialize serverData = mapper.readValue(this.serverResponse, DataDeserialize.class);
        
            updateGameFields(serverData.result,serverData.state, serverData.data);
            
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            System.out.println(ForColor.BoldAndBlue.format(roboClientStr)+ ForColor.fail.format("No Response Recorded From Server World - Server Is Offline."));
        }  
    }


    public String doMenu(){
        return
            "\nHey Soldier, Are you ready to go to war? Below are some commands to get you started:\n\n"+
            ForColor.menuItem.format("launch [role] [name]:") +ForColor.JustWhite.format("\tStart the game and launch into the world. To launch\n"+
                                    "\t\t\tinto the world you must specify the class and name\n"+
                                    "\t\t\tof your robot. E.g. launch sniper Potato007.\n\n")+
            ForColor.menuItem.format("controls:")+ ForColor.JustWhite.format("   Read up on some in-game commands/controls e.g. reload, mine, fire, look,"+
                                    "\n\t    turn right,turn left, orientation, state, etc.\n\n"+
            ForColor.menuItem.format("roles:")+
            ForColor.JustWhite.format("\tRead more about robot classes that suit your playstyle.\n\n")+
            ForColor.menuItem.format("reset:")+
            ForColor.JustWhite.format("\tReset world server connection.\n\n")+
            ForColor.menuItem.format("info:")+
            ForColor.JustWhite.format("\tView world server connection.\n\n")+
            ForColor.menuItem.format("quit:")+
            ForColor.JustWhite.format("\tClose the game.\n\n")+
            ForColor.menuItem.format("menu:")+
            ForColor.JustWhite.format("\tTo launch this menu again.\n"));
    }


    public String doRoles(){
        return
            "\nHere are the robot roles you can choose from - which role that suits your unique playstyle?\n\n"+
            ForColor.menuItem.format("All-Rounder:")+
            "\n\t\tA well balanced player for those who seek the thrill, the all rounder has a bit of everything,\n\t\t"+
            "a much needed player towards end game and those clutch moments that\n\t\twe all know too well.\n"+
            ForColor.menuItem.format("Defender")+
            "\n\t\tThe defender might as well be the ultimate defense for your team, as normal hits from bullets\n\t\t"+
            "and mines don't seem to faze them much at all, use his strength to help your team\n"+
            "\t\tgain major advantage in the gain by protecting them from mines and hits from other players.\n\t\t"+
            "But take note he is slower than most players in the game.\n"+
            ForColor.menuItem.format("Sniper:")+
            "\n\t\tThe sniper has a greater range view than most other players in the game, although his bullets\n\t\t"+
            "are numbered a single shot from the scout is enough to cut down half your health\n"+
            "\t\tand if you're unlucky a single shot can land a major kill, so choose your position wisely\n\t\t"+
            "cause just as scouts, snipers do take on more damage than most players in the game.\n"+
            ForColor.menuItem.format("Scout:")+
            "\n\t\tGain a major advantage towards your team - The scout has super algorithm that\n\t\t"+
            "allows him to scan the entire map and share his findings to the entire team.\n"+
            "\t\tbut such power comes with great risk, if the scout happens to\n\t\twalk over a mine whilst mining he's as good as dead.\n";
    }


    public String doControls(){
        return
            "\nHere are the general robot commands available for every robot:\n\n"+
            ForColor.menuItem.format("off") +ForColor.JustWhite.format("\t\t- Shut down robot\n")+ 
            ForColor.menuItem.format("help")+ForColor.JustWhite.format("\t\t- provide information about commands\n")+
            ForColor.menuItem.format("forward")+ForColor.JustWhite.format("\t\t- move forward by specified number of steps, e.g. 'FORWARD 10'\n")+
            ForColor.menuItem.format("back")+ ForColor.JustWhite.format("\t\t- move backward by specified number of steps, e.g. 'BACK 10'\n")+
            ForColor.menuItem.format("turn right")+ForColor.JustWhite.format("\t- turn right by 90 degrees\n")+
            ForColor.menuItem.format("turn left")+ForColor.JustWhite.format("\t- turn left by 90 degrees\n")+
            ForColor.menuItem.format("sprint")+ForColor.JustWhite.format("\t\t- sprint forward according to a formula\n")+
            ForColor.menuItem.format("special")+ForColor.JustWhite.format("\t\t- unique special commands for your character/role will be explained in-game.\n");
    }


    //Reset config information
    public boolean doResetClientConfig(){
        
        File myConfigFile = new File(configDir); 
        
        if (myConfigFile.delete()){ 
          System.out.println(ForColor.BoldAndBlue.format(roboClientStr) + ForColor.pass.format("RESET COMPLETE!"));
        } else {
          System.out.println(ForColor.BoldAndBlue.format(roboClientStr) + ForColor.fail.format("RESET FAILED! - CONNECTION DOESN'T EXIST"));
        }
        return false;
    }


    //Reset config information
    public String doReadClientConfig(){
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(configDir))
        {
            JSONObject fileJson = (JSONObject) jsonParser.parse(reader);
            String readIpAddress = (String) fileJson.get("ipAddress");
            long readPortNumber = (long) fileJson.get("port");

            return "\n"+ForColor.info.format("CLIENT INFO - ") + ForColor.info2.format(readIpAddress + ":" + readPortNumber + "\n");

        } catch (IOException | ParseException e) {
            System.out.println("\n"+ForColor.BoldAndBlue.format(roboClientStr)+ForColor.BoldAndRed.format("CONNECTION DOESN'T EXIST! - RESTART PROGRAM"));
        }
        return "";
    }    


    public boolean verifyRoleSelected(String[] launchArgs){
        String role = launchArgs[1];
        if(role.equalsIgnoreCase("sniper") || role.equalsIgnoreCase("scout") 
        || role.equalsIgnoreCase("defender") || role.equalsIgnoreCase("allrounder")){ 
            return true;
        }
        System.out.println(ForColor.BoldAndBlue.format(roboClientStr) + ForColor.warning.format("Please enter valid role."));
        return false;  
    }


    public boolean verifyTeamName(String[] launchArgs){
        String teamName = launchArgs[2];
        String regex = "^[0-9]*[a-zA-Z][a-zA-Z0-9]*$"; //letters and numbers
        // String regex = "[a-zA-Z]+"; //letters only
        Pattern myPattern = Pattern.compile(regex);
        Matcher myMatch = myPattern.matcher(teamName);
        
        if(myMatch.matches() && teamName.length()>=5 && teamName.length() <= 10){
            return true;
        }
        System.out.println(ForColor.BoldAndBlue.format(roboClientStr) + ForColor.warning.format("Please ensure that your team name has "+
                        "min 5 and max 10 characters "+
                        "and does not contain\n\t       "+
                        "any special characters."));
        return false;
    }


    public static HashMap<String, String> getResult(){
        return result;
    }


    public static HashMap<String, Object> getState(){
        return state;
    }


    public static HashMap<String, Object> getData(){
        return data;
    }


    public boolean greetUser(String ipAddressString, int portNumber) throws IOException{
        Scanner scannerInput = new Scanner(System.in); 
        String userInput;
        printLogo();
        System.out.println(ForColor.JustWhite.format(doMenu()));
        while(true)
        {
            System.out.print(ForColor.BoldAndBlue.format(roboClientStr) + ForColor.JustWhite.format("Enter a valid command: "));

            userInput = scannerInput.nextLine().trim().toLowerCase();

            if (userInput.equals("quit") || userInput.equals("exit") || userInput.equals("off")){
                System.out.println(ForColor.BoldAndBlue.format(roboClientStr) + ForColor.pass.format("Shutting down..."));
                return false;
            }

            if (userInput.equalsIgnoreCase("menu")){
                System.out.println(ForColor.JustWhite.format(doMenu()));
            }
            if (userInput.equalsIgnoreCase("roles")){
                System.out.println(ForColor.JustWhite.format(doRoles()));
            }
            if (userInput.equalsIgnoreCase("controls")){
                System.out.println(ForColor.JustWhite.format(doControls()));
            }
            if (userInput.equalsIgnoreCase("reset")){
                return doResetClientConfig();
            }
            if (userInput.equalsIgnoreCase("info")){
                System.out.println(doReadClientConfig());
            }

            String[] inputArr = userInput.split(" ");

            while (inputArr.length == 3 && inputArr[0].equals("launch") && verifyRoleSelected(inputArr) && verifyTeamName(inputArr)){
                int timeout = 3000;
                try{
                    startConnection(ipAddressString,portNumber, timeout);
                    this.characterClass = inputArr[1];
                    this.robotName = inputArr[2];
                    this.command = "launch";
                    this.argument = this.characterClass;
                    clientSerializeRequest();
                    sendMessage();

                    receiveMessage();

                    clientDeserializeResponse();
                
                    ForColor.printThis("\n\n\n"+getResult().get("message"), true);

                    if (getResult().get("result").equals("OK"))
                        return true;

                    System.out.println(ForColor.BoldAndBlue.format(roboClientStr) + ForColor.info2.format("Restart the game and try again."));

                    System.exit(0);
                }catch(SocketTimeoutException e){
                    // Return false if connection fails
                    System.out.println("\n"+ForColor.BoldAndBlue.format(roboClientStr)+ForColor.fail.format("Connection Timed Out."));
                    {ForColor.printThis("\n"+ForColor.BoldAndBlue.format(roboClientStr)
                    +ForColor.BoldAndYellow.format("Unable to connect to the world, please ensure the following -> "+
                            "\n1. World Server is online.\n2. World Server info is correct.\n"), true);}
                    System.exit(0);
                }
            }
        }
    }


    public void ServerCommunication(){
        String characterIdentifier = this.robotName+"."+this.characterClass;

        Runnable screenUpdates = new InformUser(this.serverResponseData, characterIdentifier);
        Thread screenUpdateThread = new Thread(screenUpdates);
        screenUpdateThread.start();
        
        Runnable userInputInterface = new UserInput(this.sendData);
        Thread userInput = new Thread(userInputInterface);
        userInput.start();

        while(true){
            
            if (state.get("status").equals("EXIT")||
                state.get("status").equals("DIED") )
                break;
        }
    }



    public void printLogo() {
        String border = ForColor.BoldAndWhite.format("******************************");
        String padding = ForColor.background.format(("000000000000000000000000000000").replace("0", " "));
        String message = ForColor.style.format(" Welcome To Robot Worlds :)!!!");
        System.out.println(border+border+border);
        System.out.println(padding+message+padding);
        System.out.println(border+border+border);
    }


    //Establish connection to world with existing file
    public  void establishConnectionToWorld(RobotClient client, String ipAddress, int portNumber) throws IOException{
        if(greetUser(ipAddress, portNumber)){
            client.ServerCommunication();
            client.stopConnection();
        }
    }


    //Test connection to world through user given connection details
    public boolean testConnectionToWorld(String ipAddress, int portNumber) {
        boolean testResult = false;
        int timeout = 3000;
        try {
            startConnection(ipAddress, portNumber, timeout);
            this.command = "test connection";
            this.argument = "testing";
            this.robotName = "testing";
            clientSerializeRequest();

            sendMessage();

            receiveMessage();

            clientDeserializeResponse();

            stopConnection();

            // Return true if connection successful
            System.out.println("\n"+ForColor.BoldAndBlue.format(roboClientStr)+ForColor.pass.format("Connection Successful!\n"));
            testResult = true;
        } catch(SocketTimeoutException e){
            // Return false if connection fails
            System.out.println("\n"+ForColor.BoldAndBlue.format(roboClientStr)+ForColor.fail.format("Connection Timed Out."));
        }catch (IOException exception) {
            // Return false if connection fails
            System.out.println("\n"+ForColor.BoldAndBlue.format(roboClientStr)+ForColor.fail.format("Unable To Reach World Server."));
        }
        return testResult;
    }
    
    
    //Validate IP Address
    public  boolean validateIP(String ipAddress){
        String zeroTo255= "(\\d{1,2}|([01])\\d{2}|2[0-4]\\d|25[0-5])";
        String regex = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;
        Pattern myPattern = Pattern.compile(regex);
        Matcher myMatch = myPattern.matcher(ipAddress);
        return myMatch.matches();
    }


    //validate Port Number
    public  boolean validatePort(String port){
        String regex2 = "^[0-9]+$";
        Pattern myPattern2 = Pattern.compile(regex2);
        Matcher myMatch2 = myPattern2.matcher(port);
        return myMatch2.matches();
    }


    public void configWorldConnectionDataFile(JSONObject jsonObject) throws NumberFormatException {
        Scanner scannerInput = new Scanner(System.in);
        String userInput;
        
        printLogo();

        while(true){
            System.out.println(ForColor.info2.format("\nTo connect to a world please provide its IP Address and Port Number e.g. 192.168.4.1:4545\n"));
            userInput = scannerInput.nextLine().toLowerCase();
            
            if(!userInput.equals("continue") && !userInput.equals("")){
                String[] args = userInput.trim().split(":");
                if(userInput.equals("off") || userInput.equals("quit") || userInput.equals("exit")){
                    ForColor.printThis(ForColor.BoldAndBlue.format(roboClientStr)+ForColor.pass.format("Shutting down...\n"), true);
                    System.exit(0);
                }
                //Validating ip address and port number given by user
                if((validateIP(args[0]) && validatePort(args[1]) && args[1].length() == 4) ||
                                (args[0].equals("localhost") && validatePort(args[1]) && args[1].length() == 4)){   
                    if(testConnectionToWorld(args[0], Integer.parseInt(args[1]))){
                        jsonObject.put("port", Integer.parseInt(args[1]));
                        jsonObject.put("ipAddress", args[0]);
                        break;
                    }
                }
                else{
                    System.out.println(ForColor.warning.format("\nPlease enter valid the info or 'quit' to quit the program.\n"));
                }
            }
        }
    }

    
    public boolean configFileCheck (String configDir) throws NumberFormatException, IOException {
        File configFile = new File(configDir);

        boolean exists = configFile.exists();

        if (!exists) {

            configFile.createNewFile();

            this.jsonObject = new JSONObject();
            
            configWorldConnectionDataFile(this.jsonObject);

            FileWriter file = new FileWriter(configDir);  

            try {             

                file.write(this.jsonObject.toJSONString());

            } catch (IOException e) {
                
                ForColor.printThis(ForColor.BoldAndBlue.format(roboClientStr) + 
                ForColor.BoldAndRed.format(e.getLocalizedMessage()), true);
            }

            file.close();
        }
        return true;  
    }


    public static void main(String[] args) {

        try{
            
            RobotClient client = new RobotClient();
            
            if(client.configFileCheck(configDir)){
                JSONParser jsonParser = new JSONParser();

                FileReader reader = new FileReader(configDir);

                JSONObject fileJson = (JSONObject) jsonParser.parse(reader);

                long readPortNumber = (long) fileJson.get("port");

                String ipAddress = (String) fileJson.get("ipAddress");

                int portNumber = (int) readPortNumber;
  
                client.establishConnectionToWorld(client, ipAddress, portNumber);

            }
        }
        catch(IOException e){
            {ForColor.printThis("\n"+ForColor.BoldAndBlue.format(roboClientStr)
                    +ForColor.BoldAndYellow.format("Unable to connect to the world, please ensure the following -> "+
                            "\n1. World Server is online.\n2. World Server info is correct.\n"), true);}
        } catch (ParseException e) {
            {ForColor.printThis("\n"+ForColor.BoldAndBlue.format(roboClientStr)
                    +ForColor.BoldAndRed.format("Unable to access the port and ip address from specified config file."), true);}
        }
        catch (IllegalArgumentException e){
            {ForColor.printThis("\n"+ForColor.BoldAndBlue.format(roboClientStr)
                    +ForColor.BoldAndRed.format("Read a null upon deserialization.\n"+
                    "Client program exited incorrectly without using \"off\"."), true);}
        }  
    }
}