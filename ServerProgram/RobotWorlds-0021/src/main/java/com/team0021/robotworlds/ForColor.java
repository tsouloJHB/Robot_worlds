package com.team0021.robotworlds;

import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;

public class ForColor {
    //Color formatting for different messages
    public static AnsiFormat menuItem = new AnsiFormat(Attribute.BOLD(), Attribute.YELLOW_TEXT());
    public static AnsiFormat pass = new AnsiFormat(Attribute.BOLD(), Attribute.GREEN_TEXT());
    public static AnsiFormat fail = new AnsiFormat(Attribute.BOLD(), Attribute.RED_TEXT());
    public static AnsiFormat info = new AnsiFormat(Attribute.BOLD(), Attribute.YELLOW_TEXT());
    public static AnsiFormat info2 = new AnsiFormat(Attribute.BOLD(), Attribute.WHITE_TEXT());
    public static AnsiFormat warning = new AnsiFormat(Attribute.BOLD(), Attribute.YELLOW_TEXT(), Attribute.ITALIC());
    public static AnsiFormat background = new AnsiFormat(Attribute.BOLD(), Attribute.WHITE_BACK());
    public static AnsiFormat style = new AnsiFormat(Attribute.BOLD(), Attribute.BRIGHT_BLACK_TEXT(), Attribute.WHITE_BACK());


    //Just for text
    public static AnsiFormat JustRed = new AnsiFormat(Attribute.RED_TEXT());
    public static AnsiFormat JustBlue = new AnsiFormat(Attribute.BLUE_TEXT());
    public static AnsiFormat JustWhite = new AnsiFormat(Attribute.WHITE_TEXT());
    public static AnsiFormat JustGreen = new AnsiFormat(Attribute.GREEN_TEXT());
    public static AnsiFormat JustYellow = new AnsiFormat(Attribute.YELLOW_TEXT());
    public static AnsiFormat JustPurple = new AnsiFormat(Attribute.MAGENTA_TEXT());

    //Color formatting for different message
    public static AnsiFormat BoldAndRed = new AnsiFormat(Attribute.BOLD(), Attribute.RED_TEXT());
    public static AnsiFormat BoldAndBlue = new AnsiFormat(Attribute.BOLD(), Attribute.BLUE_TEXT());
    public static AnsiFormat BoldAndWhite = new AnsiFormat(Attribute.BOLD(), Attribute.WHITE_TEXT());
    public static AnsiFormat BoldAndGreen = new AnsiFormat(Attribute.BOLD(), Attribute.GREEN_TEXT());
    public static AnsiFormat BoldAndYellow = new AnsiFormat(Attribute.BOLD(), Attribute.YELLOW_TEXT());
    public static AnsiFormat BoldAndPurple = new AnsiFormat(Attribute.BOLD(), Attribute.MAGENTA_TEXT());
    
    
    /**
     * Function used to reduce the amount of times we use System.out.println()
     * to inform the user. It is invoked using the message to display and a 
     * boolean object to indicate formatting.
     * @param messageToPrint String object used to display to user.
     * @param useThis Boolean object used to indicate if messageToPrint should 
     * be used as is or if the function should use the formatting provided.
    */
    public static void printThis(String messageToPrint, boolean useThis){
        if(useThis)
            System.out.print(messageToPrint);
        else
            System.out.print(ForColor.JustWhite.format(messageToPrint));
    }

}