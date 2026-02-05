package org.rebelland.pipisa.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.rebelland.pipisa.api.QuestCache;
import org.rebelland.pipisa.database.TreasuryRepository;

@AutoRegister
public final class PlayerJoinEventListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Common.runAsync(() ->{
            TreasuryRepository.getInstance().updatePlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
            QuestCache.getInstance().loadPlayer(event.getPlayer().getUniqueId());
        });
    }
}
