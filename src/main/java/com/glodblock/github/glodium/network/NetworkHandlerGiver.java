package com.glodblock.github.glodium.network;

import net.fabricmc.api.EnvType;
import org.jetbrains.annotations.NotNull;

public final class NetworkHandlerGiver {

    private NetworkHandlerGiver() {
        // NO-OP
    }

    public static NetworkHandlerServer create(String modid, @NotNull EnvType side) {
        return switch (side) {
            case SERVER -> new NetworkHandlerServer(modid);
            case CLIENT -> new NetworkHandlerClient(modid);
        };
    }

}
