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
import org.rebelland.pipisa.api.QuestService;
import org.rebelland.pipisa.database.QuestDB;
import org.rebelland.pipisa.command.lists.QuestList;
import org.rebelland.pipisa.model.SimpleQuests;

import java.util.UUID;

public class MenuQuests extends TrackedMenu {

    private final UUID uuid;

    // Текущий шаблон квеста (из списка QUESTS)
    private SimpleQuests activeTemplate;
    // Данные игрока по этому квесту (из базы/кеша)
    private SimpleQuests playerQuestData;

    /* Кнопки */
    private Button infoButton;
    private Button actionButton;

    public MenuQuests(UUID uuid) {
        setTitle(Common.colorize("&8Меню Квестов"));
        setSize(9 * 4); // Увеличил размер для удобства
        this.uuid = uuid;

        // Загружаем данные перед открытием
        calculateActiveQuest();
    }

    // Логика поиска текущего актуального квеста
    private void calculateActiveQuest() {
        this.activeTemplate = null;
        this.playerQuestData = null;

        QuestService service = QuestService.getInstance();

        for (SimpleQuests template : QuestList.QUESTS) {
            // Получаем данные игрока из сервиса (кеша)
            SimpleQuests data = service.getQuest(uuid, template.getBlock());

            // Если данных нет (квест не начат) - это наш текущий активный квест (на выдачу)
            if (data == null) {
                this.activeTemplate = template;
                this.playerQuestData = null;
                break;
            }

            // Если квест есть, но не завершен - это наш текущий активный квест (в процессе)
            if (!data.isCompleted()) {
                this.activeTemplate = template;
                this.playerQuestData = data;
                break;
            }

            // Если квест завершен, цикл продолжается к следующему
        }
    }

    @Override
    public ItemStack getItemAt(int slot) {
        // Слот 13 - Инфо, Слот 22 - Действие
        if (slot == 13 && infoButton != null) return infoButton.getItem();
        if (slot == 22 && actionButton != null) return actionButton.getItem();
        return NO_ITEM;
    }

    private void setupButtons() {
        // 1. Если все квесты пройдены
        if (activeTemplate == null) {
            infoButton = Button.makeDummy(ItemCreator.of(CompMaterial.EMERALD_BLOCK)
                    .name("&aПоздравляем!")
                    .lore("&7Вы прошли все доступные квесты.")
                    .hideTags(true));

            actionButton = Button.makeEmpty();
            return;
        }

        // 2. Кнопка информации (Слот 13) - Визуальная
        infoButton = Button.makeDummy(ItemCreator.of(activeTemplate.getBlock())
                .name(activeTemplate.getTitle())
                .lore(
                        "",
                        activeTemplate.getDescription(),
                        "",
                        getStatusLore()
                )
                .hideTags(true));


        // 3. Кнопка действия (Слот 22) - Функциональная
        actionButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType click) {

                // СЦЕНАРИЙ А: Квест еще не взят -> Выдаем квест
                if (playerQuestData == null) {
                    boolean success = QuestDB.getInstance().createQuest(uuid, activeTemplate);

                    if (success) {
                        // Важно: Обновляем кеш сервиса, чтобы он увидел новый квест
                        QuestService.getInstance().loadPlayer(uuid); // Перезагружаем или добавляем вручную

                        player.sendMessage(Common.colorize("&aВы приняли квест: " + activeTemplate.getTitle()));
                        restartMenu(); // Перерисовываем меню
                    } else {
                        player.sendMessage(Common.colorize("&cОшибка при создании квеста."));
                    }
                    return;
                }

                // СЦЕНАРИЙ Б: Квест в процессе -> Просто звук
                if (!playerQuestData.isCompleted()) {
                    player.sendMessage(Common.colorize("&eВыполните условия квеста, чтобы завершить его."));
                    return;
                }

                // СЦЕНАРИЙ В: Квест выполнен -> (Тут логика награды, если нужна)
                // Так как isCompleted уже true, мы просто переходим к следующему
                // В данной логике, если квест выполнен, цикл calculateActiveQuest сразу перейдет к следующему.
                // Но если мы хотим кнопку "Забрать награду", то нужно логику isCompleted менять только здесь.
                // Сейчас isCompleted ставится автоматически в SimpleQuests.
                // Предположим, что игрок просто нажимает "Далее".

                CompSound.ENTITY_PLAYER_LEVELUP.play(player);
                player.sendMessage(Common.colorize("&aКвест завершен! Переход к следующему..."));

                // Здесь можно выдать награду

                refreshAll();
            }

            @Override
            public ItemStack getItem() {
                if (playerQuestData == null) {
                    return ItemCreator.of(CompMaterial.LIME_DYE)
                            .name("&a&lНАЧАТЬ КВЕСТ")
                            .lore("&7Нажмите, чтобы принять задание")
                            .make();
                } else if (!playerQuestData.isCompleted()) {
                    return ItemCreator.of(CompMaterial.GRAY_DYE)
                            .name("&e&lВ ПРОЦЕССЕ")
                            .lore(
                                    "&7Прогресс: &f" + playerQuestData.getProgress() + "/" + playerQuestData.getMaxProgress(),
                                    "&7Добудьте необходимые ресурсы"
                            )
                            .make();
                } else {
                    return ItemCreator.of(CompMaterial.DIAMOND) // Или сундук
                            .name("&b&lВЫПОЛНЕНО")
                            .lore("&7Квест завершен! Ожидайте следующего.")
                            .glow(true)
                            .make();
                }
            }
        };
    }

    private String getStatusLore() {
        if (playerQuestData == null) return "&c⚠ Не начат";
        if (playerQuestData.isCompleted()) return "&a✔ Выполнен";
        return "&e➤ В прогрессе: " + playerQuestData.getProgress() + "/" + playerQuestData.getMaxProgress();
    }

    // Инициализация кнопок при создании
    {
        setupButtons();
    }

    @Override
    protected Object getTrackingKey() {
        return MenuQuests.getMenuTrackingKey(uuid);
    }

    public static String getMenuTrackingKey(UUID uuid) {
        return "quests_" + uuid;
    }
}