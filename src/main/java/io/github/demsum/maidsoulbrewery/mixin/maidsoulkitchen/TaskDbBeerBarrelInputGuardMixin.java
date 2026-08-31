package io.github.demsum.maidsoulbrewery.mixin.maidsoulkitchen;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.github.wallev.maidsoulkitchen.task.cook.drinkbeer.TaskDbBeerBarrel", remap = false)
public abstract class TaskDbBeerBarrelInputGuardMixin {
    @Inject(
            method = "extractInputStack",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void maidsoulbrewery$extractOnlyReturnedBuckets(
            Container container,
            IItemHandlerModifiable destination,
            BlockEntity blockEntity,
            CallbackInfo callbackInfo
    ) {
        int checkedSlots = Math.min(4, container.getContainerSize());
        for (int slot = 0; slot < checkedSlots; slot++) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty() || !stack.is(Items.BUCKET)) {
                continue;
            }

            ItemStack remaining = ItemHandlerHelper.insertItemStacked(destination, stack.copy(), false);
            int moved = stack.getCount() - remaining.getCount();
            if (moved > 0) {
                container.removeItem(slot, moved);
                blockEntity.setChanged();
            }
        }

        callbackInfo.cancel();
    }
}
