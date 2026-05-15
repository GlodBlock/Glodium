package com.glodblock.github.glodium.network.packet.sync;

import com.google.common.primitives.Primitives;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;

public class Paras {

    private static final IdentityHashMap<Class<?>, ByteReader<?>> DECODERS = new IdentityHashMap<>();
    private final RegistryFriendlyByteBuf payload;

    static {
        DECODERS.put(Integer.class, FriendlyByteBuf::readVarInt);
        DECODERS.put(Long.class, FriendlyByteBuf::readVarLong);
        DECODERS.put(Short.class, FriendlyByteBuf::readShort);
        DECODERS.put(Byte.class, FriendlyByteBuf::readByte);
        DECODERS.put(Boolean.class, FriendlyByteBuf::readBoolean);
        DECODERS.put(Double.class, FriendlyByteBuf::readDouble);
        DECODERS.put(String.class, FriendlyByteBuf::readUtf);
        DECODERS.put(ItemStack.class, ByteReader.wrap(ItemStack.OPTIONAL_STREAM_CODEC));
        DECODERS.put(FluidStack.class, ByteReader.wrap(FluidStack.OPTIONAL_STREAM_CODEC));
        DECODERS.put(CompoundTag.class, buf -> buf.readNbt());
    }

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
            type = Primitives.wrap(type);
        }
        var decoder = DECODERS.get(type);
        if (decoder == null && type.isEnum()) {
            final var enumValues = type.getEnumConstants();
            decoder = buf -> enumValues[buf.readInt()];
            DECODERS.put(type, decoder);
        }
        if (decoder != null) {
            return (T) decoder.read(this.payload);
        }
        throw new IllegalArgumentException("No such type: " + type);
    }

    public interface ByteReader<T> {

        T read(RegistryFriendlyByteBuf buf);

        static <T> ByteReader<T> wrap(StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull T> codec) {
            return codec::decode;
        }

    }

}
