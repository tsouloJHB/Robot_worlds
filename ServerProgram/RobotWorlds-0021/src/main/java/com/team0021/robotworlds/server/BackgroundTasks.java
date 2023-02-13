package com.team0021.robotworlds.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.team0021.robotworlds.ForColor;
import com.team0021.robotworlds.server.world.Position;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

import java.lang.Runnable;
import java.net.SocketException;
import java.net.UnknownHostException;


/**
 * Class used to provide the implementation for the commands that can be 
 * executed on the servside. It is instantiated using the name of the thread.
 * @author imogano
 * @contributors msibiya, lntshele and tsoulo
*/
public class BackgroundTasks implements Runnable{
    private ServerHandler serverHandler;
    private Scanner listenerScanner;
    private String threadName;


    public BackgroundTasks(String threadName){
        this.serverHandler = ServerHandler.getServerHandler();
        this.listenerScanner = new Scanner(System.in);
        this.threadName = threadName;
    }

    private HashMap<Position, CharacterAbstract> robotPositions = ServerHandler.getWorldObject().getCharacterPositions();
    private void clearScreen(){
        printThis(("(\033[H\033[2J"), false);
    }


    /**
     * Function used to print the coordinates of where obstacles and pitfalls 
     * can be found.
     * @param None
    */
    private void seeAllWorldObjects(){
        HashMap<Position, CharacterAbstract> robotPositions = ServerHandler.getWorldObject().characterPositions;
        String obs = ForColor.BoldAndGreen.format("\nThe are obstacles: ");

        for(Position i: ServerHandler.getWorldObject().getObstacleList()){
            obs += "\n - at ("+i.getX()+","+i.getY()+") to ("+(4+i.getX())+","+(4+i.getY())+")";
        }

        obs += "\nThe are pitfalls: ";

        for(Position i: ServerHandler.getWorldObject().getPitfallList()){
            obs += "\n - at ("+i.getX()+","+i.getY()+") to ("+i.getX()+","+i.getY()+")";
        }

        obs += "\nThe player positions are: ";
        if(robotPositions.size() == 0){
            ForColor.printThis(ForColor.BoldAndYellow.format("messageToPrint"), true);
        }
        for(Map.Entry<Position, CharacterAbstract> robotPosition : robotPositions.entrySet()){
            
            obs += "\n - at ("+robotPosition.getKey().getX()+","+robotPosition.getKey().getY()+")";
        }


        obs += "\n";

        printThis(obs,false);
    }

    private String getThreadName(){
        return ForColor.BoldAndPurple.format("["+this.threadName+"]: ");
    }

    
    private String getAdminName(){
        return ForColor.BoldAndPurple.format("[admin] > ");
    }
    
    //Validate User Identifier 
    private boolean validateTargetIdentifier(String targetIdentifier){
        String subRegex = "^[a-z]{5,10}\\.";
        String regex = subRegex+"scout$|" +subRegex+"defender$|"+subRegex+"allrounder$|"+subRegex+"sniper$";
        Pattern myPattern = Pattern.compile(regex);
        Matcher myMatch = myPattern.matcher(targetIdentifier);
        return myMatch.matches();
    }

