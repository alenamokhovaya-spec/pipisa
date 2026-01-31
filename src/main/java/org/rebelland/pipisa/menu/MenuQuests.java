package org.rebelland.pipisa.menu;

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
import org.rebelland.pipisa.command.lists.QuestList;
import org.rebelland.pipisa.command.lists.SimpleQuests;

import java.util.UUID;

public class MenuQuests extends TrackedMenu {

    /* ===== КЭШ МЕНЮ ===== */
    private SimpleQuests activeQuest;
    private QuestDB.QuestProgress cachedProgress;

    private Button questButton = Button.makeEmpty();

    public MenuQuests(UUID uuid) {

        setTitle(Common.colorize("&a&lМеню Квестов"));
        setSize(9 * 3);

        loadDataAsync(uuid);
    }

 private void loadDataAsync(UUID uuid) {

        Common.runAsync(() -> {

            activeQuest = null;
            cachedProgress = null;

            for (SimpleQuests quest : QuestList.QUESTS) {

                QuestDB.QuestProgress progress =
                        QuestDB.getInstance().getQuestProgress(uuid, quest.block);

                if (progress == null || !progress.isCompleted()) {
                    activeQuest = quest;
                    cachedProgress = progress;
                    break;
                }
            }

            Common.runLater(() -> {
                buildButton(uuid);
                restartMenu();
            });
        });
    }

    private void buildButton(UUID uuid) {

        if (activeQuest == null) {
            questButton = new Button(13) {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {

                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.EMERALD_BLOCK)
                            .name(Common.colorize("&aВсе квесты выполнены!"))
                            .lore(Common.colorize("&7Возвращайся позже 😎"))
                            .make();
                }
            };
            return;
        }

        questButton = new Button(13) {

            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {

                Common.runAsync(() -> {

                    // Принятие
                    if (cachedProgress == null) {
                        QuestDB.getInstance().createQuest(
                                uuid,
                                activeQuest.block,
                                activeQuest.maxProgress
                        );

                        Common.runLater(() -> {
                            CompSound.UI_BUTTON_CLICK.play(player);
                            loadDataAsync(uuid);
                        });
                        return;
                    }

                    // Проверка
                    if (!cachedProgress.isCompleted()) {
                        CompSound.UI_BUTTON_CLICK.play(player);
                        Common.runLater(() -> {
                            player.sendMessage(Common.colorize("&eКвест ещё не выполнен"));
                            CompSound.UI_BUTTON_CLICK.play(player);
                        });
                        return;
                    }

                    // Завершение
                    Common.runLater(() -> {
                        player.sendMessage(Common.colorize("&aКвест выполнен!"));
                        CompSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player);
                        loadDataAsync(uuid);
                    });
                });
            }

            @Override
            public ItemStack getItem() {

                String progressLine;
                String actionLine;

                if (cachedProgress == null) {
                    progressLine = "&7Прогресс: 0/" + activeQuest.maxProgress;
                    actionLine = "&a▶ Нажми, чтобы принять";
                } else {
                    progressLine = "&aПрогресс: " +
                            cachedProgress.getProgress() + "/" +
                            cachedProgress.getMaxProgress();

                    actionLine = cachedProgress.isCompleted()
                            ? "&6✔ Нажми, чтобы завершить"
                            : "&e⏳ В процессе";
                }

                return ItemCreator.of(CompMaterial.GOLD_BLOCK)
                        .name(Common.colorize(activeQuest.title))
                        .lore(
                                Common.colorize(activeQuest.description),
                                Common.colorize(progressLine),
                                Common.colorize(actionLine)
                        )
                        .make();
            }
        };
    }

    @Override
    public ItemStack getItemAt(int slot) {
        return slot == 13 ? questButton.getItem() : NO_ITEM;
    }

    @Override
    protected Object getTrackingKey() {
        return null;
    }
}
