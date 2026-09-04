package io.github.demsum.maidsoulbrewery.mixin.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.entity.task.crop.SpecialCropManager;
import io.github.demsum.maidsoulbrewery.compat.farmersdelight.FarmerDelightCropCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SpecialCropManager.class, remap = false)
public abstract class SpecialCropManagerMixin {
    @Inject(method = "init", at = @At("HEAD"), remap = false)
    private static void maidsoulbrewery$registerFarmerDelightMushroomColonies(CallbackInfo callbackInfo) {
        FarmerDelightCropCompat.registerSpecialCropHandlers();
    }
}
