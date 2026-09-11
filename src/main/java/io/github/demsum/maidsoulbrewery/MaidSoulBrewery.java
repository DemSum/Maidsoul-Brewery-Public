package io.github.demsum.maidsoulbrewery;

import com.mojang.logging.LogUtils;
import io.github.demsum.maidsoulbrewery.compat.kaleidoscopecookery.KaleidoscopeCookeryCompat;
import io.github.demsum.maidsoulbrewery.init.MaidSoulBreweryMenus;
import io.github.demsum.maidsoulbrewery.network.MaidSoulBreweryNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(MaidSoulBrewery.MOD_ID)
public final class MaidSoulBrewery {
    public static final String MOD_ID = "maidsoul_brewery";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MaidSoulBrewery(IEventBus modEventBus) {
        MaidSoulBreweryMenus.REGISTRY.register(modEventBus);
        modEventBus.addListener(MaidSoulBreweryNetwork::register);
        LOGGER.info("Loaded MaidSoul Brewery");
        KaleidoscopeCookeryCompat.initialize();
    }
}
