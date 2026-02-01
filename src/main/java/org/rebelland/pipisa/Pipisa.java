package org.rebelland.pipisa;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.rebelland.pipisa.command.Menushka;
import org.rebelland.pipisa.command.MyFirstCommand;
import org.rebelland.pipisa.command.QuestCommand;
import org.rebelland.pipisa.command.TreasuryCommands;
import org.rebelland.pipisa.database.QuestDB;
import org.rebelland.pipisa.database.TreasuryRepository;
import org.rebelland.pipisa.listener.PlayerBreakEvent;
import org.rebelland.pipisa.listener.PlayerJoinEventListener;
import org.rebelland.pipisa.listener.PlayerBreakEvent;

public final class Pipisa extends SimplePlugin {

    @Override
    public void onPluginStart() {
        // Plugin startup logic
        registerCommand(new MyFirstCommand());
        registerCommand(new Menushka());
        TreasuryRepository.getInstance().initializeTables();
        registerEvents(new PlayerJoinEventListener());
        QuestDB.getInstance().initializeTablesQuest();
        registerCommand(new QuestCommand());
        registerEvents(new PlayerBreakEvent());

    }

    @Override
    public void onPluginStop() {
        // Plugin shutdown logic
    }
}
