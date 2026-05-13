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
            switch (o) {
                case null -> {
                    buf.writeByte(PT.VOID.ordinal());
                }
                case int i -> {
                    buf.writeByte(PT.INT.ordinal());
                    buf.writeVarInt(i);
                }
                case long l -> {
                    buf.writeByte(PT.LONG.ordinal());
                    buf.writeVarLong(l);
                }
                case short s -> {
                    buf.writeByte(PT.SHORT.ordinal());
                    buf.writeShort(s);
                }
                case byte b -> {
                    buf.writeByte(PT.BYTE.ordinal());
                    buf.writeByte(b);
                }
                case boolean b -> {
                    buf.writeByte(PT.BOOLEAN.ordinal());
                    buf.writeBoolean(b);
                }
                case String s -> {
                    buf.writeByte(PT.STRING.ordinal());
                    buf.writeUtf(s, 1024);
                }
                case ItemStack s -> {
                    buf.writeByte(PT.ITEM_STACK.ordinal());
                    ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, s);
                }
                case FluidStack s -> {
                    buf.writeByte(PT.FLUID_STACK.ordinal());
                    FluidStack.OPTIONAL_STREAM_CODEC.encode(buf, s);
                }
                case CompoundTag t -> {
                    buf.writeByte(PT.NBT.ordinal());
                    buf.writeNbt(t);
                }
                case Enum<?> e -> {
                    buf.writeByte(PT.ENUM.ordinal());
                    buf.writeInt(e.ordinal());
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
                case BYTE -> objs[i] = buf.readByte();
                case BOOLEAN -> objs[i] = buf.readBoolean();
                case STRING -> objs[i] = buf.readUtf(1024);
                case ITEM_STACK -> objs[i] = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
                case FLUID_STACK -> objs[i] = FluidStack.OPTIONAL_STREAM_CODEC.decode(buf);
                case NBT -> objs[i] = buf.readNbt();
                case ENUM -> objs[i] = buf.readInt();
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
        BYTE,
        BOOLEAN,
        STRING,
        ITEM_STACK,
        FLUID_STACK,
        NBT,
        ENUM
    }

}
