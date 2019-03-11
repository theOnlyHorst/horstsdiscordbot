package com.theOnlyHorst.EpicDiscordBot.Controller;

import com.theOnlyHorst.EpicDiscordBot.EpicDiscordBot;

import java.util.Scanner;

public class CommandLineParser {

    public static void startUpConsole()
    {
        Scanner sc = new Scanner(System.in);

        while(true)
        {

            String command = sc.nextLine();
            if(command.startsWith("/"))
            {
                if("/quit".equals(command))
                {
                    EpicDiscordBot.jda.shutdown();
                    System.out.println(EpicDiscordBot.jda.getStatus().toString());
                    System.exit(0);
                    return;
                }
            }

        }


    }

}
