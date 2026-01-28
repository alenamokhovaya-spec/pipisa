package org.rebelland.pipisa.menu;

import model.TrackedMenu;
import moss.factions.shade.com.typesafe.config.ConfigException;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompSound;
import org.rebelland.pipisa.database.TreasuryRepository;
import org.mineacademy.fo.menu.button.annotation.Position;


public class MenuTreasury extends TrackedMenu {

    @Position(10)
    private final Button depositButton;
    @Position(16)
    private  final Button withdrawButton;
    @Position(13)
    private  final Button getBalanceButton;

    public MenuTreasury(String treasuryName){
        setTitle(Common.colorize("&a&lМеню &2&l" + treasuryName));
        setSize(9 * 3);
        Button button = new Button(){
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
            }
            @Override
            public ItemStack getItem() {
                return null;
            }
    };

    this.depositButton = new Button(){
        @Override
        public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
            TreasuryRepository.getInstance().deposit(treasuryName, 1000);
            CompSound.UI_BUTTON_CLICK.play(player);
            setItem(13, getBalanceButton.getItem());
            player.sendMessage(Common.colorize("&2На счёт &a" + treasuryName +"&2 звчислена 1000. Текущий баланс: &a" + TreasuryRepository.getInstance().getBalance(treasuryName)));
        }
        @Override
        public ItemStack getItem() {
            return ItemCreator.of(CompMaterial.LIME_BED)
                    .name(Common.colorize("&2&lПополнение баланса казны на: &a&l1000"))
                    .make();
        }
    };

    this.withdrawButton = new Button() {
        @Override
        public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
            boolean withdraw = TreasuryRepository.getInstance().withdraw(treasuryName, 1000);
            CompSound.UI_BUTTON_CLICK.play(player);
            if(withdraw){
                player.sendMessage(Common.colorize("&4Со счёта &c" + treasuryName +"&4 списана 1000. Текущий баланс: &c" + TreasuryRepository.getInstance().getBalance(treasuryName)));
                setItem(13, getBalanceButton.getItem());
            }
            else {
                player.sendMessage(Common.colorize("&4Недостаточно средств. Текущий баланс: &c" + TreasuryRepository.getInstance().getBalance(treasuryName)));
            }
        }
        @Override
        public ItemStack getItem() {
            return ItemCreator.of(CompMaterial.RED_BED)
                    .name(Common.colorize("&4&lСписание с баланса казны на: &c&l1000"))
                    .make();
        }
    };

    this.getBalanceButton = new Button() {
        @Override
        public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
        }

        @Override
        public ItemStack getItem() {
            return ItemCreator.of(CompMaterial.SUNFLOWER)
                    .name(Common.colorize("&6&lТекущий баланс: &a&l" +TreasuryRepository.getInstance().getBalance(treasuryName)))
                    .make();
        }
    };

    }

    @Override
    protected Object getTrackingKey() {
        return null;
    }

}
