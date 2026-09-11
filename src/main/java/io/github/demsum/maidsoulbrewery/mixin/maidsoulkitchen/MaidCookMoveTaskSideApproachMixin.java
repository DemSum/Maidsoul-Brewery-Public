package io.github.demsum.maidsoulbrewery.mixin.maidsoulkitchen;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidCheckRateTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.MaidPathFindingBFS;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.wallev.maidsoulkitchen.init.MkMemories;
import com.github.wallev.maidsoulkitchen.task.cook.common.ai.MaidCookMoveTask;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MaidCookMoveTask.class, remap = false)
public abstract class MaidCookMoveTaskSideApproachMixin extends MaidCheckRateTask {
    private static final Direction[] HORIZONTAL_DIRECTIONS = {
            Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST
    };

    @Shadow
    @Final
    private float movementSpeed;

    @Shadow
    @Final
    private int verticalSearchRange;

    @Shadow
    protected int verticalSearchStart;

    protected MaidCookMoveTaskSideApproachMixin(
            Map<MemoryModuleType<?>, MemoryStatus> entryCondition
    ) {
        super(entryCondition);
    }

    @Shadow
    protected abstract boolean shouldMoveTo(ServerLevel level, EntityMaid maid, BlockPos pos);

    @Inject(
            method = "searchForDestination(Lnet/minecraft/server/level/ServerLevel;"
                    + "Lcom/github/tartaricacid/touhoulittlemaid/entity/passive/EntityMaid;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void maidsoulBrewery$searchFromReachableApproaches(
            ServerLevel level,
            EntityMaid maid,
            CallbackInfo callback
    ) {
        callback.cancel();

        BlockPos searchCenter = maid.hasRestriction()
                ? maid.getRestrictCenter()
                : maid.blockPosition().below();
        int searchRange = (int) maid.getRestrictRadius();
        if (searchRange <= 0) {
            return;
        }

        Set<BlockPos> checkedDevices = new HashSet<>();
        BlockPos[] selectedDevice = new BlockPos[1];
        MaidPathFindingBFS pathFinding = new MaidPathFindingBFS(
                maid.getNavigation().getNodeEvaluator(), level, maid);
        try {
            Optional<BlockPos> approach = pathFinding.find(approachPos ->
                    maidsoulBrewery$selectAdjacentDevice(
                            level,
                            maid,
                            approachPos,
                            searchCenter,
                            searchRange,
                            checkedDevices,
                            selectedDevice
                    ));
            if (approach.isEmpty() || selectedDevice[0] == null) {
                return;
            }

            BlockPos approachPos = approach.get().immutable();
            BlockPos devicePos = selectedDevice[0];
            BehaviorUtils.setWalkAndLookTargetMemories(maid, approachPos, movementSpeed, 0);
            maid.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(devicePos));
            maid.getBrain().setMemory(InitEntities.TARGET_POS.get(), new BlockPosTracker(devicePos));
            maid.getBrain().setMemory(MkMemories.DESTROY_POS.get(), new BlockPosTracker(devicePos));
            setNextCheckTickCount(5);
        } finally {
            pathFinding.finish();
        }
    }

    private boolean maidsoulBrewery$selectAdjacentDevice(
            ServerLevel level,
            EntityMaid maid,
            BlockPos approachPos,
            BlockPos searchCenter,
            int searchRange,
            Set<BlockPos> checkedDevices,
            BlockPos[] selectedDevice
    ) {
        for (Direction direction : HORIZONTAL_DIRECTIONS) {
            BlockPos devicePos = approachPos.relative(direction).immutable();
            if (!maidsoulBrewery$isInOriginalSearchBounds(
                    devicePos, searchCenter, searchRange)) {
                continue;
            }
            if (!maid.isWithinRestriction(devicePos)
                    || !maidsoulBrewery$isWithinOwnerRange(maid, devicePos)
                    || !level.isLoaded(devicePos)
                    || !checkedDevices.add(devicePos)) {
                continue;
            }
            if (shouldMoveTo(level, maid, devicePos)) {
                selectedDevice[0] = devicePos;
                return true;
            }
        }
        return false;
    }

    private boolean maidsoulBrewery$isInOriginalSearchBounds(
            BlockPos devicePos,
            BlockPos searchCenter,
            int searchRange
    ) {
        int relativeY = devicePos.getY() - searchCenter.getY() - 1;
        return relativeY >= verticalSearchStart
                && relativeY <= verticalSearchRange
                && Math.abs(devicePos.getX() - searchCenter.getX()) < searchRange
                && Math.abs(devicePos.getZ() - searchCenter.getZ()) < searchRange;
    }

    private static boolean maidsoulBrewery$isWithinOwnerRange(EntityMaid maid, BlockPos pos) {
        if (maid.isHomeModeEnable()) {
            return true;
        }
        LivingEntity owner = maid.getOwner();
        return owner != null && pos.closerToCenterThan(owner.position(), 8.0);
    }
}
