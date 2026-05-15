package com.glodblock.github.glodium.network.packet;

import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.glodium.network.packet.sync.ParaSerializer;
import com.glodblock.github.glodium.network.packet.sync.Paras;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public abstract class SGenericPacket implements IMessage {

    private String name;
    private byte[] payload;
    private Object[] paras;

    public SGenericPacket() {
        // NO-OP
    }

    public SGenericPacket(String name) {
        this.name = name;
        this.paras = null;
    }

    public SGenericPacket(String name, Object... paras) {
        this.name = name;
        this.paras = paras;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(this.name);
        if (this.paras == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            ParaSerializer.to(this.paras, buf);
        }
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        this.name = buf.readUtf();
        if (buf.readBoolean()) {
            this.payload = new byte[buf.readableBytes()];
            buf.readBytes(this.payload);
            buf.clear();
        } else {
            this.payload = new byte[0];
        }
    }

    @Override
    public void onMessage(IPayloadContext ctx) {
        if (Minecraft.getInstance().screen instanceof IActionHolder ah) {
            var fun = ah.getActionMap().get(this.name);
            if (fun != null) {
                fun.accept(new Paras(this.payload, ctx.player()));
            }
        }
    }

    @Override
    public boolean isClient() {
        return true;
    }

}