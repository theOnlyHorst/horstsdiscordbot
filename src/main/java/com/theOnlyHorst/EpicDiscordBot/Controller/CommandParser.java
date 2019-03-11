package com.theOnlyHorst.EpicDiscordBot.Controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theOnlyHorst.EpicDiscordBot.EpicDiscordBot;
import com.theOnlyHorst.EpicDiscordBot.Model.Command;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
<<<<<<< HEAD
=======

>>>>>>> b6be1a8271b3ef639ca38e787289192cec2fa424
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


            File resultFile = findCommandFile(command,server);

            if(resultFile==null||!resultFile.exists())
            {
                channelsent.sendMessage("Command not found: " + command).queue();
                return;
            }
            //channelsent.sendMessage("command found sir you can continue working on it").queue();
            Gson gson = new Gson();

            Command comToExec=null;

            try ( FileInputStream fis = new FileInputStream(resultFile)){
                comToExec = gson.fromJson(new InputStreamReader(fis), new TypeToken<Command>(){}.getType());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            CommandProcessor.executeCommand(comToExec,args,userSent,server,channelsent,msg);

        }



    }


    public static File findCommandFile(String command,Guild server)
    {

        File serverCommandDir = new File(EpicDiscordBot.dataDirectory.getPath()+"/"+server.getId());
        File resultFile =null;
        if(!serverCommandDir.exists())
        {
            serverCommandDir.mkdir();
        }
        else
        {
            if(serverCommandDir.listFiles()!=null) {
                List<File> files = Arrays.asList(serverCommandDir.listFiles());
                for(File f:files)
                {
                    if(f.getName().equals(command+".json"))
                    {
                        resultFile = f;
                        break;
                    }
                }
            }
        }
        File defaultCommandDir = new File(EpicDiscordBot.dataDirectory.getPath()+"/default");
        if(resultFile==null)
        {
            List<File> files = Arrays.asList(defaultCommandDir.listFiles());
            for(File f:files)
            {
                if(f.getName().equals(command+".json"))
                {
                    resultFile = f;
                    break;
                }
            }
        }
        return resultFile;
    }





}
