package com.minersstudios.msblock.listeners.player;

import com.minersstudios.msblock.utils.CustomBlockUtils;
import com.minersstudios.msblock.utils.PlayerUtils;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerTeleportListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerTeleport(@NotNull PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        this.getPlugin().runTask(() -> {
            CustomBlockUtils.cancelAllTasksWithThisPlayer(player);
            PlayerUtils.removeSteps(player);
        });
    }
}
