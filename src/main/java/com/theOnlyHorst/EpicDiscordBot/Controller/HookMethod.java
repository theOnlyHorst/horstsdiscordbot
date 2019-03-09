package com.theOnlyHorst.EpicDiscordBot.Controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HookMethod {

    String name();
    boolean hidden();
    boolean hasReturnValue();
}
