package com.theOnlyHorst.EpicDiscordBot.Persistence;

import com.theOnlyHorst.EpicDiscordBot.Model.AbstractPersistable;

import java.util.List;

public abstract class AbstractDao<P extends AbstractPersistable> implements Dao<P> {


    @Override
    public P findById(Long id) {
        return null;
    }

    @Override
    public List<P> findAll() {
        return null;
    }

    @Override
    public P save(P persistable) {
        return null;
    }

    @Override
    public P delete(P persistable) {
        return null;
    }
}
