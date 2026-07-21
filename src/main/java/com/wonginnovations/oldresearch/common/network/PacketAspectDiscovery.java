package com.wonginnovations.oldresearch.common.network;

import com.wonginnovations.oldresearch.api.OldResearchApi;
import com.wonginnovations.oldresearch.main.OldResearch;
import com.wonginnovations.oldresearch.tc4legacy.client.PlayerNotifications;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;

public class PacketAspectDiscovery implements IMessage, IMessageHandler<PacketAspectDiscovery, IMessage> {
    private String key;

    public PacketAspectDiscovery() {}

    public PacketAspectDiscovery(String key) {
        this.key = key;
    }

    public void toBytes(ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, this.key);
    }

    public void fromBytes(ByteBuf buffer) {
        this.key = ByteBufUtils.readUTF8String(buffer);
    }

    @SideOnly(Side.CLIENT)
    public IMessage onMessage(PacketAspectDiscovery message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if(Aspect.getAspect(message.key) != null) {
                OldResearchApi.oldResStorage(Minecraft.getMinecraft().player).researchAspect(Aspect.getAspect(message.key));
                String text = I18n.format("tc.addaspectdiscovery", Aspect.getAspect(message.key).getName());
                PlayerNotifications.addNotification(TextFormatting.GOLD + text, Aspect.getAspect(message.key));
                Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("entity.experience_orb.pickup")), 0.2F, 0.5F + Minecraft.getMinecraft().world.rand.nextFloat() * 0.2F);
            }
        });
        return null;
    }
}
