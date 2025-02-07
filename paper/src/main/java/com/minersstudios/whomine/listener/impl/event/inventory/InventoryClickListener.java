package com.minersstudios.whomine.listener.impl.event.inventory;

import com.minersstudios.whomine.WhoMine;
import com.minersstudios.whomine.custom.block.CustomBlockRegistry;
import com.minersstudios.whomine.custom.item.CustomItem;
import com.minersstudios.whomine.custom.item.Wearable;
import com.minersstudios.whomine.inventory.CustomInventory;
import com.minersstudios.whomine.inventory.InventoryButton;
import com.minersstudios.whomine.listener.api.EventListener;
import com.minersstudios.whomine.locale.Translations;
import com.minersstudios.whomine.utility.MSLogger;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

import static net.kyori.adventure.text.Component.text;

public final class InventoryClickListener extends EventListener {
    private static final int HELMET_SLOT = 39;
    private static final Set<InventoryType> IGNORABLE_INVENTORY_TYPES = EnumSet.of(
            //<editor-fold desc="Ignorable inventory types" defaultstate="collapsed">
            InventoryType.CARTOGRAPHY,
            InventoryType.BREWING,
            InventoryType.BEACON,
            InventoryType.BLAST_FURNACE,
            InventoryType.FURNACE,
            InventoryType.SMOKER,
            InventoryType.GRINDSTONE,
            InventoryType.STONECUTTER,
            InventoryType.SMITHING,
            InventoryType.LOOM,
            InventoryType.MERCHANT,
            InventoryType.ENCHANTING
            //</editor-fold>
    );

    public InventoryClickListener(final @NotNull WhoMine plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        final Inventory clickedInventory = event.getClickedInventory();
        final ClickType clickType = event.getClick();
        final ItemStack currentItem = event.getCurrentItem();
        final boolean isShiftClick = event.isShiftClick();

        if (clickedInventory != null) {
            if (event.getView().getTopInventory() instanceof final CustomInventory customInventory) {
                if (
                        clickedInventory.getType() == InventoryType.PLAYER
                        && (clickType.isShiftClick() || clickType == ClickType.DOUBLE_CLICK)
                ) {
                    event.setCancelled(true);
                }

                if (clickedInventory instanceof CustomInventory) {
                    final InventoryButton inventoryButton = customInventory.buttonAt(event.getSlot());

                    if (inventoryButton != null) {
                        inventoryButton.doClickAction(event, customInventory);
                    }

                    customInventory.doClickAction(event);

                    if (
                            customInventory.clickAction() == null
                            && !clickType.isCreativeAction()
                    ) {
                        event.setCancelled(true);
                    }
                } else if (clickedInventory instanceof PlayerInventory) {
                    customInventory.doBottomClickAction(event);
                }
            }

            final WhoMine plugin = this.getPlugin();
            final Player player = (Player) event.getWhoClicked();

            if (plugin.getCache().getWorldDark().isInWorldDark(player)) {
                event.setCancelled(true);
            }

            if (currentItem != null) {
                final int slot = event.getSlot();
                final ItemStack cursorItem = event.getCursor();

                if (
                        slot == HELMET_SLOT
                        && event.getSlotType() == InventoryType.SlotType.ARMOR
                        && currentItem.isEmpty()
                        && !cursorItem.isEmpty()
                ) {
                    player.setItemOnCursor(null);
                    plugin.runTask(
                            () -> player.getInventory().setHelmet(cursorItem)
                    );
                }

                if (!currentItem.isEmpty()) {
                    boolean remove = currentItem.getType() == Material.BEDROCK;

                    if (!remove) {
                        for (final var enchantment : currentItem.getEnchantments().keySet()) {
                            remove = currentItem.getEnchantmentLevel(enchantment) > enchantment.getMaxLevel();
                        }
                    }

                    if (remove) {
                        clickedInventory.setItem(slot, new ItemStack(Material.AIR));
                        MSLogger.warning(
                                Translations.INFO_PLAYER_ITEM_REMOVED.asTranslatable()
                                .arguments(
                                        player.name(),
                                        text(currentItem.toString())
                                )
                        );
                        event.setCancelled(true);
                    }
                }
            }
        }

        if (
                IGNORABLE_INVENTORY_TYPES.contains(event.getInventory().getType())
                && isShiftClick
                && CustomBlockRegistry.isCustomBlock(currentItem)
        ) {
            event.setCancelled(true);
        }

        final Player player = (Player) event.getWhoClicked();
        final PlayerInventory inventory = player.getInventory();
        final ItemStack cursorItem = event.getCursor();

        if (
                event.getSlot() == HELMET_SLOT
                && event.getSlotType() == InventoryType.SlotType.ARMOR
                && !cursorItem.getType().isAir()
        ) {
            CustomItem.fromItemStack(currentItem, Wearable.class)
            .ifPresent(w -> {
                assert currentItem != null;

                if (currentItem.getEnchantments().containsKey(Enchantment.BINDING_CURSE)) {
                    return;
                }

                this.getPlugin().runTask(() -> {
                    inventory.setHelmet(cursorItem);
                    player.setItemOnCursor(currentItem);
                });
            });
        }

        if (
                clickedInventory != null
                && currentItem != null
                && isShiftClick
                && clickedInventory.getType() == InventoryType.PLAYER
                && player.getOpenInventory().getType() == InventoryType.CRAFTING
                && inventory.getHelmet() == null
        ) {
            CustomItem.fromItemStack(currentItem, Wearable.class)
            .ifPresent(w -> {
                event.setCancelled(true);
                this.getPlugin().runTask(() -> {
                    inventory.setHelmet(currentItem);
                    currentItem.setAmount(0);
                });
            });
        }
    }
}
