package io.github.demsum.maidsoulbrewery.mixin.maidsoulkitchen;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.wallev.maidsoulkitchen.api.task.v1.cook.ICookTask;
import com.github.wallev.maidsoulkitchen.init.MkMemories;
import com.github.wallev.maidsoulkitchen.task.cook.common.ai.MaidCookMakeTask;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MaidCookMakeTask.class, remap = false)
public abstract class MaidCookMakeTaskSideApproachMixin {
    @Shadow
    @Final
    @SuppressWarnings("rawtypes")
    private ICookTask task;

    @Redirect(
            method = "lambda$checkExtraStartConditions$0("
                    + "Lcom/github/tartaricacid/touhoulittlemaid/entity/passive/EntityMaid;"
                    + "Lnet/minecraft/world/entity/ai/Brain;"
                    + "Lnet/minecraft/world/entity/ai/behavior/PositionTracker;)Ljava/lang/Boolean;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/Vec3;equals(Ljava/lang/Object;)Z"
            ),
            require = 1
    )
    @SuppressWarnings("unchecked")
    private boolean maidsoulBrewery$acceptSideApproachAsSameTarget(
            Vec3 walkTargetPos,
            Object targetPos,
            EntityMaid maid,
            Brain<EntityMaid> brain,
            PositionTracker targetTracker
    ) {
        if (walkTargetPos.equals(targetPos)) {
            return true;
        }
        if (!(targetPos instanceof Vec3 deviceTargetPos)) {
            return false;
        }

        BlockPos walkPos = BlockPos.containing(walkTargetPos);
        BlockPos devicePos = BlockPos.containing(deviceTargetPos);
        boolean isCardinalNeighbor = walkPos.getY() == devicePos.getY()
                && Math.abs(walkPos.getX() - devicePos.getX())
                + Math.abs(walkPos.getZ() - devicePos.getZ()) == 1;
        if (!isCardinalNeighbor) {
            return false;
        }

        BlockEntity blockEntity = maid.level().getBlockEntity(devicePos);
        if (blockEntity == null || !task.isCookBE(blockEntity)) {
            return false;
        }

        return brain.getMemory(MkMemories.DESTROY_POS.get())
                .map(PositionTracker::currentBlockPosition)
                .filter(devicePos::equals)
                .isPresent();
    }
}
