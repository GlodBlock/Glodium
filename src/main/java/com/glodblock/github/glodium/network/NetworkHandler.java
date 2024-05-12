package com.glodblock.github.glodium.network;

import com.glodblock.github.glodium.Glodium;
import com.glodblock.github.glodium.network.packet.IMessage;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.IPlayPayloadHandler;
import net.neoforged.neoforge.network.registration.IDirectionAwarePayloadHandlerBuilder;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

import java.util.function.Supplier;

public class NetworkHandler {

    protected final Object2ObjectMap<ResourceLocation, Supplier<IMessage>> ID2Packet = new Object2ObjectOpenHashMap<>();
    protected final String modid;
    protected IPayloadRegistrar registrar;

    public NetworkHandler(String modid) {
        this.modid = modid;
    }

    public void onRegister(RegisterPayloadHandlerEvent event) {
        this.registrar = event.registrar(this.modid);
        this.initPackets();
    }

    protected void registerPacket(ResourceLocation id, Supplier<IMessage> factory) {
        if (this.ID2Packet.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate Packet Key: %s".formatted(id));
        }
        if (factory == null) {
            throw new IllegalArgumentException("Packet Constructor is null");
        }
        this.ID2Packet.put(id, factory);
    }

    private void initPackets() {
        for (var pair : this.ID2Packet.entrySet()) {
            var id = pair.getKey();
            this.registrar.play(
                    id,
                    bytes -> fromBytes(bytes, pair.getValue()),
                    builder -> exec(builder, pair.getValue())
            );
        }
    }

    private IMessage fromBytes(FriendlyByteBuf buf, Supplier<IMessage> factory) {
        var packet = factory.get();
        packet.fromBytes(buf);
        return packet;
    }

    private <T extends IMessage> void exec(IDirectionAwarePayloadHandlerBuilder<T, IPlayPayloadHandler<T>> builder, Supplier<IMessage> factory) {
        var packet = factory.get();
        if (packet.isClient()) {
            builder.client((p, c) -> c.workHandler().execute(() -> c.player().ifPresent(p::onMessage)));
        } else {
            builder.server((p, c) -> c.workHandler().execute(
                    () -> {
                        if (c.player().orElse(null) instanceof ServerPlayer sp) {
                            p.onMessage(sp);
                        }
                    }
            ));
        }
    }

    public void sendToAll(IMessage message) {
        PacketDistributor.ALL.noArg().send(message);
    }

    public void sendTo(IMessage message, ServerPlayer player) {
        player.connection.send(message);
    }

    public void sendToAllAround(IMessage message, PacketDistributor.TargetPoint point) {
        var server = Glodium.INSTANCE.getServer();
        if (server != null) {
            PacketDistributor.NEAR.with(point).send(message);
        }
    }

    public void sendToServer(IMessage message) {
        PacketDistributor.SERVER.noArg().send(message);
    }

}
