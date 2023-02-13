package com.team0021.robotworlds.server.world.Characters;
import java.lang.reflect.Array;

import java.util.HashMap;

import com.team0021.robotworlds.ForColor;
import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Direction;
import com.team0021.robotworlds.server.world.Position;
import com.team0021.robotworlds.server.world.Positions;

public class AllRounder extends CharacterAbstract {
    private Positions worldObject;
    private Position allRounderPosition;
    private String allRounderDirection;

    private final String teamName;
    private final String characterIdentifier;
    private final CharacterType characterType;

    private HashMap<String, Object> data;
    private HashMap<String, Object> state;
    private HashMap<String, String> result;

    public AllRounder(String teamName){
        super(teamName);
        this.worldObject = ServerHandler.getWorldObject();
        this.teamName = teamName;
        this.allRounderDirection = "NORTH";
        this.characterType = CharacterType.ALLROUNDER;
        this.characterIdentifier = "allrounder."+teamName;

        initializeData();
        initializeState();
        initializeResult();
    }

    private void initializeResult() {
        this.result = new HashMap<String, String>()
        {{
            put("result","OK");
            put("message", "Welcome soldier.");
        }};    
    }

    private void initializeState() {
        this.state = new HashMap<String, Object>()
        {{
            put("position", null);                                         
            put("direction", "NORTH");                                  
            put("shields", 5);                                           
            put("shots", 4);
            put("mine", 2);                                         
            put("status", "NORMAL");
            put("reload",3);   
            put("repair", 0);                                                                          
             }};
    }

    private void initializeData() {
        this.data =  new HashMap<String, Object>()
        {{
            put("visibility", 20);                                   
            put("reload", 5);                                       
            put("repair", 5);                                     
            put("mine", 2);                                          
            put("shields", 5); 
            put("distance",2);
            put("shots", 4);                                            
        }};
    }

    @Override
    public HashMap<String, String> getCharacterResult() {
        return this.result;
    }

    @Override
    public HashMap<String, Object> getCharacterData() {
        return this.data;
    }

    @Override
    public HashMap<String, Object> getCharacterState() {
        return this.state;
    }

    @Override
    public void setCharacterPosition(Position newPosition) {
        this.allRounderPosition = newPosition;
        int[] tempList = {newPosition.getX(), newPosition.getY()};
        this.state.put("position", tempList);
    }


     @Override
    public Position getCharacterPosition() {
        return this.allRounderPosition;
    }


    @Override
    public String getCharacterDirection() {
        return (String) this.state.get("direction");
    }

    @Override
    public String getTeamName() {
        return this.teamName;
    }

    @Override
    public String getCharacterIdentifier() {
        return this.characterIdentifier;
    }

    @Override
    public CharacterType getCharacterType() {
        return this.characterType;
    }

    @Override
    public String getMessage() {
        return this.result.get("message");
    }

    @Override
    public void setMessage(String commandResponse) {
        this.result.put("message", commandResponse);        
    }

    @Override
    public void setCharacterResult(HashMap<String, String> result) {
        this.result = result;        
    }

    @Override
    public void setCharacterData(HashMap<String, Object> data) {
        this.data = data;        
    }

    @Override
    public void setCharacterState(HashMap<String, Object> state) {
        this.state = state;       
    }

    @Override
    public void showHelp() {
        String myRole = ForColor.BoldAndYellow.format("\'ALL-ROUNDER\'");
        String helpMessage = ForColor.BoldAndPurple.format("I am a "+myRole+ForColor.BoldAndPurple.format(" and I can understand these commands:\n"))+
        ForColor.BoldAndPurple.format("Movement Commands:\n")+
        ForColor.BoldAndYellow.format("    forward:\t\t")+ForColor.BoldAndWhite.format("- move forward by specified number of steps, e.g. 'FORWARD 10'\n")+
        ForColor.BoldAndYellow.format("    back:\t\t")+ForColor.BoldAndWhite.format("- move back by specified number of steps, e.g. 'BACK 10'\n")+
        ForColor.BoldAndYellow.format("    right:\t\t")+ForColor.BoldAndWhite.format("- turn right, e.g. 'RIGHT'\n")+
        ForColor.BoldAndYellow.format("    left:\t\t")+ForColor.BoldAndWhite.format("- turn left, e.g. 'LEFT'\n")+
        ForColor.BoldAndYellow.format("    sprint:\t\t")+ForColor.BoldAndWhite.format("- move forward by the factorial of steps, e.g. 'SPRINT 5'\n")+
        ForColor.BoldAndPurple.format("Operational Commands:\n")+
        ForColor.BoldAndYellow.format("    fire:\t\t")+ForColor.BoldAndWhite.format("- shoot a robot in the same direction as you and within 5 steps\n")+
        ForColor.BoldAndYellow.format("    stab:\t\t")+ForColor.BoldAndWhite.format("- get in close range and stab a another player 1 step ahead of you to death - stealth is key.\n")+
        ForColor.BoldAndYellow.format("    look:\t\t")+ForColor.BoldAndWhite.format("- to report on anything on that is within 5 steps in your"+
                                            "\n\t\t\t  Northerly, Southerly, Westerly and Easterly direction.\n")+
        ForColor.BoldAndYellow.format("    report:\t\t")+ForColor.BoldAndWhite.format("- a variant of the look command. Provides a five radius scope from your position.\n")+
        ForColor.BoldAndYellow.format("    reload:\t\t")+ForColor.BoldAndWhite.format("- enables your character to reload on ammo.\n")+
        ForColor.BoldAndYellow.format("    repair:\t\t")+ForColor.BoldAndWhite.format("- allows you to fully restore your shields to maximum strength.\n")+
        ForColor.BoldAndYellow.format("    clear:\t\t")+ForColor.BoldAndWhite.format("- clear up your screen.\n")+
        ForColor.BoldAndYellow.format("    help:\t\t")+ForColor.BoldAndWhite.format("- provide information about commands\n")+
        ForColor.BoldAndYellow.format("    off:\t\t")+ForColor.BoldAndWhite.format("- exit game\n")+ 
        ForColor.BoldAndPurple.format("Communication Commands:\n")+
        ForColor.BoldAndYellow.format("    #[player_identifier] [message]:")+ForColor.BoldAndWhite.format("\t- Use the '#' and the recipient's player.identifier\n"+
        "\t\t\t\t\t  to send a message directly to any player in-game.\n"+
        "\t\t\t\t\t  e.g. #potato007.sniper I SEE YOU!!!\n")+
        ForColor.BoldAndYellow.format("    #all [message]:")+ForColor.BoldAndWhite.format("\t- Use the '#all' command to broadcast a message to all\n"+
        "\t\t\t  the players in-game. e.g. #all Hello World!!!\n")+
        ForColor.BoldAndYellow.format("    #team [message]:")+ForColor.BoldAndWhite.format("\t- Use the '#team' command to broadcast a message directly to all your\n"+
        "\t\t\t  team members in-game. e.g. #team Let's Do This!!!\n")+
        ForColor.BoldAndYellow.format("    #admin [message]:")+ForColor.BoldAndWhite.format("\t- Use the '#admin' to send a report against another player or flag\n"+
        "\t\t\t  a problem in-game. e.g. #admin my 'help' command doesn't work - please help.\n\n");


       setMessage(helpMessage);
    }

}