package com.glodblock.github.glodium.network;

import com.glodblock.github.glodium.Glodium;
import com.glodblock.github.glodium.GlodiumServer;
import com.glodblock.github.glodium.network.packet.IMessage;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class NetworkHandlerServer {

    protected final ResourceLocation channel;
    int id = 0;
    protected final Int2ObjectMap<Supplier<IMessage<?>>> packetFactoryMap = new Int2ObjectOpenHashMap<>();
    protected final Object2IntMap<Class<?>> packetIDMap = new Object2IntOpenHashMap<>();

    NetworkHandlerServer(String modid) {
        this.channel = Glodium.id(modid, "network");
        ServerPlayNetworking.registerGlobalReceiver(this.channel, this::serverPacket);
    }

    public void registerPacket(Class<?> clazz, Supplier<IMessage<?>> factory) {
        this.packetIDMap.put(clazz, this.id);
        this.packetFactoryMap.put(this.id, factory);
        this.id ++;
    }

    public void serverPacket(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf payload, PacketSender responseSender) {
        try {
            var packet = this.packetFactoryMap.get(payload.readVarInt()).get();
            if (!packet.isClient()) {
                packet.fromBytes(payload);
                server.execute(() -> packet.onMessage(player));
            }
        } catch (final RunningOnDifferentThreadException ignored) {
        }
    }

    public void sendToAll(IMessage<?> message) {
        var server = GlodiumServer.getServer();
        if (server != null) {
            PlayerLookup.all(server).forEach(player -> ServerPlayNetworking.send(player, this.channel, toFMLPacket(message)));
        }
    }

    public void sendTo(IMessage<?> message, ServerPlayer player) {
        ServerPlayNetworking.send(player, channel, toFMLPacket(message));
    }

    public void sendToAllAround(IMessage<?> message, TargetPoint point) {
        PlayerLookup.around((ServerLevel) point.level, new Vec3(point.x, point.y, point.z), point.r2)
                .forEach(player -> {
                    if (player != point.excluded) {
                        ServerPlayNetworking.send(player, channel, toFMLPacket(message));
                    }
                });
    }

    public void sendToServer(IMessage<?> message) {
        throw new UnsupportedOperationException();
    }

    public FriendlyByteBuf toFMLPacket(IMessage<?> message) {
        var bytes = new FriendlyByteBuf(Unpooled.buffer(1024));
        var id = this.packetIDMap.getOrDefault(message.getPacketClass(), -1);
        if (id == -1) {
            Glodium.LOGGER.error(String.format("Unregistered Packet: %s", message.getPacketClass()));
        }
        bytes.writeVarInt(id);
        message.toBytes(bytes);
        return bytes;
    }

}
