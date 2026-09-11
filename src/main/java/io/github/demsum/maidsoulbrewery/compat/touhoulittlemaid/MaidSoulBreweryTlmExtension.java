package io.github.demsum.maidsoulbrewery.compat.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.entity.task.crop.SpecialCropManager;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import io.github.demsum.maidsoulbrewery.compat.farmersdelight.FarmerDelightCropCompat;
import io.github.demsum.maidsoulbrewery.compat.kaleidoscopecookery.KaleidoscopeCookeryCompat;

@LittleMaidExtension
public final class MaidSoulBreweryTlmExtension implements ILittleMaid {
    @Override
    public void addMaidTask(TaskManager manager) {
        KaleidoscopeCookeryCompat.registerMaidTasks(manager);
    }

    @Override
    public void registerTaskData(com.github.tartaricacid.touhoulittlemaid.entity.data.TaskDataRegister register) {
        RecipeFilterDataKeys.register(register);
    }

    @Override
    public void registerSpecialCropHandler(SpecialCropManager manager) {
        FarmerDelightCropCompat.registerSpecialCropHandlers(manager);
    }
}
