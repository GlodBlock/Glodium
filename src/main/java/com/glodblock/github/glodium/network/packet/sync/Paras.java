package com.glodblock.github.glodium.network.packet.sync;

import com.glodblock.github.glodium.util.GlodCodecs;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.connection.ConnectionType;

public class Paras {

    private final RegistryFriendlyByteBuf payload;

    public Paras(byte[] payload, Player player) {
        this.payload = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload), player.registryAccess(), ConnectionType.NEOFORGE);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        var nil = this.payload.readBoolean();
        if (!nil) {
            return null;
        }
        if (type.isPrimitive()) {
            throw new IllegalArgumentException("Use direct getter for primitive type");
        }
        if (type == String.class) {
            return (T) this.getString();
        }
        if (type == CompoundTag.class) {
            return (T) this.getNBT();
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

    public int getInt() {
        return this.payload.readVarInt();
    }

    public long getLong() {
        return this.payload.readVarLong();
    }

    public short getShort() {
        return this.payload.readShort();
    }

    public byte getByte() {
        return this.payload.readByte();
    }

    public boolean getBoolean() {
        return this.payload.readBoolean();
    }

    public double getDouble() {
        return this.payload.readDouble();
    }

    public String getString() {
        return this.payload.readUtf(1024);
    }

    public CompoundTag getNBT() {
        return this.payload.readNbt();
    }

}
