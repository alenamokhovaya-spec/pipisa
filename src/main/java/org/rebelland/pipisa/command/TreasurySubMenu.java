package org.rebelland.pipisa.command;

import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.rebelland.pipisa.database.TreasuryRepository;
import org.rebelland.pipisa.menu.MenuTreasury;

public class TreasurySubMenu extends SimpleSubCommand {
    public TreasurySubMenu() {
        super("menu");
        setMinArguments(1); // Требуется минимум 1 аргумент (название)
        setUsage("<название>");
    }
    @Override
    protected void onCommand(){
        // Получение названия казны из параметра
        final String treasuryName = args[0];
        new MenuTreasury(treasuryName).displayTo((Player)sender);
    }
}