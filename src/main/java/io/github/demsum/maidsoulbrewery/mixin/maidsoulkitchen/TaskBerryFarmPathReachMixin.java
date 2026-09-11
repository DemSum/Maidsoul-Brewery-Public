package io.github.demsum.maidsoulbrewery.mixin.maidsoulkitchen;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.MaidPathFindingBFS;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "com.github.wallev.maidsoulkitchen.task.farm.TaskBerryFarm$1", remap = false)
public abstract class TaskBerryFarmPathReachMixin {
    // MSK overrides the retired two-argument hook. This restores its intended
    // surrounding-position check for TLM's current shared BFS search.
    protected boolean checkPathReach(EntityMaid maid, MaidPathFindingBFS pathFinding, BlockPos pos) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (pathFinding.canPathReach(pos.offset(x, 0, z))) {
                    return true;
                }
            }
        }
        return false;
    }
}
