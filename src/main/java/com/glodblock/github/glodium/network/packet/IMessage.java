package com.glodblock.github.glodium.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public interface IMessage extends CustomPacketPayload {

    void toBytes(FriendlyByteBuf buf);

    void fromBytes(FriendlyByteBuf buf);

    void onMessage(Player player);

    boolean isClient();

    //////////////////////
    //     NEO FORGE    //
    //////////////////////
    @Override
    default void write(@NotNull FriendlyByteBuf buf) {
        this.toBytes(buf);
    }

}
