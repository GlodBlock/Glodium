package com.glodblock.github.glodium;

import com.glodblock.github.glodium.client.render.highlight.HighlightRender;
import com.glodblock.github.glodium.xmod.XModManager;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

@Mod(Glodium.MODID)
public class Glodium {

    public static final String MODID = "glodium";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static Glodium INSTANCE;

    public Glodium() {
        XModManager.scan();
        assert INSTANCE == null;
        INSTANCE = this;
        XModManager.init();
        NeoForge.EVENT_BUS.addListener(this::common);
        NeoForge.EVENT_BUS.addListener(this::client);
        if (FMLEnvironment.getDist().isClient()) {
            NeoForge.EVENT_BUS.addListener(HighlightRender::hook);
        }
    }

    private void common(FMLCommonSetupEvent event) {
        XModManager.common();
    }

    private void client(FMLClientSetupEvent event) {
        XModManager.client();
    }

    public MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    public static Identifier id(String modid, String name) {
        return Identifier.fromNamespaceAndPath(modid, name);
    }

}
