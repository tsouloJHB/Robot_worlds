package com.team0021.robotworlds.server.world.Commands;

import java.util.HashMap;
import java.util.Stack;

import com.team0021.robotworlds.server.ServerHandler;
import com.team0021.robotworlds.server.world.Position;
import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

public class Report extends Command{
    private String topLeft = "";
    private String topRight = "";
    private String bottomLeft = "";
    private String bottomRight = "";

    public Report() {
        super("report");
    }

    private void appendToString(String objectA,  HashMap<String, Stack<String>> report){

        if(!report.get("Top Left").isEmpty()){
            topLeft = "Top Left Corner:\n";

            if(objectA.contains(" : None"))
                objectA = objectA.replace(" : None", "");

            topLeft += objectA+"\n";

            for(String aa:report.get("Top Left")){
                topLeft += " -> "+aa+"\n";
            }
        }


        if(!report.get("Top Right").isEmpty()){
            topRight = "\t\t\t\t\t\t\tTop Right Corner:\n";

            if(objectA.contains(" : None"))
                objectA = objectA.replace(" : None", "");

            topRight += "\t\t\t\t\t\t\t"+objectA+"\n";
            for(String aa:report.get("Top Right")){
                topRight += "\t\t\t\t\t\t\t  -> "+aa+"\n";
            }
        }


        if(!report.get("Bottom Left").isEmpty()){
            bottomLeft = "Bottom Left Corner:\n";

            if(objectA.contains(" : None"))
                objectA = objectA.replace(" : None", "");

            bottomLeft += objectA+"\n";
            for(String aa:report.get("Bottom Left")){
                bottomLeft += " -> "+aa+"\n";
            }
        }


        if(!report.get("Bottom Right").isEmpty()){
            bottomRight = "\t\t\t\t\t\t\tBottom Right Corner:\n";

            if(objectA.contains(" : None"))
                objectA = objectA.replace(" : None", "");

            bottomRight += "\t\t\t\t\t\t\t"+objectA+"\n";
            for(String aa:report.get("Bottom Right")){
                bottomRight += "\t\t\t\t\t\t\t -> "+aa+"\n";
            }
        }
    }
    
    /**
     * Method provides implementation of the RightCommand instruction.
     * @param Robot object 'target'
     * @return Boolean object 'true'
    */
    @Override
    public boolean execute(CharacterAbstract characterObject) {
        Positions worldObject = ServerHandler.getWorldObject();
        Position currentPosition = characterObject.getCharacterPosition();
        HashMap<String, String> tempResult = characterObject.getCharacterResult();
        HashMap<String, Stack<String>> report;
        HashMap<String, String> visibilityReport = new HashMap<String, String>();

        report = worldObject.getRobotsInRange(currentPosition);
        appendToString("Robots : None", report);
        report = worldObject.getPitfallsInRange(currentPosition);
        appendToString("Pitfalls : None", report);
        report = worldObject.getObstaclesInRange(currentPosition);
        appendToString("Obstacles : None", report);
        report = worldObject.getMinesInRange(currentPosition);
        appendToString("Mines : None", report);
        report = worldObject.getHealthPackInRange(currentPosition);
        appendToString("Health Pack : None", report);

        // this.topLeft+this.topRight+this.bottomLeft+this.bottomRight;
        // System.out.println((Stack)report.get("Top Right"));
        // System.out.println((String)report.get("Top Left"));
        // System.out.println((String)report.get("Bottom Right"));
        // System.out.println((String)report.get("Bottom Left"));
         
        characterObject.setMessage("\n"+this.topLeft+this.topRight+this.bottomLeft+this.bottomRight);
        return true;
    }

}
