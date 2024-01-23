package com.glodblock.github.glodium;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class GlodiumServer implements ModInitializer {

    private static MinecraftServer server = null;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(GlodiumServer::hookServer);
    }

    private static void hookServer(MinecraftServer run) {
        server = run;
    }

    public static MinecraftServer getServer() {
        return server;
    }

}
