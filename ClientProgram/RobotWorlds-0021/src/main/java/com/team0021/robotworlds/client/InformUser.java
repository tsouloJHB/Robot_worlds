package com.team0021.robotworlds.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team0021.robotworlds.ForColor;

public class InformUser implements Runnable {
    private BufferedReader serverResponseData;

    private ObjectMapper mapper;

    private HashMap<String, String> result;
    private HashMap<String, Object> data;
    private HashMap<String, Object> state;

    private ArrayList newA;

    private String serverResponse;

    private String characterIdentifier;


    public InformUser(BufferedReader serverResponseData, String characterIdentifier){
        this.serverResponseData = serverResponseData;
        this.characterIdentifier = characterIdentifier;
        this.mapper = new ObjectMapper();
        this.result = new HashMap<>();
        this.data = new HashMap<>();
        this.state = new HashMap<>();
    }

    @Override
    public void run() {
        DataDeserialize serverData;
        int x, y;

        try {
            while(true){
                this.serverResponse = this.serverResponseData.readLine();
                serverData = mapper.readValue(this.serverResponse, DataDeserialize.class);
                this.result = serverData.result;
                this.data = serverData.data;       
                this.state = serverData.state; 

                this.newA = (ArrayList) this.state.get("position");

                x = (int) this.newA.get(0);
                y = (int) this.newA.get(1);

                ForColor.printThis(ForColor.BoldAndWhite.format(
                "\n[WORLD] ("+x+", "+y+") > "+(String)this.result.get("message")+"\n"+
                "\t\t\t\t\t\t\t\t          Character I.D: " +this.characterIdentifier+ "\n"+
                "\t\t\t\t\t\t\t\t          Worldstate: "+this.data.get("Worldstate")+"\n"+
                "\t\t\t\t\t\t\t\t          Direction: "+this.state.get("direction")+"\n"+
                "\t\t\t\t\t\t\t\t          Shields: "+this.state.get("shields")+"\n"+
                "\t\t\t\t\t\t\t\t          Reload: "+this.state.get("reload")+"\n"+
                "\t\t\t\t\t\t\t\t          Mines: "+this.state.get("mine")+"\n"+
                "\t\t\t\t\t\t\t\t          Repair: "+this.state.get("repair")+"\n"+
                "\t\t\t\t\t\t\t\t          Shots: "+this.state.get("shots")+"\n"+
                "\t\t\t\t\t\t\t\t          Status: "+this.state.get("status")+"\n"),true);
               
                RobotClient.updateGameFields(this.result,this.state, this.data);

                if (this.state.get("status").equals("EXIT")||
                    this.state.get("status").equals("DIED") )
                    break;
            } 
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            System.out.println(ForColor.BoldAndBlue.format("[InformUser]: ")+ 
            ForColor.fail.format("No Response Recorded From Server - Server just went awol."));
        } catch (IOException e) {
            System.out.println(ForColor.BoldAndBlue.format("[InformUser]: ")+ 
            ForColor.fail.format("Server connection got interrupted."));
        }  
            System.exit(0);        
    }
}
