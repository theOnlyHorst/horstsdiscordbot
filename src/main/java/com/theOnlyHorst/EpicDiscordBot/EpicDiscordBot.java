package com.theOnlyHorst.EpicDiscordBot;

import com.theOnlyHorst.EpicDiscordBot.Controller.CommandLineParser;
import com.theOnlyHorst.EpicDiscordBot.Controller.CommandParser;
import com.theOnlyHorst.EpicDiscordBot.Controller.CommandProcessor;
import com.theOnlyHorst.EpicDiscordBot.Model.Command;
import net.dv8tion.jda.bot.JDABot;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.net.URISyntaxException;

public class EpicDiscordBot extends ListenerAdapter {


    public static File dataDirectory;
    public static JDA jda;
    public static Guild botHome;

    public static void main(String[] args) throws LoginException, RateLimitedException {


        try {
            dataDirectory = new File(new File(EpicDiscordBot.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParentFile().getParentFile().getPath()+"/data");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if(!dataDirectory.exists())
            dataDirectory.mkdirs();



        CommandProcessor.loadHookMethods();


        jda = new JDABuilder(AccountType.BOT).setToken("NTE1NTI3NTg3MTE2NjEzNjQz.DtmaZw.-50l870oJQjlvdYHCDoC7RzbLMA").addEventListener(new EpicDiscordBot()).build();

        botHome = jda.getGuildById(515529753550258196L);

        Runnable consthread = CommandLineParser::startUpConsole;

        consthread.run();

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;
        Message message = event.getMessage();
        //event.getAuthor().openPrivateChannel().queue((channel)-> channel.sendMessage("test").queue());
        String content = message.getContentRaw();
        Guild server = event.getGuild();
        TextChannel ch = event.getTextChannel();
        User u = event.getAuthor();
        Member mem = event.getMember();
        CommandParser.parseCommand(server,content,ch,u,mem,message);



    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        super.onGuildJoin(event);
    }
}
