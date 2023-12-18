package com.minersstudios.msessentials.listeners.event.player;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSEventListener;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.ResourcePack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.jetbrains.annotations.NotNull;

@MSEventListener
public final class PlayerResourcePackStatusListener extends AbstractMSListener<MSEssentials> {

    @EventHandler
    public void onPlayerResourcePackStatus(final @NotNull PlayerResourcePackStatusEvent event) {
        final PlayerResourcePackStatusEvent.Status status = event.getStatus();
        final Player player = event.getPlayer();
        final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(this.getPlugin(), player);
        final ResourcePack.Type currentType = playerInfo.getPlayerFile().getPlayerSettings().getResourcePackType();

        if (
                currentType == ResourcePack.Type.NULL
                || status == PlayerResourcePackStatusEvent.Status.ACCEPTED
        ) {
            return;
        }

        playerInfo.completeResourcePackFuture(status);
    }
}
