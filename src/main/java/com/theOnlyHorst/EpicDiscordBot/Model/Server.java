package com.theOnlyHorst.EpicDiscordBot.Model;

import lombok.*;

import javax.persistence.Entity;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@Entity
public class Server extends AbstractPersistable {


    private Long discordId;
    @Setter
    private String name;
    @Setter
    private String prefix;


}
