package com.theOnlyHorst.EpicDiscordBot.Model;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandState {

    public CommandState()
    {
        conditionStates = new ArrayList<>();
        resourceFiles = new ArrayList<>();
    }

    private List<Boolean> conditionStates;
    private List<File> resourceFiles;




}
