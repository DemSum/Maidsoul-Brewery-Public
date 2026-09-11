package io.github.demsum.maidsoulbrewery.menu;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.task.TaskConfigContainer;
import io.github.demsum.maidsoulbrewery.compat.kaleidoscopecookery.KaleidoscopeCookeryCompat;
import io.github.demsum.maidsoulbrewery.compat.touhoulittlemaid.RecipeFilterDataKeys;
import io.github.demsum.maidsoulbrewery.init.MaidSoulBreweryMenus;
import io.github.demsum.maidsoulbrewery.recipe.RecipeFilterData;
import io.github.demsum.maidsoulbrewery.recipe.RecipeOption;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public final class SteamerRecipeFilterMenu extends TaskConfigContainer {
    public SteamerRecipeFilterMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf data) {
        this(containerId, inventory, data.readInt());
    }

    public SteamerRecipeFilterMenu(int containerId, Inventory inventory, int maidId) {
        super(MaidSoulBreweryMenus.STEAMER_RECIPE_FILTER.get(), containerId, inventory, maidId);
    }

    public List<RecipeOption> getRecipeOptions() {
        EntityMaid maid = getMaid();
        return maid == null ? List.of() : KaleidoscopeCookeryCompat.getSteamerRecipeOptions(maid.level());
    }

    public RecipeFilterData getFilterData() {
        EntityMaid maid = getMaid();
        return maid == null ? RecipeFilterData.DEFAULT : RecipeFilterDataKeys.getSteamer(maid);
    }

    public int getMaidEntityId() {
        EntityMaid maid = getMaid();
        return maid == null ? -1 : maid.getId();
    }
}
