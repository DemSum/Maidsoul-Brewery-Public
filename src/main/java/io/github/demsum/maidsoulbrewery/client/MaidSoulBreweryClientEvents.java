package io.github.demsum.maidsoulbrewery.client;

import io.github.demsum.maidsoulbrewery.MaidSoulBrewery;
import io.github.demsum.maidsoulbrewery.init.MaidSoulBreweryMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = MaidSoulBrewery.MOD_ID, value = Dist.CLIENT)
public final class MaidSoulBreweryClientEvents {
    private MaidSoulBreweryClientEvents() {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MaidSoulBreweryMenus.STEAMER_RECIPE_FILTER.get(), SteamerRecipeFilterScreen::new);
    }
}
