package io.github.demsum.maidsoulbrewery.mixin.drinkbeer;

import com.github.wallev.maidsoulkitchen.task.cook.drinkbeer.BeerBarrelBlockAccessor;
import lekavar.lma.drinkbeer.blockentities.BeerBarrelBlockEntity;
import lekavar.lma.drinkbeer.recipes.BrewingRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = BeerBarrelBlockEntity.class, remap = false, priority = 500)
@Implements(@Interface(iface = BeerBarrelBlockAccessor.class, prefix = "maidsoulbrewery$"))
public abstract class BeerBarrelAccessorBridgeMixin {
    @Shadow
    private int statusCode;

    @Shadow
    @Final
    private BeerBarrelBlockEntity.BrewingInventory brewingInventory;

    @Intrinsic
    public int maidsoulbrewery$tlmk$statusCode() {
        return this.statusCode;
    }

    @Intrinsic
    public boolean maidsoulbrewery$tlmk$canBrew(BrewingRecipe recipe) {
        return recipe.matches(this.brewingInventory, ((BeerBarrelBlockEntity) (Object) this).getLevel());
    }

    @Intrinsic
    public boolean maidsoulbrewery$tlmk$hasEnoughEmptyCap(BrewingRecipe recipe) {
        return recipe.isCupQualified(this.brewingInventory);
    }
}
