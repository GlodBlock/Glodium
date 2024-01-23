package com.glodblock.github.glodium.network;

import com.glodblock.github.glodium.Glodium;
import com.glodblock.github.glodium.network.packet.IMessage;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.RunningOnDifferentThreadException;

public class NetworkHandlerClient extends NetworkHandlerServer {

    NetworkHandlerClient(String modid) {
        super(modid);
        ClientPlayNetworking.registerGlobalReceiver(this.channel, this::clientPacket);
    }

    public void clientPacket(Minecraft client, ClientPacketListener handler, FriendlyByteBuf payload, PacketSender responseSender) {
        try {
            var packet = this.packetFactoryMap.get(payload.readVarInt()).get();
            if (packet.isClient()) {
                packet.fromBytes(payload);
                client.execute(() -> {
                    try {
                        packet.onMessage(client.player);
                    } catch (IllegalArgumentException e) {
                        Glodium.LOGGER.warn(e.getMessage());
                    }
                });
            }
        } catch (RunningOnDifferentThreadException ignored) {
        }
    }

    @Override
    public void sendToServer(IMessage<?> message) {
        ClientPlayNetworking.send(channel, toFMLPacket(message));
    }

}
