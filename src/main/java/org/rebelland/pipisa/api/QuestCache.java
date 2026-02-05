package org.rebelland.pipisa.api;

import org.mineacademy.fo.Common;
import org.mineacademy.fo.remain.CompMaterial;
import org.rebelland.pipisa.database.QuestDB;
import org.rebelland.pipisa.menu.MenuQuests;
import org.rebelland.pipisa.model.QuestModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.rebelland.pipisa.database.QuestDB;

public class QuestCache {
    private final QuestDB repo = QuestDB.getInstance();

    // Кеш: UUID игрока -> Список его квестов
    private final Map<UUID, List<QuestModel>> cache = new ConcurrentHashMap<>();

    private static QuestCache instance;

    public static QuestCache getInstance() {
        if (instance == null) instance = new QuestCache();
        return instance;
    }

    /**
     * Загружает данные игрока из БД в кеш (вызывать при Join)
     */
    public void loadPlayer(UUID uuid) {
        List<QuestModel> quests = repo.getPlayerQuests(uuid);
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
    public List<QuestModel> getQuests(UUID uuid) {
        return cache.getOrDefault(uuid, new ArrayList<>());
    }

    /**
     * Найти конкретный квест игрока
     */
    public QuestModel getQuest(UUID uuid, CompMaterial block) {
        List<QuestModel> quests = getQuests(uuid);
        for (QuestModel quest : quests) {
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
        QuestModel quest = getQuest(uuid, block);

        if (quest != null && !quest.getCompleted()) {
            int newProgress = quest.getProgress() + amount;

            // Если прогресс превышает максимум, ставим максимум (или оставляем как есть, зависит от логики)
            if (newProgress > quest.getAmount()) {
                newProgress = quest.getAmount();
            }

            // Обновляем объект в кеше
            quest.updateProgress(newProgress); // Внутри сеттера isCompleted станет true, если достигнут макс.

            // Сохраняем обновление в БД (желательно делать это асинхронно в реальном проекте)
            Common.runAsync(()->{
                repo.saveQuestProgress(uuid, quest);
                MenuQuests.refreshAll(MenuQuests.getMenuTrackingKey(uuid));
            });
        }
    }
}
