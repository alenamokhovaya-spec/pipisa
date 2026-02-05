package org.rebelland.pipisa.config;

import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.YamlConfig;
import org.rebelland.pipisa.model.QuestModel;

import java.util.ArrayList;
import java.util.List;

public class QuestConfig extends YamlConfig {

    private static QuestConfig instance;

    private List<QuestModel> questsList = new ArrayList<>();

    public static QuestConfig getInstance(){
        if (instance == null) {
            instance = new QuestConfig();
        }
        return instance;
    }

    private QuestConfig() {
        this.loadConfiguration("quests_list.yml");
        loadQuests();
    }

    private void loadQuests() {
        if (isSet("quests")) {
            for (String questId : getMap("quests").keySet()) {
                questsList.add(new QuestModel(
                        CompMaterial.fromString(getString("quests." + questId + ".block")),
                        getInteger("quests." + questId + ".amount"),
                        0,
                        false,
                        getString("quests." + questId + ".name"),
                        getString("quests." + questId + ".lore")
                ));
            }
        }
    }

    public List<QuestModel> getQuestsList() {
        return new ArrayList<>(questsList);
    }

    public QuestModel findQuestTemplate(CompMaterial material, int amount) {
        for (QuestModel quest : questsList) {
            if (quest.getBlock() == material && quest.getAmount() == amount) {
                return quest;
            }
        }
        return null;
    }
}