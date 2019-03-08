package com.theOnlyHorst.EpicDiscordBot;

import com.theOnlyHorst.EpicDiscordBot.Controller.CommandParser;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

public class EpicDiscordBot extends ListenerAdapter {

    public static void main(String[] args) throws LoginException, RateLimitedException {

        

        new JDABuilder(AccountType.BOT).setToken("NTE1NTI3NTg3MTE2NjEzNjQz.DtmaZw.-50l870oJQjlvdYHCDoC7RzbLMA").addEventListener(new EpicDiscordBot()).buildAsync();

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;
        Message message = event.getMessage();
        String content = message.getRawContent();
        long id = event.getGuild().getIdLong();
        MessageChannel ch = event.getChannel();
        String returnMsg = CommandParser.parseCommand(id,content);
        if(returnMsg!=null && !returnMsg.isEmpty())
        ch.sendMessage(returnMsg).queue();


    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        super.onGuildJoin(event);
    }
}
