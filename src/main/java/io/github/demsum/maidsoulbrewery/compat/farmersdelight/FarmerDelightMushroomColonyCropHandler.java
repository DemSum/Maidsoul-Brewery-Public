package io.github.demsum.maidsoulbrewery.compat.farmersdelight;

import com.github.tartaricacid.touhoulittlemaid.api.task.ISpecialCropHandler;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public final class FarmerDelightMushroomColonyCropHandler implements ISpecialCropHandler {
    private static final String AGE_PROPERTY = "age";
    private static final TagKey<Item> FARMERS_DELIGHT_KNIVES = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("farmersdelight", "tools/knives")
    );

    private final Block richSoil;
    private final Block brownMushroomColony;
    private final Block redMushroomColony;

    public FarmerDelightMushroomColonyCropHandler(
            Block richSoil,
            Block brownMushroomColony,
            Block redMushroomColony
    ) {
        this.richSoil = richSoil;
        this.brownMushroomColony = brownMushroomColony;
        this.redMushroomColony = redMushroomColony;
    }

    @Override
    public boolean isSeed(ItemStack stack) {
        return stack.is(Items.BROWN_MUSHROOM) || stack.is(Items.RED_MUSHROOM);
    }

    @Override
    public boolean canHarvest(EntityMaid maid, BlockPos pos, BlockState state) {
        IntegerProperty age = maidsoulbrewery$getAgeProperty(state);
        return maidsoulbrewery$isMushroomColony(state)
                && age != null
                && state.getValue(age) >= maidsoulbrewery$getMaxAge(age);
    }

    @Override
    public void harvest(EntityMaid maid, BlockPos pos, BlockState state, boolean destroyMode) {
        IntegerProperty age = maidsoulbrewery$getAgeProperty(state);
        if (age == null) {
            return;
        }

        Level level = maid.level();
        ItemStack mainHand = maid.getMainHandItem();
        if (mainHand.is(FARMERS_DELIGHT_KNIVES)) {
            maidsoulbrewery$giveToMaidOrDrop(
                    maid,
                    pos,
                    new ItemStack(maidsoulbrewery$getMushroomItem(state), state.getValue(age))
            );
            if (!level.isClientSide) {
                mainHand.hurtAndBreak(1, maid, EquipmentSlot.MAINHAND);
            }
            level.levelEvent(2001, pos, Block.getId(state));
            level.setBlock(pos, state.setValue(age, 0), 3);
            level.gameEvent(maid, GameEvent.BLOCK_CHANGE, pos);
            return;
        }

        maidsoulbrewery$giveToMaidOrDrop(
                maid,
                pos,
                new ItemStack(maidsoulbrewery$getMushroomItem(state), 5)
        );
        level.levelEvent(2001, pos, Block.getId(state));
        level.setBlock(pos, maidsoulbrewery$getMushroomBlock(state).defaultBlockState(), 3);
        level.gameEvent(maid, GameEvent.BLOCK_CHANGE, pos);
    }

    @Override
    public boolean canPlant(EntityMaid maid, BlockPos pos, BlockState state, ItemStack seed) {
        if (!state.is(this.richSoil) || !isSeed(seed)) {
            return false;
        }

        Level level = maid.level();
        BlockPos plantPos = pos.above();
        BlockState replacing = level.getBlockState(plantPos);
        if (!replacing.canBeReplaced() || !replacing.getFluidState().isEmpty()) {
            return false;
        }

        BlockState mushroom = maidsoulbrewery$getMushroomBlock(seed).defaultBlockState();
        return mushroom.canSurvive(level, plantPos);
    }

    @Override
    public ItemStack plant(EntityMaid maid, BlockPos pos, BlockState state, ItemStack seed) {
        if (canPlant(maid, pos, state, seed)) {
            maid.placeItemBlock(pos.above(), seed);
        }
        return seed;
    }

    private boolean maidsoulbrewery$isMushroomColony(BlockState state) {
        return state.is(this.brownMushroomColony) || state.is(this.redMushroomColony);
    }

    private Item maidsoulbrewery$getMushroomItem(BlockState state) {
        return state.is(this.redMushroomColony) ? Items.RED_MUSHROOM : Items.BROWN_MUSHROOM;
    }

    private Block maidsoulbrewery$getMushroomBlock(BlockState state) {
        return state.is(this.redMushroomColony) ? Blocks.RED_MUSHROOM : Blocks.BROWN_MUSHROOM;
    }

    private static Block maidsoulbrewery$getMushroomBlock(ItemStack seed) {
        return seed.is(Items.RED_MUSHROOM) ? Blocks.RED_MUSHROOM : Blocks.BROWN_MUSHROOM;
    }

    private static void maidsoulbrewery$giveToMaidOrDrop(EntityMaid maid, BlockPos pos, ItemStack stack) {
        ItemStack remaining = ItemHandlerHelper.insertItemStacked(maid.getAvailableInv(false), stack, false);
        if (!remaining.isEmpty()) {
            Block.popResource(maid.level(), pos, remaining);
        }
    }

    private static IntegerProperty maidsoulbrewery$getAgeProperty(BlockState state) {
        for (var property : state.getProperties()) {
            if (property instanceof IntegerProperty integerProperty && AGE_PROPERTY.equals(property.getName())) {
                return integerProperty;
            }
        }
        return null;
    }

    private static int maidsoulbrewery$getMaxAge(IntegerProperty age) {
        int maxAge = 0;
        for (Integer value : age.getPossibleValues()) {
            maxAge = Math.max(maxAge, value);
        }
        return maxAge;
    }
}
