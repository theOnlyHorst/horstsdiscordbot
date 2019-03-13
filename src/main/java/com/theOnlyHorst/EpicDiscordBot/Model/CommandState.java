package com.theOnlyHorst.EpicDiscordBot.Model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandState {

    public CommandState()
    {
        conditionStates = new ArrayList<>();
    }

    private List<Boolean> conditionStates;




}
