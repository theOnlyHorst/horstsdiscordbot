package com.theOnlyHorst.EpicDiscordBot;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
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

        


    }
}
