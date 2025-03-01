package com.minersstudios.whomine.scheduler.task;

import com.minersstudios.whomine.Cache;
import com.minersstudios.whomine.WhoMine;
import com.minersstudios.whomine.player.collection.PlayerInfoMap;
import com.minersstudios.whomine.world.WorldDark;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

public final class PlayerListTask implements Runnable {
    private final Server server;
    private final Cache cache;

    public PlayerListTask(final @NotNull WhoMine plugin) {
        this.server = plugin.getServer();
        this.cache = plugin.getCache();
    }

    @Override
    public void run() {
        final var onlinePlayers = this.server.getOnlinePlayers();

        if (onlinePlayers.isEmpty()) {
            return;
        }

        final WorldDark worldDark = this.cache.getWorldDark();
        final PlayerInfoMap playerInfoMap = this.cache.getPlayerInfoMap();

        onlinePlayers.stream().parallel()
        .filter(player -> !worldDark.isInWorldDark(player))
        .forEach(player ->
                playerInfoMap
                .get(player)
                .savePlayerDataParams()
        );
    }
}
