package io.github.demsum.maidsoulbrewery.compat.kaleidoscopecookery;

import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import io.github.demsum.maidsoulbrewery.MaidSoulBrewery;

final class KaleidoscopeCookeryTaskRegistration {
    private KaleidoscopeCookeryTaskRegistration() {
    }

    static void register(TaskManager manager) {
        manager.add(new SteamerMaidTask());
        MaidSoulBrewery.LOGGER.info("Registered Kaleidoscope Cookery steamer maid task");
    }
}
