package com.minersstudios.msessentials.commands.admin;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.msessentials.Cache;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.map.IDMap;
import com.minersstudios.msessentials.utils.IDUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;

@MSCommand(
        command = "whitelist",
        usage = " ꀑ §cИспользуй: /<command> [add/remove/reload] [id/никнейм]",
        description = "Удаляет/добавляет игрока в вайтлист, или перезагружает его",
        permission = "msessentials.whitelist",
        permissionDefault = PermissionDefault.OP
)
public class WhitelistCommand implements MSCommandExecutor {
    private static final List<String> TAB = List.of("add", "remove", "reload");
    private static final CommandNode<?> COMMAND_NODE =
            literal("whitelist")
            .then(
                    literal("add")
                    .then(argument("никнейм", StringArgumentType.word()))
            )
            .then(
                    literal("remove")
                    .then(argument("id/никнейм", StringArgumentType.word()))
            )
            .then(literal("reload"))
            .build();

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (args.length == 0) return false;

        Server server = sender.getServer();

        if (args[0].equalsIgnoreCase("reload")) {
            server.reloadWhitelist();
            MSLogger.fine(sender, Component.translatable("ms.command.white_list.reload"));
            return true;
        }

        Cache cache = MSEssentials.getCache();
        TranslatableComponent removedFormat = Component.translatable("ms.command.white_list.remove.sender.message");
        TranslatableComponent alreadyFormat = Component.translatable("ms.command.white_list.add.already");

        if (args.length > 1 && IDUtils.matchesIDRegex(args[1])) {
            if (args[0].equalsIgnoreCase("add")) {
                MSLogger.warning(sender, Component.translatable("ms.command.white_list.add.nickname_warning"));
                return true;
            }

            if (args[0].equalsIgnoreCase("remove")) {
                IDMap idMap = cache.idMap;
                OfflinePlayer offlinePlayer = idMap.getPlayerByID(args[1]);

                if (offlinePlayer == null || offlinePlayer.getName() == null) {
                    MSLogger.severe(sender, Component.translatable("ms.error.id_not_found"));
                    return true;
                }

                PlayerInfo playerInfo = PlayerInfo.fromProfile(offlinePlayer.getUniqueId(), offlinePlayer.getName());

                if (playerInfo.setWhiteListed(false)) {
                    MSLogger.fine(
                            sender,
                            removedFormat.args(
                                    playerInfo.getGrayIDGreenName(),
                                    text(offlinePlayer.getName())
                            )
                    );
                    return true;
                }

                MSLogger.warning(
                        sender,
                        alreadyFormat.args(
                                playerInfo.getGrayIDGoldName(),
                                text(offlinePlayer.getName())
                        )
                );
                return true;
            }
            return false;
        }
        if (args.length > 1 && args[1].length() > 2) {
            OfflinePlayer offlinePlayer = server.getOfflinePlayer(args[1]);
            PlayerInfo playerInfo = PlayerInfo.fromProfile(offlinePlayer.getUniqueId(), args[1]);

            if (args[0].equalsIgnoreCase("add")) {
                if (playerInfo.setWhiteListed(true)) {
                    MSLogger.fine(
                            sender,
                            Component.translatable(
                                    "ms.command.white_list.add.sender.message",
                                    playerInfo.getGrayIDGreenName(),
                                    text(args[1])
                            )
                    );
                    return true;
                }

                MSLogger.warning(
                        sender,
                        alreadyFormat.args(
                                playerInfo.getGrayIDGoldName(),
                                text(args[1])
                        )
                );
                return true;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (playerInfo.setWhiteListed(false)) {
                    MSLogger.fine(
                            sender,
                            removedFormat.args(
                                    playerInfo.getGrayIDGreenName(),
                                    text(args[1])
                            )
                    );
                    return true;
                }

                MSLogger.warning(
                        sender,
                        Component.translatable(
                                "ms.command.white_list.remove.not_found",
                                playerInfo.getGrayIDGoldName(),
                                text(args[1])
                        )
                );
                return true;
            }
            return false;
        }

        MSLogger.warning(sender, Component.translatable("ms.error.name_length"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            return TAB;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            for (var offlinePlayer : sender.getServer().getWhitelistedPlayers()) {
                PlayerInfo playerInfo = PlayerInfo.fromProfile(offlinePlayer.getUniqueId(), args[1]);
                int id = playerInfo.getID(false, false);

                if (id != -1) {
                    completions.add(String.valueOf(id));
                }

                completions.add(offlinePlayer.getName());
            }
        }
        return completions;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
