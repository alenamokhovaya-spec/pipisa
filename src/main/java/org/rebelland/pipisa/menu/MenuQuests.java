package org.rebelland.pipisa.menu;

import model.TrackedMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompSound;
import org.rebelland.pipisa.api.QuestCache;
import org.rebelland.pipisa.config.QuestConfig;  // Импортируем QuestConfig
import org.rebelland.pipisa.database.QuestDB;
import org.rebelland.pipisa.model.QuestModel;

import java.util.List;
import java.util.UUID;

public class MenuQuests extends TrackedMenu {

    private final UUID uuid;
    private final QuestConfig questConfig = QuestConfig.getInstance();

    private QuestModel activeTemplate;
    private QuestModel playerQuestData;

    @Position(13)
    private final Button infoButton;
    @Position(15)
    private final Button actionButton;

    public MenuQuests(UUID uuid) {
        setTitle(Common.colorize("&8Меню Квестов"));
        setSize(9 * 4);
        this.uuid = uuid;

        calculateActiveQuest();

        infoButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                // Информационная кнопка
            }

            @Override
            public ItemStack getItem() {
                if (activeTemplate == null)
                    return ItemCreator.of(CompMaterial.EMERALD_BLOCK)
                            .name("&aПоздравляем!")
                            .lore("&7Вы прошли все доступные квесты.")
                            .hideTags(true)
                            .make();

                return ItemCreator.of(activeTemplate.getBlock())
                        .name(Common.colorize(activeTemplate.getName()))
                        .lore(
                                "",
                                Common.colorize(activeTemplate.getLore()),
                                "",
                                getStatusLore()
                        )
                        .hideTags(true)
                        .make();
            }
        };

        actionButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType click) {
                // СЦЕНАРИЙ А: Квест еще не взят
                if (playerQuestData == null) {
                    QuestModel newPlayerQuest = new QuestModel(
                            activeTemplate.getBlock(),
                            activeTemplate.getAmount(),
                            0,
                            false,
                            activeTemplate.getName(),
                            activeTemplate.getLore()
                    );

                    boolean success = QuestDB.getInstance().createQuest(uuid, newPlayerQuest);

                    if (success) {
                        Common.runAsync(() -> {
                            QuestCache.getInstance().loadPlayer(uuid);

                            Common.runLater(() -> {
                                player.sendMessage(Common.colorize("&aВы приняли квест: " + activeTemplate.getName()));
                                calculateActiveQuest();
                                refreshAll();
                            });
                        });
                    } else {
                        player.sendMessage(Common.colorize("&cОшибка при создании квеста."));
                    }
                    return;
                }

                // СЦЕНАРИЙ Б: Квест в процессе
                if (!playerQuestData.getCompleted()) {
                    player.sendMessage(Common.colorize("&eВыполните условия квеста, чтобы завершить его."));
                    return;
                }

                // СЦЕНАРИЙ В: Квест выполнен
                CompSound.ENTITY_PLAYER_LEVELUP.play(player);
                player.sendMessage(Common.colorize("&aКвест завершен! Переход к следующему..."));

                // Здесь можно добавить награду
                // player.getInventory().addItem(...);

                calculateActiveQuest();
                refreshAll();
            }

            @Override
            public ItemStack getItem() {
                if (playerQuestData == null) {
                    return ItemCreator.of(CompMaterial.LIME_DYE)
                            .name("&a&lНАЧАТЬ КВЕСТ")
                            .lore("&7Нажмите, чтобы принять задание")
                            .make();
                } else if (!playerQuestData.getCompleted()) {
                    return ItemCreator.of(CompMaterial.GRAY_DYE)
                            .name("&e&lВ ПРОЦЕССЕ")
                            .lore(
                                    "&7Прогресс: &f" + playerQuestData.getProgress() + "/" + playerQuestData.getAmount(),
                                    "&7Добудьте необходимые ресурсы"
                            )
                            .make();
                } else {
                    return ItemCreator.of(CompMaterial.DIAMOND)
                            .name("&b&lВЫПОЛНЕНО")
                            .lore("&7Квест завершен! Ожидайте следующего.")
                            .glow(true)
                            .make();
                }
            }
        };
    }

    // Логика поиска текущего актуального квеста
    private void calculateActiveQuest() {
        this.activeTemplate = null;
        this.playerQuestData = null;

        QuestCache cache = QuestCache.getInstance();

        // Получаем квесты из конфига
        // Нужно добавить метод getQuestsList() в QuestConfig
        List<QuestModel> questTemplates = questConfig.getQuestsList();

        for (QuestModel template : questTemplates) {
            // Получаем данные игрока из кеша
            QuestModel playerData = cache.getQuest(uuid, template.getBlock());

            // Если данных нет (квест не начат)
            if (playerData == null) {
                this.activeTemplate = template;
                this.playerQuestData = null;
                break;
            }

            // Если квест есть, но не завершен
            if (!playerData.getCompleted()) {
                this.activeTemplate = template;
                this.playerQuestData = playerData;
                break;
            }

            // Если квест завершен, продолжаем поиск
        }
    }

    @Override
    public ItemStack getItemAt(int slot) {
        return super.getItemAt(slot);
    }

    private String getStatusLore() {
        if (playerQuestData == null) return "&c⚠ Не начат";
        if (playerQuestData.getCompleted()) return "&a✔ Выполнен";
        return "&e➤ В прогрессе: " + playerQuestData.getProgress() + "/" + playerQuestData.getAmount();
    }

    @Override
    protected Object getTrackingKey() {
        return MenuQuests.getMenuTrackingKey(uuid);
    }

    public static String getMenuTrackingKey(UUID uuid) {
        return "quests_" + uuid;
    }
}