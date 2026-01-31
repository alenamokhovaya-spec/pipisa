package org.rebelland.pipisa.command.lists;

import org.mineacademy.fo.remain.CompMaterial;

public class QuestList {

    public static final SimpleQuests[] QUESTS = {

            new SimpleQuests(
                    CompMaterial.OAK_LOG,
                    16,
                    "&6&lНачало",
                    "&eДобудь 16 дуба"
            ),

            new SimpleQuests(
                    CompMaterial.STONE,
                    32,
                    "&6&lПервые шаги",
                    "&eДобудь 32 камня"
            ),

            new SimpleQuests(
                    CompMaterial.IRON_ORE,
                    16,
                    "&6&lШахтёр",
                    "&eДобудь 16 железа"
            )
    };
}
