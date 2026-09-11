package io.github.demsum.maidsoulbrewery.mixin.maidsoulkitchen;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.wallev.maidsoulkitchen.api.task.v1.cook.ICookTask;
import com.github.wallev.maidsoulkitchen.init.MkMemories;
import com.github.wallev.maidsoulkitchen.task.cook.common.ai.MaidCookMakeTask;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MaidCookMakeTask.class, remap = false)
public abstract class MaidCookMakeTaskSideApproachMixin {
    @Shadow
    @Final
    @SuppressWarnings("rawtypes")
    private ICookTask task;

    @Inject(
            method = "checkExtraStartConditions(Lnet/minecraft/server/level/ServerLevel;"
                    + "Lcom/github/tartaricacid/touhoulittlemaid/entity/passive/EntityMaid;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void maidsoulBrewery$acceptSideApproach(
            ServerLevel level,
            EntityMaid maid,
            CallbackInfoReturnable<Boolean> callback
    ) {
        Brain<EntityMaid> brain = maid.getBrain();
        Optional<PositionTracker> target = brain.getMemory(InitEntities.TARGET_POS.get());
        if (target.isEmpty()) {
            callback.setReturnValue(false);
            return;
        }

        BlockPos devicePos = target.get().currentBlockPosition();
        double closeEnoughDistance = task.getCloseEnoughDist();
        if (maid.distanceToSqr(target.get().currentPosition())
                <= closeEnoughDistance * closeEnoughDistance) {
            callback.setReturnValue(true);
            return;
        }

        if (!maidsoulBrewery$hasValidRouteToDevice(level, brain, devicePos)) {
            brain.eraseMemory(InitEntities.TARGET_POS.get());
            brain.eraseMemory(MkMemories.DESTROY_POS.get());
        }
        callback.setReturnValue(false);
    }

    @SuppressWarnings("unchecked")
    private boolean maidsoulBrewery$hasValidRouteToDevice(
            ServerLevel level,
            Brain<EntityMaid> brain,
            BlockPos devicePos
    ) {
        BlockEntity blockEntity = level.getBlockEntity(devicePos);
        if (blockEntity == null || !task.isCookBE(blockEntity)) {
            return false;
        }

        Optional<WalkTarget> walkTarget = brain.getMemory(MemoryModuleType.WALK_TARGET);
        if (walkTarget.isEmpty()) {
            return false;
        }

        BlockPos walkPos = walkTarget.get().getTarget().currentBlockPosition();
        if (walkPos.equals(devicePos)) {
            return true;
        }

        boolean isCardinalNeighbor = walkPos.getY() == devicePos.getY()
                && Math.abs(walkPos.getX() - devicePos.getX())
                + Math.abs(walkPos.getZ() - devicePos.getZ()) == 1;
        if (!isCardinalNeighbor) {
            return false;
        }

        return brain.getMemory(MkMemories.DESTROY_POS.get())
                .map(PositionTracker::currentBlockPosition)
                .filter(devicePos::equals)
                .isPresent();
    }
}
