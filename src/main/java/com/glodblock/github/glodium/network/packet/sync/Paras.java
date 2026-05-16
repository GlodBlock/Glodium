package com.glodblock.github.glodium.network.packet.sync;

import com.glodblock.github.glodium.util.GlodCodecs;
import com.google.common.primitives.Primitives;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.connection.ConnectionType;

public class Paras {

    private final RegistryFriendlyByteBuf payload;

    public Paras(byte[] payload, Player player) {
        this.payload = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload), player.registryAccess(), ConnectionType.NEOFORGE);
    }

    public <T> T get(Class<T> type) {
        var nil = this.payload.readBoolean();
        if (!nil) {
            return null;
        }
        if (type.isPrimitive()) {
            type = Primitives.wrap(type);
        }
        var decoder = ParaSerializer.getCodec(type);
        if (decoder == null && type.isEnum()) {
            decoder = GlodCodecs.enumerate(type);
            ParaSerializer.addCodec(type, decoder);
        }
        if (decoder != null) {
            return decoder.decode(this.payload);
        }
        throw new IllegalArgumentException("No such type: " + type);
    }

}
