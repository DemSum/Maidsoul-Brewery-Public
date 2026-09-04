package io.github.demsum.maidsoulbrewery.mixin.maidsoulkitchen;

import com.github.wallev.maidsoulkitchen.task.cook.drinkbeer.BeerBarrelBlockAccessor;
import com.github.wallev.maidsoulkitchen.task.cook.common.inventory.MaidRecipesManager;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import lekavar.lma.drinkbeer.blockentities.BeerBarrelBlockEntity;
import lekavar.lma.drinkbeer.registries.ItemRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.github.wallev.maidsoulkitchen.task.cook.drinkbeer.TaskDbBeerBarrel", remap = false)
public abstract class TaskDbBeerBarrelInputGuardMixin {
    private static final int maidsoulbrewery$INGREDIENT_SLOTS = 4;
    private static final int maidsoulbrewery$CUP_SLOT = 4;
    private static final int maidsoulbrewery$REQUIRED_CUPS = 4;
    private static final int maidsoulbrewery$STATUS_IDLE = 0;

    @SuppressWarnings("unused")
    public boolean hasInput(Container container) {
        return maidsoulbrewery$hasReturnedBucket(container);
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
            if (!maidsoulbrewery$isValidSlot(container, index)) {
                return;
            }

            int required = maidsoulbrewery$getTargetAmount(index, amounts.get(index));
            int missing = maidsoulbrewery$getMissingAmount(container, index, required, ingredients.get(index));
            if (missing < 0 || !maidsoulbrewery$hasEnoughAvailable(missing, ingredients.get(index))) {
                return;
            }
        }

        boolean changed = false;
        for (int index = 0; index < entries; index++) {
            int required = maidsoulbrewery$getTargetAmount(index, amounts.get(index));
            int missing = maidsoulbrewery$getMissingAmount(container, index, required, ingredients.get(index));
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
            method = "maidShouldMoveTo(Lnet/minecraft/server/level/ServerLevel;Lcom/github/tartaricacid/touhoulittlemaid/entity/passive/EntityMaid;Llekavar/lma/drinkbeer/blockentities/BeerBarrelBlockEntity;Lcom/github/wallev/maidsoulkitchen/task/cook/common/inventory/MaidRecipesManager;)Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void maidsoulbrewery$moveToRefillCups(
            ServerLevel level,
            @Coerce Object maid,
            BeerBarrelBlockEntity barrel,
            MaidRecipesManager<?> recipesManager,
            CallbackInfoReturnable<Boolean> callbackInfo
    ) {
        Container container = barrel.getBrewingInventory();
        IItemHandlerModifiable inputInventory = recipesManager.getInputInv();
        if (maidsoulbrewery$getStatus(barrel) == maidsoulbrewery$STATUS_IDLE
                && maidsoulbrewery$needsCups(container)
                && inputInventory != null
                && maidsoulbrewery$hasEmptyBeerMug(inputInventory)) {
            callbackInfo.setReturnValue(true);
        }
    }

    @Inject(
            method = "tryInsertItem(Lnet/minecraft/server/level/ServerLevel;Lcom/github/tartaricacid/touhoulittlemaid/entity/passive/EntityMaid;Llekavar/lma/drinkbeer/blockentities/BeerBarrelBlockEntity;Lcom/github/wallev/maidsoulkitchen/task/cook/common/inventory/MaidRecipesManager;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void maidsoulbrewery$refillCupsFirst(
            ServerLevel level,
            @Coerce Object maid,
            BeerBarrelBlockEntity barrel,
            MaidRecipesManager<?> recipesManager,
            CallbackInfo callbackInfo
    ) {
        if (maidsoulbrewery$getStatus(barrel) != maidsoulbrewery$STATUS_IDLE) {
            return;
        }

        Container container = barrel.getBrewingInventory();
        IItemHandlerModifiable inputInventory = recipesManager.getInputInv();
        if (inputInventory != null && maidsoulbrewery$fillCups(container, inputInventory)) {
            container.setChanged();
            barrel.setChanged();
            callbackInfo.cancel();
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
        int checkedSlots = Math.min(maidsoulbrewery$INGREDIENT_SLOTS, container.getContainerSize());
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

    private static int maidsoulbrewery$getTargetAmount(int targetSlot, int requested) {
        if (targetSlot >= 0 && targetSlot < maidsoulbrewery$INGREDIENT_SLOTS) {
            return Math.min(requested, 1);
        }
        return requested;
    }

    private static int maidsoulbrewery$getStatus(BeerBarrelBlockEntity barrel) {
        return ((BeerBarrelBlockAccessor) barrel).tlmk$statusCode();
    }

    private static boolean maidsoulbrewery$needsCups(Container container) {
        if (!maidsoulbrewery$isValidSlot(container, maidsoulbrewery$CUP_SLOT)) {
            return false;
        }

        ItemStack cups = container.getItem(maidsoulbrewery$CUP_SLOT);
        return cups.isEmpty()
                || cups.is(ItemRegistry.EMPTY_BEER_MUG.get()) && cups.getCount() < maidsoulbrewery$REQUIRED_CUPS;
    }

    private static boolean maidsoulbrewery$fillCups(Container container, IItemHandlerModifiable source) {
        if (!maidsoulbrewery$needsCups(container)) {
            return false;
        }

        ItemStack cups = container.getItem(maidsoulbrewery$CUP_SLOT);
        int missing = cups.isEmpty() ? maidsoulbrewery$REQUIRED_CUPS : maidsoulbrewery$REQUIRED_CUPS - cups.getCount();
        for (int slot = 0; slot < source.getSlots() && missing > 0; slot++) {
            ItemStack stack = source.getStackInSlot(slot);
            if (stack.isEmpty() || !stack.is(ItemRegistry.EMPTY_BEER_MUG.get())) {
                continue;
            }

            ItemStack extracted = source.extractItem(slot, missing, false);
            if (extracted.isEmpty()) {
                continue;
            }

            ItemStack current = container.getItem(maidsoulbrewery$CUP_SLOT);
            if (current.isEmpty()) {
                container.setItem(maidsoulbrewery$CUP_SLOT, extracted.copy());
            } else {
                ItemStack merged = current.copy();
                merged.grow(extracted.getCount());
                container.setItem(maidsoulbrewery$CUP_SLOT, merged);
            }
            missing -= extracted.getCount();
        }

        return missing < (cups.isEmpty() ? maidsoulbrewery$REQUIRED_CUPS : maidsoulbrewery$REQUIRED_CUPS - cups.getCount());
    }

    private static boolean maidsoulbrewery$hasEmptyBeerMug(IItemHandlerModifiable source) {
        for (int slot = 0; slot < source.getSlots(); slot++) {
            ItemStack stack = source.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.is(ItemRegistry.EMPTY_BEER_MUG.get())) {
                return true;
            }
        }
        return false;
    }

    private static boolean maidsoulbrewery$hasReturnedBucket(Container container) {
        int checkedSlots = Math.min(maidsoulbrewery$INGREDIENT_SLOTS, container.getContainerSize());
        for (int slot = 0; slot < checkedSlots; slot++) {
            if (container.getItem(slot).is(Items.BUCKET)) {
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
