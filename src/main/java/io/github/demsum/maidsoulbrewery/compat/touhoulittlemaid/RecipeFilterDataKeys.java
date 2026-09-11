package io.github.demsum.maidsoulbrewery.compat.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import com.github.tartaricacid.touhoulittlemaid.entity.data.TaskDataRegister;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import io.github.demsum.maidsoulbrewery.MaidSoulBrewery;
import io.github.demsum.maidsoulbrewery.recipe.RecipeFilterData;
import net.minecraft.resources.ResourceLocation;

public final class RecipeFilterDataKeys {
    public static final ResourceLocation STEAMER_TASK_ID =
            ResourceLocation.fromNamespaceAndPath(MaidSoulBrewery.MOD_ID, "kaleidoscope_steamer");

    private static TaskDataKey<RecipeFilterData> steamer;

    private RecipeFilterDataKeys() {
    }

    public static void register(TaskDataRegister register) {
        steamer = register.register(STEAMER_TASK_ID, RecipeFilterData.CODEC);
    }

    public static TaskDataKey<RecipeFilterData> steamerKey() {
        if (steamer == null) {
            throw new IllegalStateException("Steamer recipe filter data was not registered");
        }
        return steamer;
    }

    public static RecipeFilterData getSteamer(EntityMaid maid) {
        return maid.getOrCreateData(steamerKey(), RecipeFilterData.DEFAULT);
    }
}
