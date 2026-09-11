package io.github.demsum.maidsoulbrewery.compat.kaleidoscopecookery;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidArriveAtBlockTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.mojang.datafixers.util.Pair;
import io.github.demsum.maidsoulbrewery.compat.touhoulittlemaid.RecipeFilterDataKeys;
import io.github.demsum.maidsoulbrewery.menu.SteamerRecipeFilterMenu;
import io.github.demsum.maidsoulbrewery.recipe.RecipeFilterData;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

final class SteamerMaidTask implements IMaidTask {
    static final int VERTICAL_SEARCH_RANGE = 4;

    private static final ResourceLocation UID = RecipeFilterDataKeys.STEAMER_TASK_ID;
    private static final double CLOSE_ENOUGH_DISTANCE = 2.5;

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public ItemStack getIcon() {
        return ModItems.STEAMER.get().getDefaultInstance();
    }

    @Override
    @Nullable
    public SoundEvent getAmbientSound(EntityMaid maid) {
        return null;
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid maid) {
        MaidSteamerMoveTask moveTask = new MaidSteamerMoveTask();
        return new ArrayList<>(List.of(
                Pair.of(5, moveTask),
                Pair.of(6, new MaidArriveAtBlockTask(CLOSE_ENOUGH_DISTANCE, moveTask::workAtSelectedSteamer))
        ));
    }

    @Override
    public boolean workPointTask(EntityMaid maid) {
        return true;
    }

    @Override
    public MenuProvider getTaskConfigGuiProvider(EntityMaid maid) {
        int maidId = maid.getId();
        return new SimpleMenuProvider(
                (containerId, inventory, player) ->
                        new SteamerRecipeFilterMenu(containerId, inventory, maidId),
                getName()
        );
    }

    @Override
    public String getMaidActionSummary() {
        return "Load ingredients into prepared steamers and collect finished food";
    }

    static void workAt(EntityMaid maid, BlockPos pos) {
        if (!(maid.level() instanceof ServerLevel level)) {
            return;
        }
        SteamerWorkStorage storage = SteamerWorkStorage.forMaid(maid);
        try {
            storage.flushOutputs();
            BlockEntity blockEntity = level.getBlockEntity(pos);
            RecipeFilterData filter = RecipeFilterDataKeys.getSteamer(maid);
            boolean worked = SteamerAdapter.inspect(blockEntity, level)
                    .filter(snapshot -> snapshot.accessible() && snapshot.covered())
                    .map(snapshot -> {
                        if (snapshot.canTakeFood()) {
                            if (!storage.canAcceptOutputs(snapshot.items())) {
                                return false;
                            }
                            boolean taken = SteamerAdapter.takeReadyFoodTo(
                                    blockEntity, level, maid, storage.outputDestination());
                            if (taken) {
                                storage.flushOutputs();
                            }
                            return taken;
                        }
                        if (!snapshot.hasHeatSource() || !snapshot.hasEmptySlot()) {
                            return false;
                        }
                        Predicate<ItemStack> placeable = stack ->
                                SteamerAdapter.canPlaceFood(blockEntity, level, stack, filter::allows);
                        if (!storage.prepareIngredient(placeable, snapshot.emptySlotCount())) {
                            return false;
                        }
                        int slot = SteamerAdapter.findPlaceableFoodSlot(
                                blockEntity, level, storage.ingredients(), filter::allows);
                        return SteamerAdapter.placeFoodFromSlot(
                                blockEntity, level, maid, storage.ingredients(), slot, filter::allows);
                    })
                    .orElse(false);
            if (worked) {
                maid.swing(InteractionHand.MAIN_HAND);
            }
        } finally {
            storage.sync();
            SteamerWorkLocks.release(level, pos, maid);
        }
    }
}
