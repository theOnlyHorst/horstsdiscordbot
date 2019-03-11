package com.theOnlyHorst.EpicDiscordBot.Controller;

import com.theOnlyHorst.EpicDiscordBot.Model.Command;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandParser {


    public static final String defaultPrefix = "!";


    public static void parseCommand(Guild server, String content, MessageChannel channelsent, User userSent, Message msg)
    {

        //TODO implement server prefix management

        //else
        if(content.startsWith(defaultPrefix))
        {
            String[] splitContent = content.replace(defaultPrefix,"").split(" ");
            String command = splitContent[0];

            //splitContent = content.replace(defaultPrefix+command+" ","").split(" ");

            ArrayList<String> args = new ArrayList<String>(Arrays.asList(splitContent));
            args.remove(0);



           Command comToExec = FileReader.getCommand(command,server.getId());



            CommandProcessor.executeCommand(comToExec,args,userSent,server,channelsent,msg);

        }



    }








}
