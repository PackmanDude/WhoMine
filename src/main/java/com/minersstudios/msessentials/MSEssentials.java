package com.minersstudios.msessentials;

import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msessentials.chat.ChatType;
import com.minersstudios.msessentials.discord.DiscordManager;
import com.minersstudios.msessentials.menu.DiscordLinkCodeMenu;
import com.minersstudios.msessentials.menu.ResourcePackMenu;
import com.minersstudios.msessentials.menu.SkinsMenu;
import com.minersstudios.msessentials.player.collection.PlayerInfoMap;
import com.minersstudios.msessentials.task.BanListTask;
import com.minersstudios.msessentials.task.MuteMapTask;
import com.minersstudios.msessentials.task.PlayerListTask;
import com.minersstudios.msessentials.task.SeatsTask;
import com.minersstudios.msessentials.world.WorldDark;
import fr.xephi.authme.AuthMe;
import fr.xephi.authme.api.v3.AuthMeApi;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.UnknownNullability;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.minersstudios.mscore.language.LanguageRegistry.Components.ON_DISABLE_MESSAGE_SUBTITLE;
import static com.minersstudios.mscore.language.LanguageRegistry.Components.ON_DISABLE_MESSAGE_TITLE;

/**
 * The main class of the MSEssentials plugin
 *
 * @see MSPlugin
 */
public final class MSEssentials extends MSPlugin<MSEssentials> {
    private static MSEssentials singleton;

    private Cache cache;
    private Config config;
    private Scoreboard scoreboardHideTags;
    private Team scoreboardHideTagsTeam;

    public static final String NAMESPACE = "msessentials";
    public static final String HIDE_TAGS_TEAM_NAME = "hide_tags";

    @Override
    public void load() {
        initClass(DiscordLinkCodeMenu.class);
        initClass(ResourcePackMenu.class);
        initClass(SkinsMenu.class);
    }

    @Override
    public void enable() {
        singleton = this;
        this.cache = new Cache(this);
        this.config = new Config(this);

        this.cache.load();
        this.config.reload();
        this.setupHideTags();
        this.setupAuthMe();

        this.runTask(WorldDark::init);
        this.runTaskTimer(new SeatsTask(this), 0L, 1L);
        this.runTaskTimer(new PlayerListTask(this), 6000L, 6000L);
        this.runTaskTimer(new MuteMapTask(this), 0L, 50L);
        this.runTaskTimer(new BanListTask(this), 0L, 6000L);
    }

    @Override
    public void disable() {
        this.kickAll();
        this.sendServerDisableMessage();

        if (!this.cache.isLoaded()) {
            this.cache.unload();
        }

        singleton = null;
    }

    /**
     * @return The cache of the plugin or null if the plugin is disabled
     */
    public @UnknownNullability Cache getCache() {
        return this.cache;
    }

    /**
     * @return The configuration of the plugin or null if the plugin is disabled
     */
    public @UnknownNullability Config getConfiguration() {
        return this.config;
    }

    /**
     * @return The player info of the console or null if the plugin is disabled
     */
    public @UnknownNullability Scoreboard getScoreboardHideTags() {
        return this.scoreboardHideTags;
    }

    /**
     * @return The player info of the console or null if the plugin is disabled
     */
    public @UnknownNullability Team getScoreboardHideTagsTeam() {
        return this.scoreboardHideTagsTeam;
    }

    /**
     * @return Singleton instance of the plugin or null if the plugin is disabled
     */
    public static @UnknownNullability MSEssentials singleton() {
        return singleton;
    }

    /**
     * @return The player info of the console or null if the plugin is disabled
     */
    public static @UnknownNullability Logger logger() {
        return singleton == null ? null : singleton.getLogger();
    }

    /**
     * @return The player info of the console or null if the plugin is disabled
     */
    public static @UnknownNullability ComponentLogger componentLogger() {
        return singleton == null ? null : singleton.getComponentLogger();
    }

    /**
     * @return The player info of the console or null if the plugin is disabled
     */
    public static @UnknownNullability Cache cache() {
        return singleton == null ? null : singleton.cache;
    }

    /**
     * @return The player info of the console or null if the plugin is disabled
     */
    public static @UnknownNullability Config config() {
        return singleton == null ? null : singleton.config;
    }

    private void setupHideTags() {
        this.scoreboardHideTags = this.getServer().getScoreboardManager().getNewScoreboard();
        this.scoreboardHideTagsTeam = this.scoreboardHideTags.registerNewTeam(HIDE_TAGS_TEAM_NAME);

        this.scoreboardHideTagsTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        this.scoreboardHideTagsTeam.setCanSeeFriendlyInvisibles(false);
    }

    private void setupAuthMe() {
        final Logger logger = this.getLogger();
        final PluginManager pluginManager = this.getServer().getPluginManager();

        try {
            final AuthMe authMe = AuthMeApi.getInstance().getPlugin();

            if (!authMe.isEnabled()) {
                logger.log(
                        Level.SEVERE,
                        "AuthMe is not enabled, MSEssentials will not work properly"
                );
                pluginManager.disablePlugin(this);
            }
        } catch (final Throwable e) {
            logger.log(
                    Level.SEVERE,
                    "AuthMe is not installed, MSEssentials will not work properly"
            );
            pluginManager.disablePlugin(this);
        }
    }

    private void kickAll() {
        final PlayerInfoMap playerInfoMap = this.cache.getPlayerInfoMap();
        final var onlinePlayers = this.getServer().getOnlinePlayers();

        if (
                playerInfoMap != null
                && !playerInfoMap.isEmpty()
                && !onlinePlayers.isEmpty()
        ) {
            for (final var player : onlinePlayers) {
                playerInfoMap
                .get(player)
                .kick(
                        player,
                        ON_DISABLE_MESSAGE_TITLE,
                        ON_DISABLE_MESSAGE_SUBTITLE,
                        PlayerKickEvent.Cause.RESTART_COMMAND
                );
            }
        }
    }

    private void sendServerDisableMessage() {
        final DiscordManager discordManager = this.cache.getDiscordHandler();

        if (discordManager != null) {
            discordManager.sendMessage(ChatType.GLOBAL, LanguageRegistry.Strings.DISCORD_SERVER_DISABLED);
            discordManager.sendMessage(ChatType.LOCAL, LanguageRegistry.Strings.DISCORD_SERVER_DISABLED);
        }
    }
}
