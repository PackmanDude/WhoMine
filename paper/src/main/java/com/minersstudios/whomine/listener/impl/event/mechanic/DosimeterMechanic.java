package com.minersstudios.whomine.listener.impl.event.mechanic;

import com.minersstudios.whomine.WhoMine;
import com.minersstudios.whomine.custom.anomaly.Anomaly;
import com.minersstudios.whomine.custom.item.CustomItem;
import com.minersstudios.whomine.custom.item.registry.Dosimeter;
import com.minersstudios.whomine.listener.api.EventListener;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.kyori.adventure.text.Component.text;

public final class DosimeterMechanic extends EventListener {

    public DosimeterMechanic(final @NotNull WhoMine plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerSwapHandItems(final @NotNull PlayerSwapHandItemsEvent event) {
        final Player player = event.getPlayer();
        final var players = this.getPlugin().getCache().getDosimeterPlayers();
        final EquipmentSlot equipmentSlot = players.get(player);

        if (equipmentSlot != null) {
            players.put(player, equipmentSlot == EquipmentSlot.HAND ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
        }
    }

    @EventHandler
    public void onPlayerItemHeld(final @NotNull PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();
        final var players = this.getPlugin().getCache().getDosimeterPlayers();
        final EquipmentSlot equipmentSlot = players.get(player);

        if (equipmentSlot == EquipmentSlot.HAND) {
            final ItemStack dosimeterItem = player.getInventory().getItem(event.getPreviousSlot());

            CustomItem.fromItemStack(dosimeterItem, Dosimeter.class)
            .ifPresent(dosimeter -> {
                final Dosimeter copy = dosimeter.copy();

                assert dosimeterItem != null;

                copy.setItem(dosimeterItem);
                copy.setEnabled(false);
                players.remove(player);
            });
        }
    }

    @EventHandler
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory inventory = event.getClickedInventory();
        final ClickType clickType = event.getClick();

        if (!(inventory instanceof final PlayerInventory playerInventory)) {
            return;
        }

        final var players = this.getPlugin().getCache().getDosimeterPlayers();
        final EquipmentSlot equipmentSlot = players.get(player);

        if (equipmentSlot == null) {
            return;
        }

        final ItemStack dosimeterItem = playerInventory.getItem(equipmentSlot);

        CustomItem.fromItemStack(dosimeterItem, Dosimeter.class)
        .ifPresent(dosimeter -> {
            final Dosimeter copy = dosimeter.copy();
            final EquipmentSlot newEquipmentSlot = equipmentSlot == EquipmentSlot.HAND ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;

            if (
                    clickType.isShiftClick()
                    || (clickType == ClickType.SWAP_OFFHAND
                    && equipmentSlot == EquipmentSlot.OFF_HAND
                    && event.getSlot() != playerInventory.getHeldItemSlot())
            ) {
                copy.setItem(clickType.isShiftClick() ? Objects.requireNonNull(event.getCurrentItem()) : dosimeterItem);
                copy.setEnabled(false);
                players.remove(player);

                return;
            }

            this.getPlugin().runTask(() -> {
                if (dosimeterItem.equals(playerInventory.getItem(newEquipmentSlot))) {
                    players.put(player, newEquipmentSlot);
                } else if (!dosimeterItem.equals(playerInventory.getItem(equipmentSlot))) {
                    copy.setItem(
                            clickType.isKeyboardClick()
                            ? dosimeterItem
                            : Objects.requireNonNull(event.getCursor())
                    );
                    copy.setEnabled(false);
                    players.remove(player);
                }
            });
        });
    }

    @EventHandler
    public void onPlayerDropItem(final @NotNull PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final var players = this.getPlugin().getCache().getDosimeterPlayers();
        final EquipmentSlot equipmentSlot = players.get(player);

        if (equipmentSlot != null) {
            final ItemStack drop = event.getItemDrop().getItemStack();
            final ItemStack itemStack = player.getInventory().getItem(equipmentSlot);

            CustomItem.fromItemStack(itemStack, Dosimeter.class)
            .ifPresent(dosimeter -> {
                if (CustomItem.fromItemStack(drop, Dosimeter.class).isEmpty()) {
                    return;
                }

                final Dosimeter copy = dosimeter.copy();

                copy.setItem(drop);
                copy.setEnabled(false);
                players.remove(player);
            });
        }
    }

    @EventHandler
    public void onPlayerQuit(final @NotNull PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final EquipmentSlot equipmentSlot = this.getPlugin().getCache().getDosimeterPlayers().remove(player);

        if (equipmentSlot != null) {
            final ItemStack itemStack = player.getInventory().getItem(equipmentSlot);

            CustomItem.fromItemStack(itemStack, Dosimeter.class)
            .ifPresent(dosimeter -> {
                final Dosimeter copy = dosimeter.copy();

                copy.setItem(itemStack);
                copy.setEnabled(false);
            });
        }
    }

    @EventHandler
    public void onPlayerInteract(final @NotNull PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) {
            return;
        }

        final Player player = event.getPlayer();
        final EquipmentSlot hand = event.getHand();

        if (
                hand == null
                || !hand.isHand()
        ) {
            return;
        }

        final ItemStack itemInHand = player.getInventory().getItem(hand);

        CustomItem.fromItemStack(itemInHand, Dosimeter.class)
        .ifPresent(dosimeter -> {
            final Dosimeter copy = dosimeter.copy();

            event.setCancelled(true);
            copy.setItem(itemInHand);
            copy.setEnabled(!copy.isEnabled());

            if (copy.isEnabled()) {
                this.getPlugin().getCache().getDosimeterPlayers().put(player, hand);
            } else {
                this.getPlugin().getCache().getDosimeterPlayers().remove(player, hand);
            }
        });
    }

