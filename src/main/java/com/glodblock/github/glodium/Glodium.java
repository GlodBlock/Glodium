package com.glodblock.github.glodium;

import com.glodblock.github.glodium.client.render.highlight.HighlightRender;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@Mod(Glodium.MODID)
public class Glodium {

    public static final String MODID = "glodium";
    public static Glodium INSTANCE;

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

}
