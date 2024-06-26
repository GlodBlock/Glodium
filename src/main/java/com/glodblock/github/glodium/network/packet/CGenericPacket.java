package com.glodblock.github.glodium.network.packet;

import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.glodium.network.packet.sync.ParaSerializer;
import com.glodblock.github.glodium.network.packet.sync.Paras;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public abstract class CGenericPacket implements IMessage {

    private String name;
    private Object[] paras;

    public CGenericPacket() {
        // NO-OP
    }

    public CGenericPacket(String name) {
        this.name = name;
        this.paras = null;
    }

    public CGenericPacket(String name, Object... paras) {
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
            this.paras = ParaSerializer.from(buf);
        } else {
            this.paras = null;
        }
    }

    @Override
    public void onMessage(IPayloadContext ctx) {
        if (ctx.player().containerMenu instanceof IActionHolder ah) {
            var fun = ah.getActionMap().get(this.name);
            if (fun != null) {
                fun.accept(new Paras(this.paras));
            }
        }
    }

    @Override
    public boolean isClient() {
        return false;
    }

}
