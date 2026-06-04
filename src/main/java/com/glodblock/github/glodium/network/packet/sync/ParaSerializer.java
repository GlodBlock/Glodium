package com.glodblock.github.glodium.network.packet.sync;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Map;

public final class ParaSerializer {

    //////////////////////////////
    //                          //
    //     Serializer Zone      //
    //                          //
    //////////////////////////////

    private static final Map<Class<?>, StreamCodec<? super RegistryFriendlyByteBuf, ?>> CODEC_MAP = new IdentityHashMap<>();

    public static <T> void addCodec(Class<T> type, StreamCodec<? super RegistryFriendlyByteBuf, @NotNull T> codec) {
        if (!CODEC_MAP.containsKey(type)) {
            CODEC_MAP.put(type, codec);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> StreamCodec<? super RegistryFriendlyByteBuf, @NotNull T> getCodec(Class<T> type) {
        return (StreamCodec<? super RegistryFriendlyByteBuf, @NotNull T>) CODEC_MAP.get(type);
    }

    static {
        CODEC_MAP.put(ItemStack.class, ItemStack.OPTIONAL_STREAM_CODEC);
        CODEC_MAP.put(FluidStack.class, FluidStack.OPTIONAL_STREAM_CODEC);
    }

    @SuppressWarnings("unchecked")
    public static void to(Object[] obj, RegistryFriendlyByteBuf buf) {
        for (var o : obj) {
            if (o == null) {
                buf.writeBoolean(false);
                continue;
            } else {
                buf.writeBoolean(true);
            }
            switch (o) {
                case Integer i -> buf.writeVarInt(i);
                case Long l -> buf.writeVarLong(l);
                case Short s -> buf.writeShort(s);
                case Byte b -> buf.writeByte(b);
                case Boolean b -> buf.writeBoolean(b);
                case Double d -> buf.writeDouble(d);
                case String s -> buf.writeUtf(s, 1024);
                case CompoundTag t -> buf.writeNbt(t);
                case Enum<?> e -> buf.writeVarInt(e.ordinal());
                default -> {
                    var decoder = (StreamCodec<? super RegistryFriendlyByteBuf, @NotNull Object>) getCodec(o.getClass());
                    if (decoder != null) {
                        decoder.encode(buf, o);
                    } else {
                        throw new IllegalStateException("Unknown type: " + o.getClass());
                    }
                }
            }
        }
    }

}
