package com.github.gtexpert.gtbm.mixins;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraftforge.fml.common.Loader;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.api.util.ModLog;
import com.github.gtexpert.gtbm.api.util.Mods;

import zone.rong.mixinbooter.ILateMixinLoader;

public class GTBMMixinLoader implements ILateMixinLoader {

    public static final Map<String, Boolean> modMixinsConfig = Stream.of(
            new AbstractMap.SimpleImmutableEntry<>(Mods.Names.GREGICALITY_MULTIBLOCKS,
                    Mods.GregicalityMultiblocks.isModLoaded()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    @Override
    public List<String> getMixinConfigs() {
        return modMixinsConfig.keySet().stream().map(mod -> "mixins." + ModValues.MODID + "." + mod + ".json")
                .collect(Collectors.toList());
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        String[] parts = mixinConfig.split("\\.");

        if (parts.length != 4) {
            ModLog.logger.fatal("Mixin Config Check Failed! Invalid Length.");
            ModLog.logger.fatal("Mixin Config: " + mixinConfig);
            return true;
        }

        if (!Objects.equals(parts[1], ModValues.MODID)) {
            ModLog.logger.error("Non GTExpertCore Mixin Found in Mixin Queue. This is probably an error. Skipping...");
            ModLog.logger.error("Mixin Config: " + mixinConfig);
            return true;
        }

        if (!Loader.isModLoaded(parts[2])) {
            ModLog.logger.error("Mod '" + parts[2] +
                    "' is not loaded. If this is a normal GTExpertCore instance, this is probably an error.");
            ModLog.logger.error("Not Loading Mixin Config " + mixinConfig);
            return false;
        }

        if (!modMixinsConfig.containsKey(parts[2]) || !modMixinsConfig.get(parts[2])) {
            ModLog.logger.info("Integration for Mod '" + parts[2] + "' is not enabled, or does not exist.");
            ModLog.logger.info("Not Loading Mixin Config " + mixinConfig);
            return false;
        }

        return true;
    }
}
