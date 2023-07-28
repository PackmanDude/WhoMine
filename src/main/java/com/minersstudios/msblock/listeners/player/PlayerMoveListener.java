package com.minersstudios.msblock.listeners.player;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.customblock.CustomBlockRegistry;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.BlockUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerMoveListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block block = player.getLocation().subtract(0.0d, 0.15d, 0.0d).getBlock();

        if (
                player.getGameMode() != GameMode.SPECTATOR
                && !player.isFlying()
                && !player.isSneaking()
        ) {
            double distance = event.getFrom().distance(event.getTo());

            if (
                    distance != 0.0d
                    && MSBlock.getCache().stepMap.addDistance(player, distance)
                    && BlockUtils.isWoodenSound(block.getType())
            ) {
                Location stepLocation = block.getLocation().toCenterLocation();

                CustomBlockRegistry.fromBlockData(block.getBlockData())
                .orElse(CustomBlockRegistry.DEFAULT)
                .getSoundGroup().playStepSound(stepLocation);
            }
        }
    }
}
