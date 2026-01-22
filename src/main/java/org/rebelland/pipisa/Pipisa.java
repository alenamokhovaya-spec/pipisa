package org.rebelland.pipisa;

import org.bukkit.plugin.java.JavaPlugin;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.rebelland.pipisa.command.Menushka;
import org.rebelland.pipisa.command.MyFirstCommand;

public final class Pipisa extends SimplePlugin {

    @Override
    public void onPluginStart() {
        // Plugin startup logic
        registerCommand(new MyFirstCommand());
        registerCommand(new Menushka());

    }

    @Override
    public void onPluginStop() {
        // Plugin shutdown logic
    }
}