    private void  printServerLogo(){

        try {
            printThis(ForColor.BoldAndPurple.format("\t\t\t\t\t\t"+getThreadName())+
            ForColor.JustWhite.format(
               "\n\t\t\t\t    Today is a good day for errors :}."+
               "\n\t\t\t\t  Sending you ")+ 
            ForColor.BoldAndGreen.format("good vibes")+
            ForColor.JustWhite.format(", ")+
            ForColor.BoldAndYellow.format("light")+
            ForColor.JustWhite.format(" and ")+
            ForColor.BoldAndBlue.format("grit")+
            ForColor.JustWhite.format(".")+
            ForColor.BoldAndWhite.format("\n\t\t\t    (The plug) ")+
            ForColor.JustWhite.format(ServerHandler.getConnectionInfo()+"\n\n"), true);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    /**
     * Function used to reduce the amount of times we use System.out.println()
     * to inform the user. It is invoked using the message to display and a 
     * boolean object to indicate formatting.
     * @param messageToPrint String object used to display to user.
     * @param useThis Boolean object used to indicate if messageToPrint should 
     * be used as is or if the function should use the formatting provided.
    */
    private void printThis(String messageToPrint, boolean useThis){
        if(useThis)
            System.out.print("\n"+messageToPrint);
        else
            System.out.print(ForColor.BoldAndPurple.format("\n["+this.threadName+"]: ")+
                            ForColor.JustWhite.format(messageToPrint));
    }

    private void sendMessageToAllClients(String message){
        boolean targetFound = false;
        if(robotPositions.size() != 0) {
            ServerHandler.broadcastToEveryoneUsing(getAdminName()+message);
            System.out.println(ForColor.BoldAndGreen.format("Message Sent!"));
            targetFound = true;
        }
        if(!targetFound) {
            ForColor.printThis(ForColor.BoldAndYellow.format("No Active Players At This Time.\n"), true);
        }
    }

    private void sendMessageToClient(String targetIdentifier, String message){
        boolean targetFound = false;
        CharacterAbstract getTarget = null;
        if(validateTargetIdentifier(targetIdentifier)){
            for(Map.Entry<Position, CharacterAbstract> robotPosition : robotPositions.entrySet()){
                getTarget = robotPosition.getValue();
                String targetPlayer = getTarget.getCharacterIdentifier();
                if(targetPlayer.contains(targetIdentifier.toLowerCase())){ 
                    ServerHandler.directCommunicationToUser(getTarget, getAdminName()+message);
                    System.out.println(ForColor.BoldAndGreen.format("Message Sent!"));
                    targetFound = true;
                    break;
                }
            }
        }
        if(!targetFound) {
            ForColor.printThis(ForColor.BoldAndYellow.format("The following user: "+ForColor.BoldAndRed.format(targetIdentifier)+
            ForColor.BoldAndYellow.format(" - Does not exist, please try again.\n")), true);
        }
    }

    public void purgeClient(String targetName, String targetRole){
        String targetIdentifier = targetRole+"."+targetName+"."+targetRole;
        boolean targetFound = false;
        CharacterAbstract getTarget = null;
        Positions locatePosition = ServerHandler.getWorldObject();
        HashMap<String, Object> state = new HashMap<String, Object>();
        String goodbyeMessage = ForColor.BoldAndRed.format("You've Been Killed Off By The Admin - Take This Time To Think About Your Words & Actions");
   
        for(Map.Entry<Position, CharacterAbstract> robotPosition : robotPositions.entrySet()){
            getTarget = robotPosition.getValue();
            String targetPlayer = getTarget.getCharacterIdentifier();
            if(targetPlayer.equals(targetIdentifier.toLowerCase())){
                locatePosition.terminatePosition(getTarget);
                ServerHandler.killClientThread(targetName+"."+targetRole);    
                state = getTarget.getCharacterState();
                state.put("status", "DIED");
                getTarget.setCharacterState(state);
                ServerHandler.directCommunicationToUser(getTarget, getAdminName()+goodbyeMessage);             
                System.out.println(ForColor.BoldAndRed.format(targetName+"."+targetRole+" "+ForColor.BoldAndGreen.format("Has Been Eliminated")));
                targetFound = true;
               
                break;
            }
        }
        if(!targetFound) {
            ForColor.printThis(ForColor.BoldAndYellow.format("The following user: "+ForColor.BoldAndRed.format(targetName+"."+targetRole)+
            ForColor.BoldAndYellow.format(" - Does not exist, please try again.\n")), true);
    
        }
    }

    private void viewPlayerList(){
        CharacterAbstract target = null;
        String userRole = "";
        String userName = "";
        int count = 0;
      
        if(robotPositions.size() == 0){
            System.out.println(ForColor.BoldAndYellow.format("No Active Players At This Time."));
        }else{
            System.out.println(ForColor.BoldAndYellow.format("No.\tUsername:\tCharacterRole:"));
        }
        for(Map.Entry<Position, CharacterAbstract> robotPosition : robotPositions.entrySet()){
            target = robotPosition.getValue();
            String player = target.getCharacterIdentifier();
            if(player.contains("sniper")){
                player = player.replace("sniper", "");
                userRole = "sniper";
                userName = player;
            }else
            if(player.contains("defender")){
                player = player.replace("defender", "");
                userRole = "defender";
                userName = player;
            }else
            if(player.contains("scout")){
                player = player.replace("scout", "");
                userRole = "scout";
                userName = player;
            }else
            if(player.contains("allrounder")){
                player = player.replace("allrounder", "");
                userRole = "allrounder";
                userName = player;
            }
            userName = userName.replace(".", "");
            System.out.printf(ForColor.BoldAndWhite.format("%d\t%-10s\t%-10s\n"), (++count), userName,userRole);
        }
    }
    
    

    /**
     * Function provides the implementation for the BackgroundTask thread.
    */
    @Override
    public void run() {
        
        String greeting = "Logged on serverside.\n\n";

        printServerLogo();

        String menu =
            ForColor.BoldAndWhite.format("Server management commands:\n\n")+
            ForColor.BoldAndYellow.format("purge [username] [role]:")+ForColor.JustWhite.format("       To remove a specific player from the game, e.g. purge potato007 sniper\n\n")+
            ForColor.BoldAndYellow.format("#[username.role] [message]:")+ForColor.JustWhite.format("    Use the '#' and the recipient's player.identifier to send a message"+
                                            "\n\t\t\t       directly to any player in-game, e.g. #potato007.sniper I SEE YOU!!!\n\n")+
            ForColor.BoldAndYellow.format("#all [message]:")+ForColor.JustWhite.format("    Use the '#all' command to broadcast a message to all the players in-game.\n"+
                                            "\t\t   e.g. #all Hello World!!!\n\n")+
            ForColor.BoldAndYellow.format("deactivated:")+ForColor.JustWhite.format("\tTo see number of deactivated players.\n\n")+
            ForColor.BoldAndYellow.format("active:")+ForColor.JustWhite.format("\t\tTo see number of active players.\n\n")+
            ForColor.BoldAndYellow.format("players:")+ForColor.JustWhite.format( "\tTo view the list of active players.\n\n")+
            ForColor.BoldAndYellow.format("clear:")+ForColor.JustWhite.format("\t\tTo clear up your screen.\n\n")+
            ForColor.BoldAndYellow.format("dump:")+ForColor.JustWhite.format("\t\tTo see the all obstacle, pitfalls, mines and players in the world.\n\n")+
            ForColor.BoldAndYellow.format("menu:")+ForColor.JustWhite.format( "\t\tTo see menu.\n\n")+
            ForColor.BoldAndYellow.format("off:")+ForColor.JustWhite.format("\t\tTo exit.\n"); 

        greeting += ForColor.JustWhite.format(menu);

        printThis(greeting,true);

        greeting = "";

        while(true)
        {
            printThis(greeting, false);

            greeting = this.listenerScanner.nextLine().trim();
            String[] message = greeting.split(" ", 2);
            String[] args = greeting.split(" ");
            greeting = args[0];

            if(message.length > 1 && args[0].equalsIgnoreCase("#all")){
                sendMessageToAllClients(ForColor.BoldAndYellow.format(message[1]));
            }
            else if(message.length > 1 && message[0].contains("#")){
                String identifier = greeting.replace("#", "");
                sendMessageToClient(identifier, ForColor.BoldAndYellow.format(message[1]));
            }
            else if(greeting.equalsIgnoreCase("active")){
                printThis(""+this.serverHandler.getNumberOfActivePlayers()+"\n",false);
            }
            else if(greeting.equalsIgnoreCase("deactivated")){
                printThis(""+this.serverHandler.getNumberOfDeActivatedPlayers()+"\n",false);
            }
            else if(greeting.equalsIgnoreCase("clear")){
                clearScreen();
                printServerLogo();
            }
            else if(greeting.equalsIgnoreCase("dump")){
                seeAllWorldObjects();
            }

            else if(greeting.equalsIgnoreCase("menu")){
                printThis(menu,false);
            }
            else if(greeting.equalsIgnoreCase("players")){
                viewPlayerList();
            }
            else if(greeting.equalsIgnoreCase("purge") && args.length == 3){
                String targetName = args[1].toLowerCase();
                String targetRole = args[2].toLowerCase();
                purgeClient(targetName, targetRole);
            }
            else if(greeting.equalsIgnoreCase("off") || greeting.equalsIgnoreCase("quit") || 
                greeting.equalsIgnoreCase("exit") || greeting.equalsIgnoreCase("bye")) {
                break;
            }else {
                System.out.println(ForColor.BoldAndYellow.format("Invalid command please enter a valid command."));
            }

            greeting = "";
        }
        greeting = "Shutting down...";
        System.out.println(getThreadName()+ ForColor.BoldAndGreen.format(greeting));
        System.exit(0);
    }
}
