package com.theOnlyHorst.EpicDiscordBot.Controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theOnlyHorst.EpicDiscordBot.EpicDiscordBot;
import com.theOnlyHorst.EpicDiscordBot.Model.Command;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileReader {

    public static List<String> getCommandsForServer(String serverId)
    {
        List<String> returnVal = new ArrayList<>();
        File serverCommandDir = new File(EpicDiscordBot.dataDirectory.getPath()+"/commands/"+serverId);
        List<File> filesserver = Arrays.asList(serverCommandDir.listFiles());
        for (File f : filesserver)
        {
            returnVal.add(CommandParser.getServerPrefix(Long.parseLong(serverId))+f.getName().replace(".json",""));
        }

        return returnVal;
    }

    public static List<String> getDefaultCommands(String serverId)
    {
        List<String> returnVal = new ArrayList<>();
        File defaultCommandDir = new File(EpicDiscordBot.dataDirectory.getPath()+"/commands/default");
        List<File> filesdefault = Arrays.asList(defaultCommandDir.listFiles());
        for (File f : filesdefault)
        {
            returnVal.add(CommandParser.getServerPrefix(Long.parseLong(serverId))+f.getName().replace(".json",""));
        }
        return returnVal;
    }

    private static File findCommandFile(String command, String serverId)
    {

        File serverCommandDir = new File(EpicDiscordBot.dataDirectory.getPath()+"/commands/"+serverId);
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
        File defaultCommandDir = new File(EpicDiscordBot.dataDirectory.getPath()+"/commands/default");
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

    public static Command getCommand(String commandName,String serverId)
    {

        File resultFile = findCommandFile(commandName,serverId);
        Gson gson = new Gson();
        Command ret=null;

        if(resultFile==null)
        {
            return null;
        }

        try ( FileInputStream fis = new FileInputStream(resultFile)){
            ret = gson.fromJson(new InputStreamReader(fis), new TypeToken<Command>(){}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static File getResourceFile(String serverId,String fileName)
    {
        File serverResDir = new File(EpicDiscordBot.dataDirectory.getPath()+"/resources/"+serverId);

        if(!serverResDir.exists())
            serverResDir.mkdir();

        List<File> files = Arrays.asList(serverResDir.listFiles());
        for(File f:files)
        {
            if(f.getName().equals(fileName))
            {
                return f;
            }
        }
        return null;
    }
}
