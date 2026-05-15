package com.glodblock.github.glodium.network.packet.sync;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public final class ParaSerializer {

    //////////////////////////////
    //                          //
    //     Serializer Zone      //
    //                          //
    //////////////////////////////

    public static void to(Object[] obj, RegistryFriendlyByteBuf buf) {
        buf.writeByte(obj.length);
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
                case ItemStack s -> ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, s);
                case FluidStack s -> FluidStack.OPTIONAL_STREAM_CODEC.encode(buf, s);
                case CompoundTag t -> buf.writeNbt(t);
                case Enum<?> e -> buf.writeInt(e.ordinal());
                default -> throw new IllegalArgumentException("Args contains invalid type: " + o.getClass().getName());
            }
        }
    }

}
