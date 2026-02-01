package org.rebelland.pipisa.command.lists;

import org.mineacademy.fo.remain.CompMaterial;
import org.rebelland.pipisa.model.SimpleQuests;

import java.util.ArrayList;
import java.util.List;

public class QuestList {

    public static final List<SimpleQuests> QUESTS = new ArrayList<>();

    static {
        // Прогресс 0, isCompleted = false
        QUESTS.add(new SimpleQuests(CompMaterial.OAK_LOG, 16, 0, "&6&lНачало", "&eДобудь 16 дуба", false));
        QUESTS.add(new SimpleQuests(CompMaterial.STONE, 32, 0, "&6&lПервые шаги", "&eДобудь 32 камня", false));
        QUESTS.add(new SimpleQuests(CompMaterial.IRON_ORE, 16, 0, "&6&lШахтёр", "&eДобудь 16 железа", false));
    }
}
