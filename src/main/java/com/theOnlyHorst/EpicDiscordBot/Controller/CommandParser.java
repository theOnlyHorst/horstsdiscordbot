package com.theOnlyHorst.EpicDiscordBot.Controller;

import com.theOnlyHorst.EpicDiscordBot.Model.Command;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandParser {


    private static final String defaultPrefix = "!";


    public static void parseCommand(Guild server, String content, MessageChannel channelsent, User userSent, Message msg)
    {

        //else
        if(content.startsWith(getServerPrefix(server.getIdLong())))
        {
            String[] splitContent = content.replace(getServerPrefix(server.getIdLong()),"").split(" ");
            String command = splitContent[0];

            //splitContent = content.replace(defaultPrefix+command+" ","").split(" ");

            ArrayList<String> args = new ArrayList<String>(Arrays.asList(splitContent));
            args.remove(0);



            Command comToExec = FileReader.getCommand(command,server.getId());



            CommandProcessor.executeCommand(comToExec,args,userSent,server,channelsent,msg);

        }



    }

    public static String getServerPrefix(Long serverId)
    {

        //TODO read prefix from server
        /*
        * if(...)
        * {
        *
        *
        * }
        *
        * else
        * {
        */
        return defaultPrefix;
        //}

    }








}
