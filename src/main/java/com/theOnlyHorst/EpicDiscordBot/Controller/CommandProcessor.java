package com.theOnlyHorst.EpicDiscordBot.Controller;

import com.theOnlyHorst.EpicDiscordBot.Model.Command;
import com.theOnlyHorst.EpicDiscordBot.Model.CommandState;
import com.udojava.evalex.Expression;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandProcessor {


    //TODO ein Arschhaufen Fehlerprüfungen

    private static Map<String,Method> hookMethods;


    public static void executeCommand(Command command, ArrayList<String> args, User executingUser, Guild server, MessageChannel channel, Message msg)
    {
        CommandState commandState = new CommandState();
        while (command.getArgumentNames().size()>args.size())
        {
            args.add("");
        }
        for (String c : command.getActions()) {



                String endAction = c;
                if (c.contains("$")) {


                    for (String a : command.getArgumentNames()) {
                        if (c.contains("$" + a)) {
                            endAction = endAction.replace("$" + a, args.get(command.getArgumentNames().indexOf(a)));
                        }
                    }

                }
                boolean finishLoop = true;
                for (boolean condstate : commandState.getConditionStates()) {
                    if (endAction.startsWith(":")) {
                        if (condstate)
                            endAction = endAction.replaceFirst(":", "");
                        else
                            finishLoop = false;

                    } else if (endAction.startsWith("!")) {
                        if (!condstate) {
                            endAction = endAction.replaceFirst("!", "");
                        } else
                            finishLoop = false;
                    } else if (endAction.equals("endAssert")) {
                        if (commandState.getConditionStates().size() > 0) {
                            commandState.getConditionStates().remove(commandState.getConditionStates().size() - 1);
                        }
                        finishLoop = false;
                        break;
                    } else {
                        throw new CommandParsingException("action is missing an on false or on true prefix");
                    }
                }

                if (!finishLoop) {
                    continue;
                }
            /*if(c.startsWith(":"))
            {
                if(commandState.getConditionStates().size()>0) {
                    if (commandState.getConditionStates().get(commandState.getConditionStates().size()-1)) {
                        endAction = endAction.replaceFirst(":","");
                    }
                    else
                    {
                        continue;
                    }
                }
                else
                {
                    throw new RuntimeException("Can't have on true value while not in an assert state");
                }

            }else if(c.startsWith("!"))
            {
                if(commandState.getConditionStates().size()>0) {
                    if (!commandState.getConditionStates().get(commandState.getConditionStates().size()-1)) {
                        endAction = endAction.replaceFirst("!", "");
                    } else {
                        continue;
                    }
                }
                else
                {
                    throw new RuntimeException("Can't have on false value while not in an assert state");
                }
            }
            else*/
                if (!c.matches("[!:].+?")) {
                    if (commandState.getConditionStates().size() > 0) {
                        throw new CommandParsingException("Can't have non on true or on false value while in an assert state");
                    }
                }

                //DEBUG
                //channel.sendMessage(endAction).queue();
                //####################################

            try {
                executeHook(command, endAction, executingUser, server, channel, msg, commandState);
            }catch (CommandParsingException ex)
            {
                throw new RuntimeException("Command Parsing Error at Line: "+command.getActions().indexOf(c)+ " " + c,ex);
            }



        }
    }

    public static List<String> executeValueHook(Command command ,String action, User executingUser, Guild server, MessageChannel channel, Message msg,CommandState commandState)
    {
        String hookMethod = getHookMethodName(action);
        List<String> methodGivenArgs = resolveMethodGivenArgs(action, command, executingUser, server, channel,msg,commandState);


            Method hookMethodObj = hookMethods.get(hookMethod);

            try {
                if(hookMethodObj.getAnnotation(HookMethod.class).hasReturnValue())
                {
                    if (hookMethodObj.getAnnotation(HookMethod.class).hidden() && command.isDefaultCommand()) {
                        return (List<String>) hookMethodObj.invoke(null, channel, executingUser, server, msg, commandState, methodGivenArgs);
                    } else if (!hookMethodObj.getAnnotation(HookMethod.class).hidden()) {
                        return (List<String>) hookMethodObj.invoke(null, channel, executingUser, server, msg, commandState, methodGivenArgs);
                    }
                }
                else
                {
                    throw new CommandParsingException("Recursively called Hook Method does not have a return value");
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                throw new CommandParsingException("Hook Method Exception: "+ e.getTargetException().getMessage());
            }

        throw new CommandParsingException("Given Hook Method does not exist");
    }
    public static void executeHook(Command command , String action, User executingUser, Guild server, MessageChannel channel, Message msg, CommandState commandState)
    {

        String hookMethod = getHookMethodName(action);
        List<String> methodGivenArgs = resolveMethodGivenArgs(action, command, executingUser, server, channel,msg,commandState);



            try {
                if (hookMethods.get(hookMethod).getAnnotation(HookMethod.class).hidden() && command.isDefaultCommand()) {
                    hookMethods.get(hookMethod).invoke(null, channel, executingUser, server, msg,commandState ,methodGivenArgs);
                    return;
                } else if (!hookMethods.get(hookMethod).getAnnotation(HookMethod.class).hidden()) {
                    hookMethods.get(hookMethod).invoke(null, channel, executingUser, server, msg,commandState ,methodGivenArgs);
                    return;
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                throw new CommandParsingException(e.getTargetException().getMessage());
            }
            throw new CommandParsingException("Given Hook Method does not exist");

    }


    private static List<String> resolveMethodGivenArgs(String action, Command command, User executingUser, Guild server, MessageChannel channel, Message msg,CommandState commandState)
    {
        Matcher matchAll = Pattern.compile("^.*?\\((.*?)\\)$").matcher(action);
        List<String> methodGivenArgs = new ArrayList<>();

        if(matchAll.matches()) {

            String[] methodGivenArgsRaw;

            methodGivenArgsRaw = matchAll.group(1).split(",");

            for (String s : methodGivenArgsRaw) {

                s = s.trim();
                if (s.startsWith("'")) {
                    Matcher m = Pattern.compile("'(.*?)'").matcher(s);

                    if (m.find()) {
                        methodGivenArgs.add(m.group(1));
                    }
                } else {
                    Matcher m2 = Pattern.compile("#res:(.*?)#").matcher(s);
                    if(s.startsWith("?"))
                    {
                        methodGivenArgs.add(Boolean.toString(evaluateExpression(s)));
                    }else if(s.equalsIgnoreCase("true")||s.equalsIgnoreCase("false"))
                    {
                        methodGivenArgs.add(s);
                    }else if (m2.matches())
                    {
                        File f = FileReader.getResourceFile(server.getId(),m2.group(1));
                        commandState.getResourceFiles().add(f);
                        methodGivenArgs.add(m2.replaceFirst("#"+commandState.getResourceFiles().indexOf(f)+"#"));
                    }
                    else if (s.matches("^.*?\\(.*\\)")) {
                        methodGivenArgs.addAll(executeValueHook(command, s, executingUser, server, channel, msg, commandState));
                    }
                }
            }
        }
        return methodGivenArgs;
    }
    private static String getHookMethodName(String action) {
        Matcher matchAll = Pattern.compile("(^.*?)\\(.*?\\)$").matcher(action);
        String hookMethod ;
        if(matchAll.matches()) {
            hookMethod = matchAll.group(1);
        }
        else {
            hookMethod = null;
        }
        return hookMethod;
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
    public static void replyCommand(MessageChannel channelSent, User userSent, Guild server, Message msg,CommandState commandState, List<String> methodArgs)
    {
        File attachment = null;
        Pattern p = Pattern.compile("#(\\d+)#");
        for(String s:methodArgs)
        {
            Matcher m = p.matcher(s);
            if(m.matches())
            {
                attachment = commandState.getResourceFiles().get(Integer.parseInt(m.group(1)));
                methodArgs.remove(s);
                break;
            }
        }
        if(attachment==null)
        channelSent.sendMessage(String.join("\n",methodArgs)).queue();
        else
        {
            if(methodArgs.size()==0)
            {
                channelSent.sendFile(attachment).queue();
            }else
            channelSent.sendFile(attachment,String.join("\n",methodArgs)).queue();
        }
    }

    @HookMethod(name = "help",hidden = true, hasReturnValue = true)
    public static List<String> helpCommand(MessageChannel channelSent, User userSent, Guild server,Message msg,CommandState commandState, List<String> methodArgs)
    {

        List<String> lines = new ArrayList<>();
        if(!methodArgs.get(0).trim().isEmpty()) {

            Command comToHelp = FileReader.getCommand(methodArgs.get(0), server.getId());
            lines.add("Command: " + comToHelp.getName());
            lines.add("Usage: " + CommandParser.getServerPrefix(server.getIdLong()) + comToHelp.getName() + " " + String.join(" ", comToHelp.getArgumentNames()));
            lines.add(comToHelp.getDescription());

        }
        else
        {
            lines.add(server.getName()+ " commands: ");
            lines.addAll(FileReader.getCommandsForServer(server.getId()));
            lines.add("");
            lines.add("Default commands: ");
            lines.addAll(FileReader.getDefaultCommands(server.getId()));
            lines.add("");
            lines.add("For info about a command type: !help <command>");
        }

        return lines;

    }

    @HookMethod(name = "privateMessage",hidden = false,hasReturnValue = false)
    public static void pmCommand(MessageChannel channelSent, User userSent, Guild server,Message msg,CommandState commandState, List<String> methodArgs)
    {
        File attachment=null;
        Pattern p = Pattern.compile("#(\\d+)#");
        for(String s:methodArgs)
        {
            Matcher m = p.matcher(s);
            if(m.matches())
            {
                attachment = commandState.getResourceFiles().get(Integer.parseInt(m.group(1)));
                methodArgs.remove(s);
                break;
            }
        }
        if(attachment==null)
        userSent.openPrivateChannel().queue((channel) -> channel.sendMessage(String.join("\n", methodArgs)).queue());
        else
        {
            File finalAttachment = attachment;
            if(methodArgs.size()==0)
            userSent.openPrivateChannel().queue((channel) -> channel.sendFile(finalAttachment).queue());
            else
                userSent.openPrivateChannel().queue((channel) -> channel.sendFile(finalAttachment,String.join("\n", methodArgs)).queue());
        }
    }

    @HookMethod(name = "reactToCommand", hidden = false, hasReturnValue = false)
    public static void reactToCommand(MessageChannel channelSent, User userSent, Guild server,Message msg,CommandState commandState, List<String> methodArgs)
    {
        if(methodArgs.size() == 1)
        {
            String emote = methodArgs.get(0);

            reactToMsg(emote,server,msg);
            //System.out.println(emote);
        }
    }

    @HookMethod(name = "reactToPrevious", hidden = false, hasReturnValue = false)
    public static void reactToPreviousCommand(MessageChannel channelSent, User userSent, Guild server,Message msg,CommandState commandState, List<String> methodArgs)
    {
        if(methodArgs.size() == 2)
        {
            String emote = methodArgs.get(0);
            String msgNum = methodArgs.get(1);

            if(msgNum.matches("\\d+"))
            {


                MessageHistory history= channelSent.getHistory();
                Message target ;
                target = history.retrievePast(Integer.parseInt(msgNum)).complete().get(Integer.parseInt(msgNum)-1);

                reactToMsg(emote,server,target);
            }
        }
    }

    @HookMethod(name = "deleteCommand", hidden = false,hasReturnValue = false)
    public static void deleteWrittenCommand(MessageChannel channelSent, User userSent, Guild server,Message msg,CommandState commandState, List<String> methodArgs)
    {
        msg.delete().queue();
    }

    @HookMethod(name = "purge", hidden = false, hasReturnValue = false)
    public static void purgeMessagesCommand(MessageChannel channelSent, User userSent, Guild server,Message msg,CommandState commandState, List<String> methodArgs)
    {
        if(server.getMember(userSent).hasPermission(Permission.MESSAGE_MANAGE)) {
            if (methodArgs.size() == 1) {

                String msgNum = methodArgs.get(0);

                if (msgNum.matches("\\d+")) {

                    MessageHistory history = channelSent.getHistory();
                    history.retrievePast(Integer.parseInt(msgNum) + 1).queue((list) -> list.forEach((target) -> {
                        if (!target.equals(msg)) {
                            target.delete().queue();
                        }
                    }));
                }
            }
        }
    }

    private static void reactToMsg(String emote,Guild server,Message target)
    {
        Matcher m = Pattern.compile("<:.*:(\\d*)>").matcher(emote);
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
    @HookMethod(name = "assert", hidden = false, hasReturnValue = false)
    public static void evaluateConditionCommand(MessageChannel channelSent, User userSent, Guild server,Message msg,CommandState commandState, List<String> methodArgs)
    {
        boolean tmp = true;
        for (String methodArg : methodArgs) {
            if(methodArg.equalsIgnoreCase("false"))
            {
                tmp=false;
            }else if(!methodArg.equalsIgnoreCase("true"))
            {
                throw new CommandParsingException("given Values weren't boolean");
            }
            if(!tmp) break;
        }
        int conditionAmount = commandState.getConditionStates().size();
        if(tmp)
        {
            commandState.getConditionStates().add(true);
        }
        else
        {
            commandState.getConditionStates().add(false);
        }
    }

    @HookMethod(name = "isCondition",hidden = false,hasReturnValue = true)
    public static List<String> isConditionCommand(MessageChannel channelSent, User userSent, Guild server,Message msg,CommandState commandState, List<String> methodArgs)
    {
        List<String> value = new ArrayList<>();
        if(methodArgs.size()==1)
        {

            String expr = methodArgs.get(0);
            if(expr.startsWith("?")) {
                Expression exp = new Expression(expr.replace("?", ""));
                if (exp.isBoolean()) {
                    value.add("true");
                }
                else
                {
                    value.add("false");
                }
            }else
            {
                value.add("false");
            }
        }
        return value;
    }



    private static boolean evaluateExpression(String expr)
    {
        if(expr.startsWith("?"))
        {
            Expression exp = new Expression(expr.replace("?",""));
            if(!exp.isBoolean())
            {
                throw new CommandParsingException("the given Value: "+ expr + " was not an evaluation");
            }
            BigDecimal res = exp.eval();

            if(res.intValue()==0)
            {
                return false;
            }else
            {
                return true;
            }
        }
        else
        {
            throw new CommandParsingException("the given Value: "+ expr + " was not an evaluation because it didn't start with a ?");
        }
    }

    @HookMethod(name = "joinVoice", hidden = false , hasReturnValue = false)
    public static void joinVoiceChannel(MessageChannel channelSent, User userSent, Guild server,Message msg,CommandState commandState, List<String> methodArgs)
    {

    }






}