    public static class DosimeterTask {
        private final WhoMine plugin;
        private final Map<Player, EquipmentSlot> players;

        public DosimeterTask(final @NotNull WhoMine plugin) {
            this.plugin = plugin;
            this.players = plugin.getCache().getDosimeterPlayers();
        }

        public void run() {
            if (this.players.isEmpty()) {
                return;
            }

            this.players
            .forEach((player, equipmentSlot) -> {
                if (!player.isOnline()) {
                    return;
                }

                final ItemStack itemStack = player.getInventory().getItem(equipmentSlot);

                if (CustomItem.fromItemStack(itemStack).orElse(null) instanceof final Dosimeter dosimeter) {
                    final Dosimeter copy = dosimeter.copy();

                    copy.setItem(itemStack);

                    if (copy.isEnabled()) {
                        final var radiiPlayerInside = new Object2DoubleOpenHashMap<Anomaly>();

                        for (final var anomaly : this.plugin.getCache().getAnomalies().values()) {
                            final double radiusInside = anomaly.getBoundingBox().getRadiusInside(player);

                            if (radiusInside != -1.0d) {
                                radiiPlayerInside.put(anomaly, radiusInside);
                            }
                        }

                        final var anomalyEntry = getEntryWithMinValue(radiiPlayerInside);
                        final List<Double> radii =
                                anomalyEntry == null
                                ? Collections.emptyList()
                                : anomalyEntry.getKey().getBoundingBox().getRadii();
                        final Double radius =
                                anomalyEntry == null
                                ? null
                                : anomalyEntry.getValue();

                        copy.setItem(itemStack);
                        copy.setScreenTypeByRadius(radii, radius);
                        player.sendActionBar(
                                text("Уровень радиации : ")
                                .append(text(radiusToLevel(radii, radius, player.getLocation())))
                                .append(text(" мк3в/ч"))
                        );

                        return;
                    }
                }

                this.players.remove(player);
            });
        }

        private static @NotNull String radiusToLevel(
                final @NotNull List<Double> radii,
                final @Nullable Double radius,
                final @NotNull Location loc
        ) {
            final var reversedRadii = new ObjectArrayList<>(radii);

            Collections.reverse(reversedRadii);

            final double indexOfRadius = reversedRadii.indexOf(radius);
            final double afterComma = Math.round(((Math.abs(loc.getX()) + Math.abs(loc.getY()) + Math.abs(loc.getZ())) % 1.0d) * 10.0d) / 10.0d;

            return (indexOfRadius == -1.0d ? 0.0d : indexOfRadius + 1.0d)
                    + Math.min(afterComma, 0.9d)
                    + String.valueOf(Math.min(Math.round(Math.random() * 10.0d), 9));
        }

        private static @Nullable Map.Entry<Anomaly, Double> getEntryWithMinValue(final @NotNull Map<Anomaly, Double> map) {
            Map.Entry<Anomaly, Double> minEntry = null;
            double minValue = Double.POSITIVE_INFINITY;

            for (final var entry : map.entrySet()) {
                final double value = entry.getValue();

                if (value < minValue) {
                    minValue = value;
                    minEntry = entry;
                }
            }

            return minEntry;
        }
    }
}
