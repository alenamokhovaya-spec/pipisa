package org.rebelland.pipisa.command;

import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommand;
import org.rebelland.pipisa.menu.MenuGiveAnItem;

public class Menushka extends SimpleCommand {
    public Menushka() {
        super("menu");
    }

    @Override
    protected void onCommand() {
        // Реализация команды
        new MenuGiveAnItem().displayTo((Player) sender);
    }
}
