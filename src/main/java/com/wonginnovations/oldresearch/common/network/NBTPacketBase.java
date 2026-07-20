package com.wonginnovations.oldresearch.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class NBTPacketBase implements IMessage {
    protected NBTTagCompound nbt = null;

    public NBTPacketBase() {}

    public NBTPacketBase(NBTTagCompound nbt) {
        this.nbt = nbt;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if (buf.readBoolean()) this.nbt = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.nbt != null);
        if (this.nbt != null) ByteBufUtils.writeTag(buf, this.nbt);
    }
}
