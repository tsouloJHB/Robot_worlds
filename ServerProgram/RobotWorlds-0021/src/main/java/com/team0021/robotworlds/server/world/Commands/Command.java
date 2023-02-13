package com.team0021.robotworlds.server.world.Commands;

import com.team0021.robotworlds.server.world.Positions;
import com.team0021.robotworlds.server.world.Characters.CharacterAbstract;

/**
 * Class provides implementation for the Command class that serve as a parent 
 * class for the command classes that are used by te Robot class.
 * @author imogano
*/
public abstract class Command {
    private final String name;
    private String argument;    

    public abstract boolean execute(CharacterAbstract characterObject);


    /**
     * Class constructor for commands without an additional argument.
     * @param name
    */
    public Command(String name){
        this.name = name.trim().toLowerCase();
        this.argument = "";
    }


    /**
     * Class constructor for commands with an additional argument.
     * @param name
     * @param argument
    */
    public Command(String name, String argument) {
        this(name);
        this.argument = argument.trim().toLowerCase();
    }


    /**
     * Function used to get the name of an instruction.
     * @return The name of a command.
    */
    public String getName() {                                                                           
        return name;
    }


    /**
     * Function used to get the argument that supports an instruction.
     * @return The argument used to support a command.
    */
    public String getArgument() {
        return this.argument;
    }


    /**
     * Static function used to provide for the appropriate constructor that can 
     * be used to create and execute a command
     * @param instruction
     * @return Command object constructor
    */
    public static Command create(String instruction) {
        String[] args = instruction.toLowerCase().trim().split(" ");
        String message = instruction.trim();
        if(args.length == 1){
            if (args[0].equals("off") || args[0].equals("exit") || args[0].equals("quit"))
                return new ShutdownCommand();
            else if (args[0].equals("clear"))
                return new ClearCommand();
            else if (args[0].equals("help"))
                return new HelpCommand();
            else if (args[0].equals("fire"))
                return new FireCommand();
            else if (args[0].equals("reload"))
                return new ReloadCommand();            
            else if (args[0].equals("look"))
                return new Look(); 
            else if (args[0].equals("report"))
                return new Report();  
            else if (args[0].equals("spy"))
                return new SpyCommand();
            else if (args[0].equals("stab"))
                return new StabCommand();
            else if(args[0].equals("mine"))
                return new MineCommand();
            else if(args[0].equals("repair"))
                return new RepairCommand(); 
        }
        
        if (args.length == 2){
            if (args[0].equals("turn") && args[1].equals("right"))
                return new RightCommand(); 
            else if (args[0].equals("turn") && args[1].equals("left"))
                return new LeftCommand();               
            else if (args[0].equals("forward"))
                return new ForwardCommand(args[1]);
            else if (args[0].equals("back"))
                return new BackCommand(args[1]);  
            else if (args[0].equals("sprint"))
                return new SprintCommand(args[1]);
            else if (args[0].equals("mazerun") && 
                            (args[1].equals("right") || args[1].equals("up") || 
                             args[1].equals("down") || args[1].equals("left")))
                return new MazerunCommand(args[1]); 
        }

        if (message.startsWith("#all") && args.length > 1 || message.startsWith("#team") && args.length > 1)
            return new BroadcastCommand(message);
        else if((args[0].equalsIgnoreCase("#admin") || 
            (args[0].startsWith("#") && (args[0].toLowerCase().contains("scout") || args[0].toLowerCase().contains("sniper") || 
            args[0].toLowerCase().contains("defender") || args[0].toLowerCase().contains("allrounder"))))  && args.length > 1)
            return new DirectComCommand(message);
        else
            throw new IllegalArgumentException("Are you okay ;( ?  I cannot do: \"" + 
            instruction+"\". Seek help @ \"help\".");
    }
}