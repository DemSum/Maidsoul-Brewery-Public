package io.github.demsum.maidsoulbrewery.mixin.maidsoulkitchen;

import com.mojang.datafixers.util.Pair;
import java.util.List;
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
    @SuppressWarnings("unused")
    public boolean hasInput(Container container) {
        return maidsoulbrewery$hasIngredientInput(container);
    }

    @SuppressWarnings("unused")
    public void insertInputStack(
            Container container,
            IItemHandlerModifiable source,
            BlockEntity blockEntity,
            Pair<List<Integer>, List<List<ItemStack>>> recipeIngredient
    ) {
        List<Integer> amounts = recipeIngredient.getFirst();
        List<List<ItemStack>> ingredients = recipeIngredient.getSecond();
        int entries = Math.min(amounts.size(), ingredients.size());

        for (int index = 0; index < entries; index++) {
            int targetSlot = index;
            if (!maidsoulbrewery$isValidSlot(container, targetSlot)) {
                return;
            }

            int missing = maidsoulbrewery$getMissingAmount(container, targetSlot, amounts.get(index), ingredients.get(index));
            if (missing < 0 || !maidsoulbrewery$hasEnoughAvailable(missing, ingredients.get(index))) {
                return;
            }
        }

        boolean changed = false;
        for (int index = 0; index < entries; index++) {
            int missing = maidsoulbrewery$getMissingAmount(container, index, amounts.get(index), ingredients.get(index));
            if (missing > 0) {
                maidsoulbrewery$insertAndShrink(container, ingredients.get(index), index, missing);
                changed = true;
            }
        }

        if (changed) {
            blockEntity.setChanged();
        }
    }

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

    private static boolean maidsoulbrewery$hasIngredientInput(Container container) {
        int checkedSlots = Math.min(4, container.getContainerSize());
        for (int slot = 0; slot < checkedSlots; slot++) {
            if (!container.getItem(slot).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static boolean maidsoulbrewery$isValidSlot(Container container, int slot) {
        return slot >= 0 && slot < container.getContainerSize();
    }

    private static int maidsoulbrewery$getMissingAmount(
            Container container,
            int targetSlot,
            int required,
            List<ItemStack> availableStacks
    ) {
        ItemStack current = container.getItem(targetSlot);
        if (current.isEmpty()) {
            return required;
        }

        if (maidsoulbrewery$matchesAny(current, availableStacks)) {
            return Math.max(0, required - current.getCount());
        }

        return -1;
    }

    private static boolean maidsoulbrewery$hasEnoughAvailable(int required, List<ItemStack> availableStacks) {
        int remaining = required;
        for (ItemStack stack : availableStacks) {
            if (stack.isEmpty()) {
                continue;
            }

            remaining -= stack.getCount();
            if (remaining <= 0) {
                return true;
            }
        }
        return remaining <= 0;
    }

    private static void maidsoulbrewery$insertAndShrink(
            Container container,
            List<ItemStack> availableStacks,
            int targetSlot,
            int amount
    ) {
        int remaining = amount;
        for (ItemStack stack : availableStacks) {
            if (stack.isEmpty()) {
                continue;
            }

            ItemStack current = container.getItem(targetSlot);
            int moved = Math.min(remaining, stack.getCount());
            int newCount = (current.isEmpty() ? 0 : current.getCount()) + moved;
            container.setItem(targetSlot, stack.copyWithCount(newCount));
            stack.shrink(moved);

            remaining -= moved;
            if (remaining <= 0) {
                return;
            }
        }
    }

    private static boolean maidsoulbrewery$matchesAny(ItemStack current, List<ItemStack> availableStacks) {
        for (ItemStack stack : availableStacks) {
            if (!stack.isEmpty() && ItemStack.isSameItemSameComponents(current, stack)) {
                return true;
            }
        }
        return false;
    }
}
