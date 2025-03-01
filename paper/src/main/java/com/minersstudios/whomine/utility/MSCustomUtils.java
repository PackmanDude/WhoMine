package com.minersstudios.whomine.utility;

import com.minersstudios.whomine.api.annotation.Key;
import com.minersstudios.whomine.api.annotation.Namespace;
import com.minersstudios.whomine.api.annotation.ResourceKey;
import com.minersstudios.whomine.custom.block.CustomBlockData;
import com.minersstudios.whomine.custom.block.CustomBlockRegistry;
import com.minersstudios.whomine.custom.decor.CustomDecorData;
import com.minersstudios.whomine.custom.decor.CustomDecorType;
import com.minersstudios.whomine.custom.item.CustomItem;
import com.minersstudios.whomine.custom.item.CustomItemType;
import com.minersstudios.whomine.custom.item.renameable.RenameableItemRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static com.minersstudios.whomine.utility.SharedConstants.*;

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
    public static @NotNull Optional<ItemStack> getItemStack(final @Subst("namespace:key") @ResourceKey @Nullable String namespacedKeyStr) {
        if (ChatUtils.isBlank(namespacedKeyStr)) {
            return Optional.empty();
        }

        final int colonIndex = namespacedKeyStr.indexOf(":");

        if (colonIndex == -1) {
            return Optional.empty();
        }

        @Subst("namespace") final String namespace = namespacedKeyStr.substring(0, colonIndex);
        @Subst("key") final String key = namespacedKeyStr.substring(colonIndex + 1);

        return getItemStack(namespace, key);
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
        if (namespacedKey == null) {
            return Optional.empty();
        }

        @Subst("namespace") final String namespace = namespacedKey.getNamespace();
        @Subst("key") final String key = namespacedKey.getKey();

        return getItemStack(namespace, key);
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
            final @Namespace @Nullable String namespace,
            final @Key @Nullable String key
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
                @Subst("namespace") final String namespace = namespacedKey.getNamespace();
                @Subst("key") final String key = container.get(namespacedKey, PersistentDataType.STRING);

                return getCustom(namespace, key);
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
    public static @NotNull Optional<?> getCustom(final @Subst("namespace:key") @ResourceKey @Nullable String namespacedKeyStr) {
        if (
                ChatUtils.isBlank(namespacedKeyStr)
                || !namespacedKeyStr.contains(":")
        ) {
            return Optional.empty();
        }

        final int colonIndex = namespacedKeyStr.indexOf(":");

        if (colonIndex == -1) {
            return Optional.empty();
        }

        @Subst("namespace") final String namespace = namespacedKeyStr.substring(0, colonIndex);
        @Subst("key") final String key = namespacedKeyStr.substring(colonIndex + 1);

        return getCustom(
                namespace,
                key
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
        if (namespacedKey == null) {
            return Optional.empty();
        }

        @Subst("namespace") final String namespace = namespacedKey.getNamespace();
        @Subst("key") final String key = namespacedKey.getKey();

        return getCustom(namespace, key);
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
            final @Namespace @Nullable String namespace,
            final @Key @Nullable String key
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
