package org.rebelland.pipisa.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.event.SimpleListener;
import org.mineacademy.fo.remain.CompMaterial;
import org.rebelland.pipisa.api.QuestCache;
import org.rebelland.pipisa.database.QuestDB;

import java.util.List;

public final class PlayerBreakEvent implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        CompMaterial blockMaterial = CompMaterial.fromBlock(block);
        Common.log("BLOCK TYPE: " + block.getType().name());
        Common.log("COMP BLOCK: " + CompMaterial.fromBlock(block).name());


        // Получаем список блоков для квестов игрока
        Common.runAsync(() -> {
            QuestCache.getInstance().addProgress(event.getPlayer().getUniqueId(), blockMaterial, 1);
        });
    }
}