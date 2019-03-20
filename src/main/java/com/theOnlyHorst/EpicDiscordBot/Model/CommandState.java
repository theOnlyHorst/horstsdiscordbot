package com.theOnlyHorst.EpicDiscordBot.Model;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class CommandState {

    public CommandState()
    {
        conditionStates = new ArrayList<>();
        resourceFiles = new ArrayList<>();
        stringLists = new HashMap<>();
        loadedResources = new HashMap<>();
    }

    private List<Boolean> conditionStates;
    private List<File> resourceFiles;
    private Map<String,List<String>> stringLists;

    private Map<String,ResourceLoadState> loadedResources;


    public enum ResourceLoadState
    {
        FILE,
        STRINGS,
        SOUND,
        NONE
    }



}
