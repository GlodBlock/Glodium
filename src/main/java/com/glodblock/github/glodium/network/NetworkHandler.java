package com.glodblock.github.glodium.network;

import com.glodblock.github.glodium.Glodium;
import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class NetworkHandler {

    protected final List<Supplier<IMessage>> LAZY_INIT = new ArrayList<>();
    protected final String modid;
    protected PayloadRegistrar registrar;

    public NetworkHandler(String modid) {
        this.modid = modid;
    }

    public void onRegister(RegisterPayloadHandlersEvent event) {
        this.registrar = event.registrar(this.modid);
        this.initPackets();
    }

    protected void registerPacket(Supplier<IMessage> factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Packet Constructor is null");
        }
        this.LAZY_INIT.add(factory);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void initPackets() {
        for (var factory : this.LAZY_INIT) {
            var instance = factory.get();
            if (instance.isClient()) {
                this.registrar.playToClient(
                        (CustomPacketPayload.Type) instance.type(),
                        this.codec(factory),
                        IMessage::onMessage
                );
            } else {
                this.registrar.playToServer(
                        (CustomPacketPayload.Type) instance.type(),
                        this.codec(factory),
                        IMessage::onMessage
                );
            }
        }
    }

    protected StreamCodec<? super RegistryFriendlyByteBuf, ? extends IMessage> codec(Supplier<? extends IMessage> factory) {
        return StreamCodec.of(
                (pBuffer, pValue) -> pValue.toBytes(pBuffer),
                pBuffer -> {
                    IMessage msg = factory.get();
                    msg.fromBytes(pBuffer);
                    return msg;
                }
        );
    }

    public void sendToAll(IMessage message) {
        PacketDistributor.sendToAllPlayers(message);
    }

    public void sendTo(IMessage message, ServerPlayer player) {
        player.connection.send(message);
    }

    public void sendToAllAround(IMessage message, ServerLevel world, BlockPos pos, double r, @Nullable ServerPlayer excludePlayer) {
        var server = Glodium.INSTANCE.getServer();
        if (server != null) {
            PacketDistributor.sendToPlayersNear(world, excludePlayer, pos.getX(), pos.getY(), pos.getZ(), r, message);
        }
    }

    public void sendToAllAround(IMessage message, ServerLevel world, Position pos, double r, @Nullable ServerPlayer excludePlayer) {
        var server = Glodium.INSTANCE.getServer();
        if (server != null) {
            PacketDistributor.sendToPlayersNear(world, excludePlayer, pos.x(), pos.y(), pos.z(), r, message);
        }
    }

    public void sendToServer(IMessage message) {
        PacketDistributor.sendToServer(message);
    }

}
