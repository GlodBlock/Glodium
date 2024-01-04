package com.glodblock.github.glodium;

import com.glodblock.github.glodium.client.render.highlight.HighlightRender;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

@Mod(Glodium.MODID)
public class Glodium {

    public static final String MODID = "glodium";
    public static Glodium INSTANCE;
    public static final Logger LOGGER = LogUtils.getLogger();

    public Glodium() {
        assert INSTANCE == null;
        INSTANCE = this;
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.addListener(HighlightRender::hook));
    }

    public static ResourceLocation id(String id, String path) {
        return new ResourceLocation(id, path);
    }

    public MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

}
