package com.theOnlyHorst.EpicDiscordBot.Controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theOnlyHorst.EpicDiscordBot.EpicDiscordBot;
import com.theOnlyHorst.EpicDiscordBot.Model.Command;
import com.theOnlyHorst.EpicDiscordBot.Model.Server;
import net.dv8tion.jda.core.entities.*;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandProcessor {


    //TODO ein Arschhaufen Fehlerprüfungen

    private static Map<String,Method> hookMethods;


    public static void executeCommand(Command command, ArrayList<String> args, User executingUser, Guild server, MessageChannel channel, Message msg)
    {
        while (command.getArgumentNames().size()>args.size())
        {
            args.add("");
        }
        for (String c : command.getActions())
        {
            String endAction = c;
            if(c.contains("$"))
            {


                for (String a : command.getArgumentNames())
                {
                    if(c.contains("$"+a))
                    {
                        endAction = endAction.replace("$"+a, args.get(command.getArgumentNames().indexOf(a)));
                    }
                }

            }


            //DEBUG
            //channel.sendMessage(endAction).queue();
            //####################################

            executeHook(command,endAction,executingUser,server,channel,msg);



        }
    }

    public static List<String> executeValueHook(Command command ,String action, User executingUser, Guild server, MessageChannel channel, Message msg)
    {
        Matcher matchAll = Pattern.compile("(^.*?)\\((.*?)\\)$").matcher(action);


        if(matchAll.matches()) {
            String hookMethod = matchAll.group(1);
            String[] methodGivenArgsRaw;

            methodGivenArgsRaw = matchAll.group(2).split(",");


            List<String> methodGivenArgs = new ArrayList<>();

            for (String s : methodGivenArgsRaw) {
                s = s.trim();
                if (s.startsWith("'")) {
                    Matcher m = Pattern.compile("'(.*?)'").matcher(s);
                    if (m.find()) {
                        methodGivenArgs.add(m.group(1));
                    }
                } else {
                    if (s.matches("^.*?\\(.*\\)"))
                        methodGivenArgs.addAll(executeValueHook(command, s, executingUser, server, channel, msg));
                }
            }


            Method hookMethodObj = hookMethods.get(hookMethod);

            try {
                if (hookMethodObj.getAnnotation(HookMethod.class).hidden() && command.isDefaultCommand() && hookMethodObj.getAnnotation(HookMethod.class).hasReturnValue()) {
                    return (List<String>) hookMethodObj.invoke(null, channel, executingUser, server, msg, methodGivenArgs);
                } else if (!hookMethodObj.getAnnotation(HookMethod.class).hidden() && hookMethodObj.getAnnotation(HookMethod.class).hasReturnValue()) {
                    return (List<String>) hookMethodObj.invoke(null, channel, executingUser, server, msg, methodGivenArgs);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static void executeHook(Command command ,String action, User executingUser, Guild server, MessageChannel channel, Message msg)
    {


        Matcher matchAll = Pattern.compile("(^.*?)\\((.*?)\\)$").matcher(action);


        if(matchAll.matches()) {
            String hookMethod = matchAll.group(1);
            String[] methodGivenArgsRaw;

            methodGivenArgsRaw = matchAll.group(2).split(",");


            List<String> methodGivenArgs = new ArrayList<>();

            for (String s : methodGivenArgsRaw) {

                s = s.trim();
                if (s.startsWith("'")) {
                    Matcher m = Pattern.compile("'(.*?)'").matcher(s);
                    if (m.find()) {
                        methodGivenArgs.add(m.group(1));
                    }
                } else {
                    if (s.matches("^.*?\\(.*\\)"))
                        methodGivenArgs.addAll(executeValueHook(command, s, executingUser, server, channel, msg));
                }
            }


            try {
                if (hookMethods.get(hookMethod).getAnnotation(HookMethod.class).hidden() && command.isDefaultCommand()) {
                    hookMethods.get(hookMethod).invoke(null, channel, executingUser, server, msg, methodGivenArgs);
                } else if (!hookMethods.get(hookMethod).getAnnotation(HookMethod.class).hidden()) {
                    hookMethods.get(hookMethod).invoke(null, channel, executingUser, server, msg, methodGivenArgs);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.getCause().printStackTrace();
                //e.printStackTrace();
            }
        }
    }



    public static void loadHookMethods()
    {
        hookMethods = new HashMap<>();
        Method[] ClassMethods = CommandProcessor.class.getDeclaredMethods();
        for(Method m:ClassMethods)
        {
            if((m.getModifiers() & Modifier.STATIC)==0)
            {
                continue;
            }
            if(m.getAnnotation(HookMethod.class)!=null)
            {
                hookMethods.put(m.getAnnotation(HookMethod.class).name(),m);
            }
        }
    }


    @HookMethod(name = "reply",hidden = false,hasReturnValue = false)
    public static void replyCommand(MessageChannel channelSent, User userSent, Guild server, Message msg, List<String> methodArgs)
    {
        channelSent.sendMessage(String.join("\n",methodArgs)).queue();
    }

    @HookMethod(name = "help",hidden = true, hasReturnValue = true)
    public static List<String> helpCommand(MessageChannel channelSent, User userSent, Guild server,Message msg, List<String> methodArgs)
    {

        List<String> lines = new ArrayList<>();
        if(!methodArgs.get(0).trim().isEmpty()) {
            File f = CommandParser.findCommandFile(methodArgs.get(0), server);

            if (f == null) {
                channelSent.sendMessage("Specified Command wasn't found").queue();
            }
            Gson gson = new Gson();

            Command comToHelp = null;

            try ( FileInputStream fis = new FileInputStream(f)){
                comToHelp = gson.fromJson(new InputStreamReader(fis), new TypeToken<Command>() {
                }.getType());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            lines.add("Command: " + comToHelp.getName());
            lines.add("Usage: " + CommandParser.defaultPrefix + comToHelp.getName() + " " + String.join(" ", comToHelp.getArgumentNames()));
            lines.add(comToHelp.getDescription());



        }
        else
        {
            File serverCommandDir = new File(EpicDiscordBot.dataDirectory.getPath()+"/"+server.getId());
            File defaultCommandDir = new File(EpicDiscordBot.dataDirectory.getPath()+"/default");

            List<File> filesserver = Arrays.asList(serverCommandDir.listFiles());
            List<File> filesdefault = Arrays.asList(defaultCommandDir.listFiles());

            lines.add(server.getName()+ " commands: ");
            for (File f : filesserver)
            {
                lines.add(CommandParser.defaultPrefix+f.getName().replace(".json",""));
            }
            lines.add("");
            lines.add("Default commands: ");
            for (File f : filesdefault)
            {
                lines.add(CommandParser.defaultPrefix+f.getName().replace(".json",""));
            }
            lines.add("");
            lines.add("For info about a command type: !help <command>");
        }

        return lines;

    }

    @HookMethod(name = "privateMessage",hidden = false,hasReturnValue = false)
    public static void pmCommand(MessageChannel channelSent, User userSent, Guild server,Message msg, List<String> methodArgs)
    {
        userSent.openPrivateChannel().queue((channel) -> channel.sendMessage(String.join("\n", methodArgs)).queue());
    }

    @HookMethod(name = "reactToCommand", hidden = false, hasReturnValue = false)
    public static void reactToCommand(MessageChannel channelSent, User userSent, Guild server,Message msg, List<String> methodArgs)
    {
        if(methodArgs.size() == 1)
        {
            String emote = methodArgs.get(0);
            Matcher m = Pattern.compile("<:.*:(\\d*)>").matcher(emote);

            if(m.matches())
            {
                String id = m.group(1);

                Emote e = server.getEmoteById(id);

                if(e!=null)
                msg.addReaction(e).queue();
                else {
                   // System.out.println("emote not found");
                }
            }
            else if(emote.matches("[\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee]")){

                msg.addReaction(emote).queue();
            }
            //System.out.println(emote);
        }
    }

    @HookMethod(name = "reactToPrevious", hidden = false, hasReturnValue = false)
    public static void reactToPreviousCommand(MessageChannel channelSent, User userSent, Guild server,Message msg, List<String> methodArgs)
    {
        if(methodArgs.size() == 2)
        {
            String emote = methodArgs.get(0);
            String msgNum = methodArgs.get(1);

            if(msgNum.matches("\\d+"))
            {

                Matcher m = Pattern.compile("<:.*:(\\d*)>").matcher(emote);
                MessageHistory history= channelSent.getHistory();
                Message target ;
                target = history.retrievePast(Integer.parseInt(msgNum)).complete().get(Integer.parseInt(msgNum)-1);

                if(m.matches())
                {
                    String id = m.group(1);

                    Emote e = server.getEmoteById(id);

                    if(e!=null)
                        target.addReaction(e).queue();
                    else {
                        // System.out.println("emote not found");
                    }
                }
                else if(emote.matches("[\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee]")){

                    target.addReaction(emote).queue();
                }
            }
        }
    }

    @HookMethod(name = "deleteCommand", hidden = false,hasReturnValue = false)
    public static void deleteWrittenCommand(MessageChannel channelSent, User userSent, Guild server,Message msg, List<String> methodArgs)
    {
        msg.delete().queue();
    }

    @HookMethod(name = "purge", hidden = false, hasReturnValue = false)
    public static void purgeMessagesCommand(MessageChannel channelSent, User userSent, Guild server,Message msg, List<String> methodArgs)
    {
        if(methodArgs.size()==1) {

            String msgNum = methodArgs.get(0);

            if(msgNum.matches("\\d+")) {

                MessageHistory history = channelSent.getHistory();
                history.retrievePast(Integer.parseInt(msgNum)+1).queue((list) -> list.forEach((target) -> {
                    if(!target.equals(msg)) {
                        target.delete().queue();
                    }
                }));
            }
        }
    }


}
