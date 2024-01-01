package com.minersstudios.mscore.utility;

import com.minersstudios.mscustoms.custom.block.CustomBlockData;
import com.minersstudios.mscustoms.custom.block.CustomBlockRegistry;
import com.minersstudios.mscustoms.custom.decor.CustomDecorData;
import com.minersstudios.mscustoms.custom.decor.CustomDecorType;
import com.minersstudios.mscustoms.custom.item.CustomItem;
import com.minersstudios.mscustoms.custom.item.CustomItemType;
import com.minersstudios.mscustoms.custom.item.renameable.RenameableItemRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static com.minersstudios.mscore.utility.SharedConstants.*;

/**
 * Utility class for custom items / blocks / decor.
 * Allowed namespaces: msitems, msblock, msdecor.
 *
 * @see CustomItem
 * @see CustomBlockData
 * @see CustomDecorData
 */
public final class MSCustomUtils {

    @Contract(" -> fail")
    private MSCustomUtils() throws AssertionError {
        throw new AssertionError("Utility class");
    }

    /**
     * Gets an {@link ItemStack} of custom item / block / decor
     * from NamespacedKey string
     *
     * @param namespacedKeyStr NamespacedKey string,
     *                         example - (msitems:example)
     * @return Optional of {@link ItemStack} object
     *         or empty optional if not found
     * @see #getItemStack(String, String)
     */
    public static @NotNull Optional<ItemStack> getItemStack(final @Nullable String namespacedKeyStr) {
        if (
                ChatUtils.isBlank(namespacedKeyStr)
                || !namespacedKeyStr.contains(":")
        ) {
            return Optional.empty();
        }

        final int index = namespacedKeyStr.indexOf(":");

        return getItemStack(
                namespacedKeyStr.substring(0, index),
                namespacedKeyStr.substring(index + 1)
        );
    }

    /**
     * Gets an {@link ItemStack} of custom item / block / decor from
     * {@link NamespacedKey}
     *
     * @param namespacedKey NamespacedKey of custom item / block / decor,
     *                      example - (msitems:example)
     * @return Optional of {@link ItemStack} object
     *         or empty optional if not found
     * @see #getItemStack(String, String)
     */
    public static @NotNull Optional<ItemStack> getItemStack(final @Nullable NamespacedKey namespacedKey) {
        return namespacedKey == null
                ? Optional.empty()
                : getItemStack(namespacedKey.getNamespace(), namespacedKey.getKey());
    }

    /**
     * Gets an {@link ItemStack} of custom item / block / decor from namespace
     * and key
     *
     * @param namespace The namespace of the plugin,
     *                  example - (msitems, msblock, msdecor)
     * @param key       The key of the custom item / block / decor,
     *                  example - (example)
     * @return Optional of {@link ItemStack} object
     *         or empty optional if not found
     * @see MSBlockUtils#getItemStack(String)
     * @see MSDecorUtils#getItemStack(String)
     * @see MSItemUtils#getItemStack(String)
     */
    public static @NotNull Optional<ItemStack> getItemStack(
            final @Nullable String namespace,
            final @Nullable String key
    ) {
        return namespace == null || key == null
                ? Optional.empty()
                : switch (namespace) {
                    case MSBLOCK_NAMESPACE -> MSBlockUtils.getItemStack(key);
                    case MSDECOR_NAMESPACE -> MSDecorUtils.getItemStack(key);
                    case MSITEMS_NAMESPACE -> MSItemUtils.getItemStack(key);
                    default -> Optional.empty();
                };
    }

    /**
     * Gets {@link CustomBlockData} or {@link CustomDecorData}
     * or {@link CustomItem} from {@link ItemStack}
     *
     * @param itemStack {@link ItemStack} of custom item / block / decor
     * @return Optional of {@link CustomBlockData}  or {@link CustomDecorData}
     *         or {@link CustomItem} or empty optional if not found
     * @see #getCustom(NamespacedKey)
     */
    public static @NotNull Optional<?> getCustom(final @Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return Optional.empty();
        }

        final ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return Optional.empty();
        }

        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        for (final var namespacedKey : container.getKeys()) {
            if (!namespacedKey.equals(RenameableItemRegistry.RENAMEABLE_NAMESPACED_KEY)) {
                return getCustom(namespacedKey.getNamespace(), container.get(namespacedKey, PersistentDataType.STRING));
            }
        }

        return Optional.empty();
    }

    /**
     * Gets {@link CustomBlockData} or {@link CustomDecorData}
     * or {@link CustomItem} from namespaced key string
     *
     * @param namespacedKeyStr Namespaced key string,
     *                         example - (msitems:example)
     * @return Optional of {@link CustomBlockData} or {@link CustomDecorData}
     *         or {@link CustomItem} or empty optional if not found
     * @see #getCustom(String, String)
     */
    public static @NotNull Optional<?> getCustom(final @Nullable String namespacedKeyStr) {
        if (ChatUtils.isBlank(namespacedKeyStr)) {
            return Optional.empty();
        }

        final int index = namespacedKeyStr.indexOf(":");

        return getCustom(
                namespacedKeyStr.substring(0, index),
                namespacedKeyStr.substring(index + 1)
        );
    }

    /**
     * Gets {@link CustomBlockData} or {@link CustomDecorData}
     * or {@link CustomItem} from {@link NamespacedKey}
     *
     * @param namespacedKey NamespacedKey of custom item / block / decor,
     *                      example - (msitems:example)
     * @return Optional of {@link CustomBlockData} or {@link CustomDecorData}
     *         or {@link CustomItem} or empty optional if not found
     * @see #getCustom(String, String)
     */
    public static @NotNull Optional<?> getCustom(final @Nullable NamespacedKey namespacedKey) {
        return namespacedKey == null
                ? Optional.empty()
                : getCustom(namespacedKey.getNamespace(), namespacedKey.getKey());
    }

    /**
     * Gets {@link CustomBlockData} or {@link CustomDecorData}
     * or {@link CustomItem} from namespace and key
     *
     * @param namespace The namespace of the plugin,
     *                  example - (msitems, msblock, msdecor)
     * @param key       The key of the custom item / block / decor,
     *                  example - (example)
     * @return Optional of {@link CustomBlockData} or {@link CustomDecorData}
     *         or {@link CustomItem} or empty optional if not found
     * @see CustomBlockRegistry#fromKey(String)
     * @see CustomDecorType#fromKey(String)
     * @see CustomItemType#fromKey(String)
     */
    public static @NotNull Optional<?> getCustom(
            final @Nullable String namespace,
            final @Nullable String key
    ) {
        return ChatUtils.isBlank(namespace) || ChatUtils.isBlank(key)
                ? Optional.empty()
                : switch (namespace) {
                    case MSBLOCK_NAMESPACE,
                         MSBLOCK_NAMESPACE + ":type" -> CustomBlockRegistry.fromKey(key);
                    case MSDECOR_NAMESPACE,
                         MSDECOR_NAMESPACE + ":type" -> CustomDecorData.fromKey(key);
                    case MSITEMS_NAMESPACE,
                         MSITEMS_NAMESPACE + ":type" -> CustomItem.fromKey(key);
                    default -> Optional.empty();
                };
    }
}
