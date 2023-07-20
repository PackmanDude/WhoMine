package com.minersstudios.msitem.commands;

import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msitem.items.CustomItem;
import com.minersstudios.msitem.items.RenameableItem;
import com.minersstudios.msitem.items.Typed;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class GiveCommand {

    public static boolean runCommand(@NotNull CommandSender sender, String @NotNull ... args) {
        if (args.length < 3) return false;
        if (args[1].length() > 2) {
            int amount = 1;
            Player player = Bukkit.getPlayer(args[1]);

            if (player == null) {
                MSLogger.severe(sender, translatable("ms.error.player_not_found"));
                return true;
            }

            ItemStack itemStack;
            RenameableItem renameableItem = MSPlugin.getGlobalCache().renameableItemMap.getByPrimaryKey(args[2]);
            CustomItem customItem = MSPlugin.getGlobalCache().customItemMap.getByPrimaryKey(args[2]);

            if (customItem == null) {
                if (renameableItem == null) {
                    MSLogger.severe(sender, translatable("ms.command.msitem.give.wrong_item"));
                    return true;
                } else {
                    itemStack = renameableItem.getResultItemStack();
                }
            } else {
                if (
                        customItem instanceof Typed typed
                        && args.length == 4
                        && !args[3].matches("\\d+")
                ) {
                    for (var type : typed.getTypes()) {
                        if (type.getNamespacedKey().getKey().equals(args[3])) {
                            customItem = typed.createCustomItem(type);
                        }
                    }
                }
                itemStack = customItem.getItemStack();
            }

            switch (args.length) {
                case 4, 5 -> {
                    try {
                        amount = Integer.parseInt(args[args.length - 1]);
                    } catch (NumberFormatException ignore) {
                        MSLogger.severe(sender, translatable("ms.error.wrong_format"));
                        return true;
                    }
                }
            }

            itemStack.setAmount(amount);
            player.getInventory().addItem(itemStack);
            MSLogger.info(
                    sender,
                    translatable(
                            "ms.command.msitem.give.success",
                            text(amount),
                            itemStack.displayName(),
                            text(player.getName())
                    )
            );
            return true;
        }

        MSLogger.warning(sender, translatable("ms.error.name_length"));
        return true;
    }
}
