package com.theOnlyHorst.EpicDiscordBot.Persistence;

import com.theOnlyHorst.EpicDiscordBot.Model.AbstractPersistable;

import java.util.List;

public interface Dao<P extends AbstractPersistable> {

    P findById(Long id);

    List<P> findAll();

    P save(P persistable);

    P delete(P persistable);
}
