package com.glodblock.github.glodium.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public interface IMessage extends CustomPacketPayload {

    void toBytes(RegistryFriendlyByteBuf buf);

    void fromBytes(RegistryFriendlyByteBuf buf);

    default void onMessage(IPayloadContext ctx) {
        this.onMessage(ctx.player());
    }

    default void onMessage(Player player) {

    }

    boolean isClient();

    ResourceLocation id();

    @Override
    default CustomPacketPayload.@NotNull Type<? extends IMessage> type() {
        return new Type<>(id());
    }

}
