package io.github.demsum.maidsoulbrewery.compat.kaleidoscopecookery;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidMoveToBlockTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.MaidPathFindingBFS;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import io.github.demsum.maidsoulbrewery.compat.touhoulittlemaid.RecipeFilterDataKeys;
import io.github.demsum.maidsoulbrewery.recipe.RecipeFilterData;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

final class MaidSteamerMoveTask extends MaidMoveToBlockTask {
    private static final float MOVEMENT_SPEED = 0.6F;
    private static final int[] INTERACTION_HEIGHT_OFFSETS = {0, 1, -1, 2, -2};

    private BlockPos currentSteamerPos;

    MaidSteamerMoveTask() {
        super(MOVEMENT_SPEED, SteamerMaidTask.VERTICAL_SEARCH_RANGE);
    }

    @Override
    protected void start(ServerLevel level, EntityMaid maid, long gameTime) {
        SteamerWorkStorage storage = SteamerWorkStorage.forMaid(maid);
        storage.flushOutputs();
        storage.sync();
        searchForSideApproach(level, maid, storage, RecipeFilterDataKeys.getSteamer(maid));
    }

    @Override
    protected boolean shouldMoveTo(ServerLevel level, EntityMaid maid, BlockPos pos) {
        if (!withinOwnerRange(maid, pos)) {
            return false;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!SteamerAdapter.supports(blockEntity)) {
            return false;
        }
        return shouldUseSteamer(
                level,
                blockEntity,
                RecipeFilterDataKeys.getSteamer(maid),
                SteamerWorkStorage.forMaid(maid)
        );
    }

    private boolean shouldUseSteamer(
            ServerLevel level,
            BlockEntity blockEntity,
            RecipeFilterData filter,
            SteamerWorkStorage storage
    ) {
        Predicate<ItemStack> placeable = stack ->
                SteamerAdapter.canPlaceFood(blockEntity, level, stack, filter::allows);
        return SteamerAdapter.inspect(blockEntity, level)
                .filter(snapshot -> snapshot.accessible() && snapshot.covered())
                .map(snapshot -> {
                    if (snapshot.canTakeFood()) {
                        return storage.canAcceptOutputs(snapshot.items());
                    }
                    return snapshot.hasHeatSource()
                            && snapshot.hasEmptySlot()
                            && storage.canSupplyIngredient(placeable);
                })
                .orElse(false);
    }

    void workAtSelectedSteamer(EntityMaid maid, BlockPos ignoredApproachPos) {
        BlockPos steamerPos = currentSteamerPos;
        currentSteamerPos = null;
        if (steamerPos != null) {
            SteamerMaidTask.workAt(maid, steamerPos);
        }
    }

    private void searchForSideApproach(
            ServerLevel level,
            EntityMaid maid,
            SteamerWorkStorage storage,
            RecipeFilterData filter
    ) {
        releaseCurrentClaim(level, maid);
        Set<BlockPos> checkedSteamers = new HashSet<>();
        MaidPathFindingBFS pathFinding = new MaidPathFindingBFS(
                maid.getNavigation().getNodeEvaluator(), level, maid);
        try {
            Optional<BlockPos> approach = pathFinding.find(pos ->
                    claimAdjacentSteamer(level, maid, pos, storage, filter, checkedSteamers));
            if (approach.isEmpty()) {
                return;
            }

            BlockPos approachPos = approach.get().immutable();
            BehaviorUtils.setWalkAndLookTargetMemories(maid, approachPos, MOVEMENT_SPEED, 0);
            maid.getBrain().setMemory(
                    MemoryModuleType.LOOK_TARGET, new BlockPosTracker(currentSteamerPos));
            maid.getBrain().setMemory(
                    InitEntities.TARGET_POS.get(), new BlockPosTracker(approachPos));
            setNextCheckTickCount(5);
        } finally {
            pathFinding.finish();
        }
    }

    private boolean claimAdjacentSteamer(
            ServerLevel level,
            EntityMaid maid,
            BlockPos approachPos,
            SteamerWorkStorage storage,
            RecipeFilterData filter,
            Set<BlockPos> checkedSteamers
    ) {
        BlockPos.MutableBlockPos steamerPos = new BlockPos.MutableBlockPos();
        for (int yOffset : INTERACTION_HEIGHT_OFFSETS) {
            for (int xOffset = -1; xOffset <= 1; xOffset++) {
                for (int zOffset = -1; zOffset <= 1; zOffset++) {
                    if (xOffset == 0 && zOffset == 0) {
                        continue;
                    }
                    steamerPos.setWithOffset(approachPos, xOffset, yOffset, zOffset);
                    if (!maid.isWithinRestriction(steamerPos)
                            || !withinOwnerRange(maid, steamerPos)
                            || !SteamerAdapter.supports(level.getBlockState(steamerPos))) {
                        continue;
                    }

                    BlockPos immutableSteamerPos = steamerPos.immutable();
                    if (!checkedSteamers.add(immutableSteamerPos)) {
                        continue;
                    }
                    BlockEntity blockEntity = level.getBlockEntity(steamerPos);
                    if (!SteamerAdapter.supports(blockEntity)
                            || !shouldUseSteamer(level, blockEntity, filter, storage)
                            || !SteamerWorkLocks.tryClaim(level, immutableSteamerPos, maid)) {
                        continue;
                    }
                    currentSteamerPos = immutableSteamerPos;
                    return true;
                }
            }
        }
        return false;
    }

    private void releaseCurrentClaim(ServerLevel level, EntityMaid maid) {
        if (currentSteamerPos != null) {
            SteamerWorkLocks.release(level, currentSteamerPos, maid);
            currentSteamerPos = null;
        }
    }

    private static boolean withinOwnerRange(EntityMaid maid, BlockPos pos) {
        if (maid.isHomeModeEnable()) {
            return true;
        }
        LivingEntity owner = maid.getOwner();
        return owner != null && pos.closerToCenterThan(owner.position(), 8.0);
    }
}
