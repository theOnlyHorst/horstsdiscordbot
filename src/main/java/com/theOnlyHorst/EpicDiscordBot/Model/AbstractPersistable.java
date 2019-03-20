package com.theOnlyHorst.EpicDiscordBot.Model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Getter
@MappedSuperclass
@EqualsAndHashCode
public abstract class AbstractPersistable {

    @Id
    @GeneratedValue
    private Long id;

    public void afterDelete()
    {
        this.id =null;
    }


}
