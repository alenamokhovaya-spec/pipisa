package org.rebelland.pipisa.menu;

import io.lumine.mythic.bukkit.utils.lib.jooq.impl.QOM;
import model.TrackedMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompSound;
import org.rebelland.pipisa.database.QuestDB;

import java.util.UUID;


public class MenuQuests extends TrackedMenu {

    private int amount;
    private Button questButton = Button.makeEmpty();
    private String currentProgress;
    private boolean completeded;
    private String toAccept;

    public MenuQuests(UUID uuid) {
        setTitle(Common.colorize("&a&lМеню Квеста"));
        setSize(9 * 3);

        Common.runAsync(() -> {
            this.amount = QuestDB.getInstance().getQuestCountByUUID(uuid.toString());
            Common.runLater(() -> {
                if (completeded){
                    amount+=1;
                }
                switch (amount) {
                    case 0:
                    case 1:
                        registerQuestButton(
                                uuid,
                                CompMaterial.OAK_LOG,
                                16,
                                Common.colorize("&6&lНачало"),
                                "&e&lДобудь 16 дуба.");
                        break;
                    case 2:
                        registerQuestButton(
                                uuid,
                                CompMaterial.STONE,
                                32,
                                Common.colorize("&6&lПервые шаги"),
                                "&e&lДобудь 32 камня.");
                        break;
                }
                restartMenu();
            });
        });
    }

    public void registerQuestButton(UUID uuid, CompMaterial block, int maxProgress, String nameItem, String loreItem) {
        this.completeded = false;
        if (amount == 0) {
            toAccept = (Common.colorize("&4&lНАЖМИ ЧТО-БЫ ПРИНЯТЬ"));
        } else {
            toAccept = (Common.colorize("&4&lНАЖМИ ЧТО-БЫ ЗАВЕРШИТЬ"));
        };
        Common.runAsync(() -> {
            try {
                this.currentProgress = ("&2Текущий прогресс: &a" + QuestDB.getInstance().getQuestProgress(uuid, block).getProgress() + "/" + QuestDB.getInstance().getQuestProgress(uuid, block).getMaxProgress());
            } catch (Exception e) {
                this.currentProgress = "0/0";
            }
            Common.runLater(() -> {
                this.questButton = new Button(13) {
                    @Override
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (amount == 0) {
                            Common.runAsync(()->{
                                QuestDB.getInstance().createQuest(uuid, block, maxProgress);
                                CompSound.UI_BUTTON_CLICK.play(player);
                            });
                            restartMenu();
                        } else {
                            Common.runAsync(() -> {
                                boolean completed = QuestDB.getInstance().isQuestCompleted(uuid, block);
                                if (completed) {
                                    Common.runLater(() -> {
                                        player.sendMessage(Common.colorize("&aКвест выполнен!"));
                                        CompSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player);
                                        MenuQuests.this.completeded = true;
                                        restartMenu();
                                    });
                                } else {
                                    Common.runLater(() -> {
                                        player.sendMessage(Common.colorize("&4Еще не выполнен квест"));
                                    });
                                }
                            });
                        }
                        restartMenu();
                    }

                    @Override
                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.GOLD_BLOCK)
                                .name(nameItem)
                                .lore(
                                        loreItem,
                                        Common.colorize(currentProgress),
                                        (toAccept))
                                .make();
                    }
                };

                // Перезапускаем меню после создания кнопки
                restartMenu();

            });
        });
    }

    @Override
    public ItemStack getItemAt(int slot) {
        if (slot == 13 && questButton != null) {
            return questButton.getItem();
        }
        return NO_ITEM;
    }

    @Override
    protected Object getTrackingKey() {
        return null;
    }
}
