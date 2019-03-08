package com.theOnlyHorst.EpicDiscordBot.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandParser {


    public static final String defaultPrefix = "!";


    public static String parseCommand(long serverId,String content)
    {

        //TODO implement server prefix management

        //else
        if(content.startsWith(defaultPrefix))
        {
            String[] splitContent = content.replace(defaultPrefix,"").split(" ");
            String command = splitContent[0];
            List<String> args = Arrays.asList(splitContent);
            args.remove(0);
            CommandParser.class.getClassLoader().getResource("Commands/"+command+".json");


        }

        return null;

    }





}
