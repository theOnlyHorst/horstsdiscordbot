package com.theOnlyHorst.EpicDiscordBot.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
public class Server {

    private Long id;
    @Setter
    private String name;
    @Setter
    private String prefix;


}
