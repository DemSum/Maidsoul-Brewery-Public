package io.github.demsum.maidsoulbrewery;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(MaidSoulBrewery.MOD_ID)
public final class MaidSoulBrewery {
    public static final String MOD_ID = "maidsoul_brewery";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MaidSoulBrewery() {
        LOGGER.info("Loaded MaidSoul Brewery");
    }
}
