package com.glodblock.github.glodium.network.packet.sync;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public final class ParaSerializer {

    //////////////////////////////
    //                          //
    //     Serializer Zone      //
    //                          //
    //////////////////////////////

    public static void to(Object[] obj, RegistryFriendlyByteBuf buf) {
        buf.writeByte(obj.length);
        for (var o : obj) {
            switch (o) {
                case null -> {
                    buf.writeByte(PT.VOID.ordinal());
                }
                case Integer i -> {
                    buf.writeByte(PT.INT.ordinal());
                    buf.writeVarInt(i);
                }
                case Long l -> {
                    buf.writeByte(PT.LONG.ordinal());
                    buf.writeVarLong(l);
                }
                case Short s -> {
                    buf.writeByte(PT.SHORT.ordinal());
                    buf.writeShort(s);
                }
                case Boolean b -> {
                    buf.writeByte(PT.BOOLEAN.ordinal());
                    buf.writeBoolean(b);
                }
                case String s -> {
                    buf.writeByte(PT.STRING.ordinal());
                    buf.writeUtf(s, 1024);
                }
                case ItemStack s -> {
                    buf.writeByte(PT.STACK.ordinal());
                    ItemStack.STREAM_CODEC.encode(buf, s);
                }
                case CompoundTag t -> {
                    buf.writeByte(PT.NBT.ordinal());
                    buf.writeNbt(t);
                }
                default -> throw new IllegalArgumentException("Args contains invalid type: " + o.getClass().getName());
            }
        }
    }

    public static Object[] from(RegistryFriendlyByteBuf buf) {
        var objs = new Object[buf.readByte()];
        for (int i = 0; i < objs.length; i ++) {
            switch (PT.values()[buf.readByte()]) {
                case VOID -> objs[i] = null;
                case INT -> objs[i] = buf.readVarInt();
                case LONG -> objs[i] = buf.readVarLong();
                case SHORT -> objs[i] = buf.readShort();
                case BOOLEAN -> objs[i] = buf.readBoolean();
                case STRING -> objs[i] = buf.readUtf(1024);
                case STACK -> objs[i] = ItemStack.STREAM_CODEC.decode(buf);
                case NBT -> objs[i] = buf.readNbt();
                default -> throw new IllegalArgumentException("Args contains unknown type.");
            }
        }
        return objs;
    }

    private enum PT {
        VOID,
        INT,
        LONG,
        SHORT,
        BOOLEAN,
        STRING,
        STACK,
        NBT
    }

}
