package com.theOnlyHorst.EpicDiscordBot.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class User {

    private Long id;
    @Setter
    private String name;

}
