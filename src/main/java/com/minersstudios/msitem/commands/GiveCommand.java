package com.minersstudios.msitem.commands;

import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msitem.item.CustomItemType;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class GiveCommand {
    private static final TranslatableComponent PLAYER_NOT_FOUND = translatable("ms.error.player_not_found");
    private static final TranslatableComponent PLAYER_NOT_ONLINE = translatable("ms.error.player_not_online");
    private static final TranslatableComponent WRONG_ITEM = translatable("ms.command.msitem.give.wrong_block");
    private static final TranslatableComponent WRONG_FORMAT = translatable("ms.error.wrong_format");
    private static final TranslatableComponent GIVE_SUCCESS = translatable("ms.command.msitem.give.success");

    public static boolean runCommand(
            @NotNull CommandSender sender,
            String @NotNull ... args
    ) {
        if (args.length < 3) return false;

        String playerArg = args[1];
        String itemArg = args[2];
        String amountArg = args.length == 4 ? args[3] : "1";
        PlayerInfo playerInfo = PlayerInfo.fromString(playerArg);

        if (playerInfo == null) {
            MSLogger.severe(sender, PLAYER_NOT_FOUND);
            return true;
        }

        Player player = playerInfo.getOnlinePlayer();

        if (player == null) {
            MSLogger.warning(sender, PLAYER_NOT_ONLINE);
            return true;
        }

        CustomItemType.fromKey(itemArg).ifPresentOrElse(customItem -> {
            int amount;

            try {
                amount = Integer.parseInt(amountArg);
            } catch (NumberFormatException ignore) {
                MSLogger.severe(sender, WRONG_FORMAT);
                return;
            }

            ItemStack itemStack = customItem.getItem();
            itemStack.setAmount(amount);

            player.getInventory().addItem(itemStack);
            MSLogger.fine(
                    sender,
                    GIVE_SUCCESS.args(
                            text(amount),
                            itemStack.displayName(),
                            playerInfo.getDefaultName()
                    )
            );
        }, () -> MSLogger.severe(sender, WRONG_ITEM));
        return true;
    }
}
