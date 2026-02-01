package org.rebelland.pipisa.api;

import org.mineacademy.fo.remain.CompMaterial;
import org.rebelland.pipisa.command.lists.QuestList;
import org.rebelland.pipisa.menu.MenuQuests;
import org.rebelland.pipisa.model.SimpleQuests;
import org.rebelland.pipisa.database.QuestDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class QuestService {
    private final QuestDB repo = QuestDB.getInstance();

    // Кеш: UUID игрока -> Список его квестов
    private final Map<UUID, List<SimpleQuests>> cache = new ConcurrentHashMap<>();

    private static QuestService instance;

    public static QuestService getInstance() {
        if (instance == null) instance = new QuestService();
        return instance;
    }

    /**
     * Загружает данные игрока из БД в кеш (вызывать при Join)
     */
    public void loadPlayer(UUID uuid) {
        List<SimpleQuests> quests = repo.getPlayerQuests(uuid);
        cache.put(uuid, quests);
    }

    /**
     * Выгружает данные игрока из кеша (вызывать при Quit)
     */
    public void unloadPlayer(UUID uuid) {
        cache.remove(uuid);
    }

    /**
     * Получить список квестов из кеша
     */
    public List<SimpleQuests> getQuests(UUID uuid) {
        return cache.getOrDefault(uuid, new ArrayList<>());
    }

    /**
     * Найти конкретный квест игрока
     */
    public SimpleQuests getQuest(UUID uuid, CompMaterial block) {
        List<SimpleQuests> quests = getQuests(uuid);
        for (SimpleQuests quest : quests) {
            if (quest.getBlock() == block) {
                return quest;
            }
        }
        return null;
    }

    /**
     * Добавить прогресс (логика работает через кеш и сохраняет в БД)
     */
    public void addProgress(UUID uuid, CompMaterial block, int amount) {
        SimpleQuests quest = getQuest(uuid, block);

        if (quest != null && !quest.isCompleted()) {
            int newProgress = quest.getProgress() + amount;

            // Если прогресс превышает максимум, ставим максимум (или оставляем как есть, зависит от логики)
            if (newProgress > quest.getMaxProgress()) {
                newProgress = quest.getMaxProgress();
            }

            // Обновляем объект в кеше
            quest.setProgress(newProgress); // Внутри сеттера isCompleted станет true, если достигнут макс.

            // Сохраняем обновление в БД (желательно делать это асинхронно в реальном проекте)
            repo.saveQuestProgress(uuid, quest);
            MenuQuests.refreshAll(MenuQuests.getMenuTrackingKey(uuid));
        }
    }
}
