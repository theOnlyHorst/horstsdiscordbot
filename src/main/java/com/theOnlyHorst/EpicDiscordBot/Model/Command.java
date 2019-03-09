package com.theOnlyHorst.EpicDiscordBot.Model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Command {

    @SerializedName("id")
    private Long id;
    @SerializedName("creatorId")
    private Long creatorId;
    @SerializedName("defaultCommand")
    private boolean defaultCommand;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("argumentNames")
    private List<String> argumentNames;
    @SerializedName("actions")
    private List<String> actions;
}
