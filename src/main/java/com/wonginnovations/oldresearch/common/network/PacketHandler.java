package com.wonginnovations.oldresearch.common.network;

import com.wonginnovations.oldresearch.main.OldResearch;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

    public static void preInit() {
        int discriminator = 0;
        OldResearch.NETWORK.registerMessage(PacketAspectCombinationToServer.class, PacketAspectCombinationToServer.class, discriminator++, Side.SERVER);
        OldResearch.NETWORK.registerMessage(PacketAspectDiscovery.class, PacketAspectDiscovery.class, discriminator++, Side.CLIENT);
        OldResearch.NETWORK.registerMessage(PacketAspectDiscoveryError.class, PacketAspectDiscoveryError.class, discriminator++, Side.CLIENT);
        OldResearch.NETWORK.registerMessage(PacketAspectPlaceToServer.class, PacketAspectPlaceToServer.class, discriminator++, Side.SERVER);
        OldResearch.NETWORK.registerMessage(PacketAspectPool.class, PacketAspectPool.class, discriminator++, Side.CLIENT);
        OldResearch.NETWORK.registerMessage(PacketGivePlayerNoteToServer.class, PacketGivePlayerNoteToServer.class, discriminator++, Side.SERVER);
        OldResearch.NETWORK.registerMessage(PacketCopyPlayerNoteToServer.class, PacketCopyPlayerNoteToServer.class, discriminator++, Side.SERVER);
        OldResearch.NETWORK.registerMessage(PacketSyncAspects.class, PacketSyncAspects.class, discriminator++, Side.CLIENT);
        OldResearch.NETWORK.registerMessage(PacketSyncResearchTableAspects.class, PacketSyncResearchTableAspects.class, discriminator++, Side.CLIENT);
        OldResearch.NETWORK.registerMessage(PacketScanSelfToServer.class, PacketScanSelfToServer.class, discriminator++, Side.SERVER);
        OldResearch.NETWORK.registerMessage(PacketScanSlotToServer.class, PacketScanSlotToServer.class, discriminator++, Side.SERVER);
    }
}
