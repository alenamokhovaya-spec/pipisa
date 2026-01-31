package org.rebelland.pipisa.command;

import org.bukkit.entity.Player;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;
import org.rebelland.pipisa.database.QuestDB;
import org.rebelland.pipisa.menu.MenuQuests;

public class QuestCommand extends SimpleCommand {
    public QuestCommand(){ super("quests");};
    @Override
    protected void onCommand() {
        Player player = (Player) sender;
        new MenuQuests(player.getUniqueId()).displayTo(player);
    }
}
