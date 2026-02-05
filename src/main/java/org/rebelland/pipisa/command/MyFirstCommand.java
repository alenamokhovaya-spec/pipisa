package org.rebelland.pipisa.command;

import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommand;
import org.rebelland.pipisa.config.QuestConfig;

public class MyFirstCommand extends SimpleCommand {
    public MyFirstCommand() {
        super("mc");
    }

    //sender - тот кто вызвал команду

    @Override
    protected void onCommand() {
        // Реализация команды
        Player player = (Player) sender;
        player.sendTitle(Common.colorize("&d&lпук"), null, 1, 3, 1);
    }
}
