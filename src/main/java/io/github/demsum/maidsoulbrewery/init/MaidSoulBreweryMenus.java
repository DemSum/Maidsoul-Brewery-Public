package io.github.demsum.maidsoulbrewery.init;

import io.github.demsum.maidsoulbrewery.MaidSoulBrewery;
import io.github.demsum.maidsoulbrewery.menu.SteamerRecipeFilterMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MaidSoulBreweryMenus {
    public static final DeferredRegister<MenuType<?>> REGISTRY =
            DeferredRegister.create(Registries.MENU, MaidSoulBrewery.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<SteamerRecipeFilterMenu>> STEAMER_RECIPE_FILTER =
            REGISTRY.register("steamer_recipe_filter", () ->
                    IMenuTypeExtension.create(SteamerRecipeFilterMenu::new));

    private MaidSoulBreweryMenus() {
    }
}
