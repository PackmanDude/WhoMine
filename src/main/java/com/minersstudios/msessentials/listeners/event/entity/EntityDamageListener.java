package com.minersstudios.msessentials.listeners.event.entity;

import com.minersstudios.mscore.listener.event.MSEventListener;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.world.WorldDark;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

@MSEventListener
public final class EntityDamageListener extends AbstractMSListener<MSEssentials> {

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(final @NotNull EntityDamageEvent event) {
        if (
                event.getEntity() instanceof Player player
                && WorldDark.isInWorldDark(player)
        ) {
            event.setCancelled(true);
        }
    }
}
