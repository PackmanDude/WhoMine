package com.minersstudios.msdecor.registry.decoration.home.head;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.builder.ShapedRecipeBuilder;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.sound.SoundGroup;
import com.minersstudios.msdecor.api.CustomDecorDataImpl;
import com.minersstudios.msdecor.api.DecorHitBox;
import com.minersstudios.msdecor.api.Facing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class HoglinHead extends CustomDecorDataImpl<HoglinHead> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1162);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Голова борова"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key("hoglin_head")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.SOLID)
                        .facings(Facing.WALL)
                        .size(1.0d, 1.0d, 1.0d)
                        .build()
                )
                .facings(Facing.WALL)
                .soundGroup(SoundGroup.WOOD)
                .itemStack(itemStack)
                .recipes(
                        unused -> Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        " PS",
                                        "BBS",
                                        "  S"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('P', Material.PORKCHOP),
                                        ShapedRecipeBuilder.material('B', Material.BONE),
                                        ShapedRecipeBuilder.material('S', Material.SPRUCE_LOG)
                                ),
                                Boolean.TRUE
                        )
                );
    }
}
