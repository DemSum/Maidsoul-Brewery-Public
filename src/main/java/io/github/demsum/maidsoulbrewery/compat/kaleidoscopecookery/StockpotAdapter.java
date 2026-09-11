package io.github.demsum.maidsoulbrewery.compat.kaleidoscopecookery;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IStockpot;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class StockpotAdapter {
    private StockpotAdapter() {
    }

    static void verifyApi() {
        if (!IStockpot.class.isAssignableFrom(StockpotBlockEntity.class)) {
            throw new IllegalStateException("Kaleidoscope Cookery StockpotBlockEntity no longer implements IStockpot");
        }
    }

    public static boolean supports(BlockEntity blockEntity) {
        return blockEntity instanceof StockpotBlockEntity;
    }

    public static Optional<Snapshot> inspect(BlockEntity blockEntity, Level level) {
        if (!(blockEntity instanceof StockpotBlockEntity stockpot)) {
            return Optional.empty();
        }

        List<ItemStack> inputs = stockpot.getInputs().stream().map(ItemStack::copy).toList();
        return Optional.of(new Snapshot(
                stockpot.getBlockPos(),
                Status.fromValue(stockpot.getStatus()),
                stockpot.hasHeatSource(level),
                stockpot.hasLid(),
                inputs,
                stockpot.getResult().copy(),
                stockpot.getTakeoutCount(),
                stockpot.getSoupBaseId(),
                stockpot.getLidItem().copy()
        ));
    }

    public static boolean operateLid(BlockEntity blockEntity, Level level, LivingEntity user, ItemStack heldStack) {
        IStockpot stockpot = currentStockpot(blockEntity, level);
        return stockpot != null && stockpot.onLitClick(level, user, heldStack);
    }

    public static boolean addSoupBase(BlockEntity blockEntity, Level level, LivingEntity user, ItemStack soupBase) {
        IStockpot stockpot = currentStockpot(blockEntity, level);
        return stockpot != null
                && stockpot.getStatus() == IStockpot.PUT_SOUP_BASE
                && !stockpot.hasLid()
                && stockpot.addSoupBase(level, user, soupBase);
    }

    public static boolean removeSoupBase(BlockEntity blockEntity, Level level, LivingEntity user, ItemStack container) {
        IStockpot stockpot = currentStockpot(blockEntity, level);
        return stockpot != null && !stockpot.hasLid() && stockpot.removeSoupBase(level, user, container);
    }

    public static boolean addIngredient(BlockEntity blockEntity, Level level, LivingEntity user, ItemStack ingredient) {
        IStockpot stockpot = currentStockpot(blockEntity, level);
        return stockpot != null
                && stockpot.getStatus() == IStockpot.PUT_INGREDIENT
                && !stockpot.hasLid()
                && stockpot.addIngredient(level, user, ingredient);
    }

    public static boolean removeIngredient(BlockEntity blockEntity, Level level, LivingEntity user) {
        IStockpot stockpot = currentStockpot(blockEntity, level);
        return stockpot != null
                && stockpot.getStatus() == IStockpot.PUT_INGREDIENT
                && !stockpot.hasLid()
                && stockpot.removeIngredient(level, user);
    }

    public static boolean takeProduct(BlockEntity blockEntity, Level level, LivingEntity user, ItemStack carrier) {
        IStockpot stockpot = currentStockpot(blockEntity, level);
        return stockpot != null
                && stockpot.getStatus() == IStockpot.FINISHED
                && !stockpot.hasLid()
                && stockpot.takeOutProduct(level, user, carrier);
    }

    private static IStockpot currentStockpot(BlockEntity blockEntity, Level level) {
        if (!(blockEntity instanceof StockpotBlockEntity stockpot)
                || stockpot.isRemoved()
                || level.getBlockEntity(stockpot.getBlockPos()) != stockpot) {
            return null;
        }
        return stockpot;
    }

    public enum Status {
        PUT_SOUP_BASE,
        PUT_INGREDIENT,
        COOKING,
        FINISHED,
        UNKNOWN;

        static Status fromValue(int status) {
            return switch (status) {
                case IStockpot.PUT_SOUP_BASE -> PUT_SOUP_BASE;
                case IStockpot.PUT_INGREDIENT -> PUT_INGREDIENT;
                case IStockpot.COOKING -> COOKING;
                case IStockpot.FINISHED -> FINISHED;
                default -> UNKNOWN;
            };
        }
    }

    public record Snapshot(
            BlockPos pos,
            Status status,
            boolean hasDirectHeatSource,
            boolean hasLid,
            List<ItemStack> inputs,
            ItemStack result,
            int takeoutCount,
            ResourceLocation soupBaseId,
            ItemStack lidItem
    ) {
        public Snapshot {
            inputs = inputs.stream().map(ItemStack::copy).toList();
            result = result.copy();
            lidItem = lidItem.copy();
        }

        @Override
        public List<ItemStack> inputs() {
            return inputs.stream().map(ItemStack::copy).toList();
        }

        @Override
        public ItemStack result() {
            return result.copy();
        }

        @Override
        public ItemStack lidItem() {
            return lidItem.copy();
        }

        public boolean canTakeProduct() {
            return status == Status.FINISHED && !hasLid && !result.isEmpty() && takeoutCount > 0;
        }
    }
}
