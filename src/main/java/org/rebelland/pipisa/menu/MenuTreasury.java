package org.rebelland.pipisa.menu;

import model.TrackedMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompSound;
import org.rebelland.pipisa.database.TreasuryRepository;
import org.mineacademy.fo.menu.button.annotation.Position;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MenuTreasury extends TrackedMenu {

    @Position(10)
    private final Button depositButton;
    @Position(16)
    private final Button withdrawButton;
    @Position(13)
    private final Button getBalanceButton;

    private final String name;
    private final static Map<String, Integer> cache = new ConcurrentHashMap<>();

    public MenuTreasury(String treasuryName){
        setTitle(Common.colorize("&a&lМеню &2&l" + treasuryName));
        setSize(9 * 3);

        name = treasuryName;

        this.depositButton = new Button(){
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                if (HookManager.getBalance(player) >= 1000) {
                    HookManager.withdraw(player, 1000);
                    Common.runAsync(() -> {
                        TreasuryRepository.getInstance().deposit(treasuryName, 1000);
                        CompSound.UI_BUTTON_CLICK.play(player);
                        int amount = TreasuryRepository.getInstance().getBalance(treasuryName);
                        cache.put(treasuryName, amount);
                        Common.runLater(() -> {
                            player.sendMessage(Common.colorize("&2На счёт &a" + treasuryName + "&2 звчислена 1000. Текущий баланс: &a" + amount));
                        });
                        refreshAll();
                    });
                }
                else {
                    player.sendMessage(Common.colorize("&4Недостаточно средств. Текущий баланс: &c" + HookManager.getBalance(player)));
                }

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
                if (cache.get(treasuryName) >= 1000) {
                    HookManager.deposit(player, 1000);
                    Common.runAsync(() -> {
                        boolean withdraw = TreasuryRepository.getInstance().withdraw(treasuryName, 1000);
                        CompSound.UI_BUTTON_CLICK.play(player);
                        int amount = TreasuryRepository.getInstance().getBalance(treasuryName);
                        cache.put(treasuryName, amount);
                        Common.runLater(() -> {
                            if (withdraw) {
                                player.sendMessage(Common.colorize("&4Со счёта &c" + treasuryName + "&4 списана 1000. Текущий баланс: &c" + amount));
                                refreshAll();
                            } else {
                                player.sendMessage(Common.colorize("&4Недостаточно средств. Текущий баланс: &c" + amount));
                            }
                        });
                    });
                }
                else {
                    player.sendMessage(Common.colorize("&4Недостаточно средств в казне. Текущий баланс: &c" + cache.get(treasuryName)));
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
                Integer amount = cache.get(treasuryName);
                if (amount == null){
                    return ItemCreator.of(CompMaterial.DIRT)
                            .name(Common.colorize("&6&lТекущий баланс: &a&l" + 0))
                            .make();
                }
                return ItemCreator.of(CompMaterial.SUNFLOWER)
                        .name(Common.colorize("&6&lТекущий баланс: &a&l" + amount))
                        .make();
            }
        };
        Common.runAsync(()->{
            cache.put(treasuryName,TreasuryRepository.getInstance().getBalance(treasuryName));
            refreshAll();
        });

    }

    @Override
    protected Object getTrackingKey() {
        return name;
    }

}
