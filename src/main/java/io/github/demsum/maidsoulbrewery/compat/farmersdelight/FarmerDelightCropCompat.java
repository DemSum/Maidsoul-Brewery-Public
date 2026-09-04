package io.github.demsum.maidsoulbrewery.compat.farmersdelight;

import com.github.tartaricacid.touhoulittlemaid.api.task.ISpecialCropHandler;
import com.github.tartaricacid.touhoulittlemaid.entity.task.crop.SpecialCropManager;
import io.github.demsum.maidsoulbrewery.MaidSoulBrewery;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class FarmerDelightCropCompat {
    private static final ResourceLocation RICH_SOIL =
            ResourceLocation.fromNamespaceAndPath("farmersdelight", "rich_soil");
    private static final ResourceLocation BROWN_MUSHROOM_COLONY =
            ResourceLocation.fromNamespaceAndPath("farmersdelight", "brown_mushroom_colony");
    private static final ResourceLocation RED_MUSHROOM_COLONY =
            ResourceLocation.fromNamespaceAndPath("farmersdelight", "red_mushroom_colony");

    private FarmerDelightCropCompat() {
    }

    public static void registerSpecialCropHandlers() {
        Block richSoil = maidsoulbrewery$getBlock(RICH_SOIL);
        Block brownMushroomColony = maidsoulbrewery$getBlock(BROWN_MUSHROOM_COLONY);
        Block redMushroomColony = maidsoulbrewery$getBlock(RED_MUSHROOM_COLONY);
        if (richSoil == Blocks.AIR || brownMushroomColony == Blocks.AIR || redMushroomColony == Blocks.AIR) {
            return;
        }

        ISpecialCropHandler handler = new FarmerDelightMushroomColonyCropHandler(
                richSoil,
                brownMushroomColony,
                redMushroomColony
        );
        Map<Item, ISpecialCropHandler> seedHandlers = SpecialCropManager.getItemSeedHandlers();
        Map<Block, ISpecialCropHandler> cropHandlers = SpecialCropManager.getBlockCropHandlers();

        try {
            seedHandlers.putIfAbsent(Items.BROWN_MUSHROOM, handler);
            seedHandlers.putIfAbsent(Items.RED_MUSHROOM, handler);
            cropHandlers.putIfAbsent(richSoil, handler);
            cropHandlers.putIfAbsent(brownMushroomColony, handler);
            cropHandlers.putIfAbsent(redMushroomColony, handler);
        } catch (UnsupportedOperationException exception) {
            MaidSoulBrewery.LOGGER.warn("Skipped Farmer's Delight mushroom colony crop handlers because TLM crop handlers are already frozen");
            return;
        }

        MaidSoulBrewery.LOGGER.debug("Registered Farmer's Delight mushroom colony crop handlers");
    }

    private static Block maidsoulbrewery$getBlock(ResourceLocation id) {
        Block block = BuiltInRegistries.BLOCK.get(id);
        return block == null ? Blocks.AIR : block;
    }
}
