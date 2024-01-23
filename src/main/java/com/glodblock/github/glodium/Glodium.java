package com.glodblock.github.glodium;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public abstract class Glodium {

    public static final String MODID = "glodium";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation id(String id, String path) {
        return new ResourceLocation(id, path);
    }

}
