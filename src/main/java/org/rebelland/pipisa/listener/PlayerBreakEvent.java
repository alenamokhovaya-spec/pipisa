package org.rebelland.pipisa.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.event.SimpleListener;
import org.mineacademy.fo.remain.CompMaterial;
import org.rebelland.pipisa.database.QuestDB;

import java.util.List;

@AutoRegister
public final class PlayerBreakEvent extends SimpleListener<BlockBreakEvent> {

    public PlayerBreakEvent() {
        super(BlockBreakEvent.class, EventPriority.HIGHEST, true);
    }

    @Override
    protected void execute(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        CompMaterial blockMaterial = CompMaterial.fromBlock(block);
        Common.log("BLOCK TYPE: " + block.getType().name());
        Common.log("COMP BLOCK: " + CompMaterial.fromBlock(block).name());


        // Получаем список блоков для квестов игрока
        Common.runAsync(() -> {
            List<CompMaterial> questBlocks = QuestDB.getInstance().getAllBlocksByUUID(player.getUniqueId());

            // Проверяем, содержится ли сломанный блок в списке квестовых блоков
            if (questBlocks.contains(blockMaterial)) {
                // Обновляем прогресс квеста
                QuestDB.getInstance().updateProgress(
                        player.getUniqueId(),
                        blockMaterial,
                        QuestDB.getInstance().getQuestProgress(player.getUniqueId(), blockMaterial).getProgress() + 1
                );
            }
        });
    }
}