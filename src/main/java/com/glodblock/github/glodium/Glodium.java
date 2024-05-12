package com.glodblock.github.glodium;

import com.glodblock.github.glodium.client.render.highlight.HighlightRender;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

@Mod(Glodium.MODID)
public class Glodium {

    public static final String MODID = "glodium";
    public static Glodium INSTANCE;
    public static final Logger LOGGER = LogUtils.getLogger();

    public Glodium() {
        assert INSTANCE == null;
        INSTANCE = this;
        if (FMLEnvironment.dist.isClient()) {
            NeoForge.EVENT_BUS.addListener(HighlightRender::hook);
        }
    }

    public MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    public static ResourceLocation id(String modid, String name) {
        return new ResourceLocation(modid, name);
    }

}
