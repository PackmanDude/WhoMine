package com.minersstudios.msessentials.listeners.event.player;

import com.minersstudios.mscore.listener.event.MSEventListener;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.world.WorldDark;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.jetbrains.annotations.NotNull;

@MSEventListener
public final class PlayerDropItemListener extends AbstractMSListener<MSEssentials> {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(final @NotNull PlayerDropItemEvent event) {
        if (WorldDark.isInWorldDark(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
