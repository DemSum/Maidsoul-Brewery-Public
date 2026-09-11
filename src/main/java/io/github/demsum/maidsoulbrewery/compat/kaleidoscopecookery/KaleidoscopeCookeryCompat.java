package io.github.demsum.maidsoulbrewery.compat.kaleidoscopecookery;

import io.github.demsum.maidsoulbrewery.MaidSoulBrewery;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import io.github.demsum.maidsoulbrewery.recipe.RecipeOption;
import java.util.List;
import net.neoforged.fml.ModList;
import net.minecraft.world.level.Level;

public final class KaleidoscopeCookeryCompat {
    public static final String MOD_ID = "kaleidoscope_cookery";

    private static boolean initialized;
    private static boolean tasksRegistered;

    private KaleidoscopeCookeryCompat() {
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(MOD_ID);
    }

    public static void initialize() {
        if (initialized || !isLoaded()) {
            return;
        }

        SteamerAdapter.verifyApi();
        StockpotAdapter.verifyApi();
        initialized = true;
        MaidSoulBrewery.LOGGER.info("Enabled Kaleidoscope Cookery compatibility adapters");
    }

    public static void registerMaidTasks(TaskManager manager) {
        if (tasksRegistered || !isLoaded()) {
            return;
        }
        KaleidoscopeCookeryTaskRegistration.register(manager);
        tasksRegistered = true;
    }

    public static List<RecipeOption> getSteamerRecipeOptions(Level level) {
        return isLoaded() ? SteamerAdapter.getRecipeOptions(level) : List.of();
    }
}
