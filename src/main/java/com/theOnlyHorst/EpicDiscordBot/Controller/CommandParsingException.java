package com.theOnlyHorst.EpicDiscordBot.Controller;

import lombok.Getter;

public class CommandParsingException extends RuntimeException {

    @Getter
    private String commandLine;


    public CommandParsingException(String message)
    {
        super (message);
        this.commandLine =commandLine;
    }



}
