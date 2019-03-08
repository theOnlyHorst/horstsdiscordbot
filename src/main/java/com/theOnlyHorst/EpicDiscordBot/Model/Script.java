package com.theOnlyHorst.EpicDiscordBot.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Script {

    private Long id;
    private User creator;
    private String fileName;
}
