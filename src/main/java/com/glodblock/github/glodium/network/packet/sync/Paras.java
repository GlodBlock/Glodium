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
        if (type == String.class) {
            return (T) this.getString();
        }
        if (type == CompoundTag.class) {
            return (T) this.getNBT();
        }
        var nil = this.payload.readBoolean();
        if (!nil) {
            return null;
        }
        if (type.isPrimitive()) {
            throw new IllegalArgumentException("Use direct getter for primitive type");
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
        if (this.payload.readBoolean()) {
            return this.payload.readVarInt();
        } else {
            return 0;
        }
    }

    public long getLong() {
        if (this.payload.readBoolean()) {
            return this.payload.readVarLong();
        } else {
            return 0;
        }
    }

    public short getShort() {
        if (this.payload.readBoolean()) {
            return this.payload.readShort();
        } else {
            return 0;
        }
    }

    public byte getByte() {
        if (this.payload.readBoolean()) {
            return this.payload.readByte();
        } else {
            return 0;
        }
    }

    public boolean getBoolean() {
        if (this.payload.readBoolean()) {
            return this.payload.readBoolean();
        } else {
            return false;
        }
    }

    public double getDouble() {
        if (this.payload.readBoolean()) {
            return this.payload.readDouble();
        } else {
            return 0;
        }
    }

    public String getString() {
        if (this.payload.readBoolean()) {
            return this.payload.readUtf(1024);
        } else {
            return null;
        }
    }

    public CompoundTag getNBT() {
        if (this.payload.readBoolean()) {
            return this.payload.readNbt();
        } else {
            return null;
        }
    }

}
