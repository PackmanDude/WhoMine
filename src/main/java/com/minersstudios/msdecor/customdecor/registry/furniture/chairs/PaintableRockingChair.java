package com.minersstudios.msdecor.customdecor.registry.furniture.chairs;

import com.minersstudios.mscore.inventory.recipe.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.ShapedRecipeBuilder;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.SoundGroup;
import com.minersstudios.msdecor.customdecor.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class PaintableRockingChair<C extends CustomDecorData<C>> extends CustomDecorDataImpl<C> {

    protected final @NotNull Builder createBuilder(
            final @NotNull String key,
            final int customModelData,
            final @NotNull String displayName,
            final @NotNull Material planksMaterial
    ) {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(customModelData);
        itemMeta.displayName(ChatUtils.createDefaultStyledText(displayName));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key(key)
                .hitBox(new DecorHitBox(
                        1.0d,
                        1.0d,
                        1.0d,
                        DecorHitBox.Type.BARRIER
                ))
                .facing(Facing.FLOOR)
                .soundGroup(SoundGroup.WOOD)
                .itemStack(itemStack)
                .recipes(
                        builder -> Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .namespacedKey(builder.key())
                                .group(CustomDecorType.NAMESPACE + ":paintable_rocking_chair")
                                .category(CraftingBookCategory.BUILDING)
                                .result(builder.itemStack())
                                .shape(
                                        "P  ",
                                        "PLP",
                                        "PPP"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('P', planksMaterial),
                                        ShapedRecipeBuilder.material('L', Material.LEATHER)
                                )
                                .build(),
                                true
                        )
                )
                .parameters(DecorParameter.SITTABLE)
                .sitHeight(0.5d);
    }

    public static final class Acacia extends PaintableRockingChair<Acacia> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "acacia_paintable_rocking_chair",
                    1039,
                    "Акациевое кресло-качалка",
                    Material.ACACIA_PLANKS
            );
        }
    }

    public static final class Birch extends PaintableRockingChair<Birch> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "birch_paintable_rocking_chair",
                    1041,
                    "Берёзовое кресло-качалка",
                    Material.BIRCH_PLANKS
            );
        }
    }

    public static final class Cherry extends PaintableRockingChair<Cherry> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "cherry_paintable_rocking_chair",
                    1381,
                    "Вишнёвое кресло-качалка",
                    Material.CHERRY_PLANKS
            );
        }
    }

    public static final class Crimson extends PaintableRockingChair<Crimson> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "crimson_paintable_rocking_chair",
                    1043,
                    "Багровое кресло-качалка",
                    Material.CRIMSON_PLANKS
            );
        }
    }

    public static final class DarkOak extends PaintableRockingChair<DarkOak> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "dark_oak_paintable_rocking_chair",
                    1045,
                    "Кресло-качалка из тёмного дуба",
                    Material.DARK_OAK_PLANKS
            );
        }
    }

    public static final class Jungle extends PaintableRockingChair<Jungle> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "jungle_paintable_rocking_chair",
                    1047,
                    "Тропическое кресло-качалка",
                    Material.JUNGLE_PLANKS
            );
        }
    }

    public static final class Mangrove extends PaintableRockingChair<Mangrove> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "mangrove_paintable_rocking_chair",
                    1198,
                    "Мангровое кресло-качалка",
                    Material.MANGROVE_PLANKS
            );
        }
    }

    public static final class Oak extends PaintableRockingChair<Oak> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "oak_paintable_rocking_chair",
                    1049,
                    "Дубовое кресло-качалка",
                    Material.OAK_PLANKS
            );
        }
    }

    public static final class Spruce extends PaintableRockingChair<Spruce> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "spruce_paintable_rocking_chair",
                    1051,
                    "Еловое кресло-качалка",
                    Material.SPRUCE_PLANKS
            );
        }
    }

    public static final class Warped extends PaintableRockingChair<Warped> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "warped_paintable_rocking_chair",
                    1053,
                    "Искажённое кресло-качалка",
                    Material.WARPED_PLANKS
            );
        }
    }
}
