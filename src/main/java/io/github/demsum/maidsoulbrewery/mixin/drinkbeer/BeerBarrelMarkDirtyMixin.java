package io.github.demsum.maidsoulbrewery.mixin.drinkbeer;

import io.github.demsum.maidsoulbrewery.bridge.DrinkBeerBarrelMarkDirtyBridge;
import lekavar.lma.drinkbeer.blockentities.BeerBarrelBlockEntity;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = BeerBarrelBlockEntity.class, remap = false, priority = 2000)
@Implements(@Interface(iface = DrinkBeerBarrelMarkDirtyBridge.class, prefix = "maidsoulbrewery$"))
public abstract class BeerBarrelMarkDirtyMixin {
    @Shadow
    public abstract void updateBE();

    @Intrinsic
    public void maidsoulbrewery$markDirty() {
        this.updateBE();
    }
}
