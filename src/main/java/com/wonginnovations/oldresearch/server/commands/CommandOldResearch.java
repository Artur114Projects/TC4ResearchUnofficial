package com.wonginnovations.oldresearch.server.commands;

import com.wonginnovations.oldresearch.api.OldResearchApi;
import com.wonginnovations.oldresearch.common.research.storage.IOldResStorage;
import com.wonginnovations.oldresearch.main.OldResearch;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandOldResearch extends CommandBase {
    @Override
    public @NotNull String getName() {
        return OldResearch.MODID;
    }

    @Override
    public @NotNull String getUsage(@NotNull ICommandSender sender) {
        return "/oldresearch <action> [<player> [<params>]]";
    }

    @Override
    public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, String @NotNull [] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString("§cInvalid arguments"));
            return;
        }

        switch (args[0]) {
            case "researchaspect": {
                if (args.length < 3) {
                    sender.sendMessage(new TextComponentString("§cInvalid arguments"));
                    return;
                }
                EntityPlayerMP player = getPlayer(server, sender, args[1]);

                if (args[2].equals("all")) {
                    IOldResStorage storage = OldResearchApi.oldResStorage(player);
                    int n = 0;
                    for (Aspect aspect : Aspect.aspects.values()) {
                        if (!storage.isKnowAspect(aspect)) {
                            storage.researchAspect(aspect);
                            n++;
                        }
                    }
                    storage.sync();
                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully researched " + n + " aspects"));
                } else {
                    Aspect aspect = Aspect.getAspect(args[2]);
                    if (aspect == null) {
                        sender.sendMessage(new TextComponentString("§cInvalid aspect name"));
                        return;
                    }
                    IOldResStorage storage = OldResearchApi.oldResStorage(player);
                    storage.researchAspect(aspect);
                    storage.sync();
                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully researched aspect " + TextFormatting.RESET + aspect.getChatcolor() + aspect));
                }
                break;
            }
            case "addaspect": {
                if (args.length < 4) {
                    sender.sendMessage(new TextComponentString("§cInvalid arguments"));
                    return;
                }
                EntityPlayerMP player = getPlayer(server, sender, args[1]);

                if (args[2].equals("all")) {
                    IOldResStorage storage = OldResearchApi.oldResStorage(player);
                    for (Aspect aspect : Aspect.aspects.values()) {
                        storage.addToAspectPool(aspect, parseInt(args[3]));
                    }
                    storage.sync();
                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully"));
                } else {
                    Aspect aspect = Aspect.getAspect(args[2]);
                    if (aspect == null) {
                        sender.sendMessage(new TextComponentString("§cInvalid aspect name"));
                        return;
                    }
                    IOldResStorage storage = OldResearchApi.oldResStorage(player);
                    storage.addToAspectPool(aspect, parseInt(args[3]));
                    storage.sync();
                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully added " + parseInt(args[3]) + " aspects " + TextFormatting.RESET + aspect.getChatcolor() + aspect));
                }
                break;
            }
            case "setaspect": {
                if (args.length < 4) {
                    sender.sendMessage(new TextComponentString("§cInvalid arguments"));
                    return;
                }
                EntityPlayerMP player = getPlayer(server, sender, args[1]);

                if (args[2].equals("all")) {
                    IOldResStorage storage = OldResearchApi.oldResStorage(player);
                    for (Aspect aspect : Aspect.aspects.values()) {
                        storage.setAspectCount(aspect, parseInt(args[3]));
                    }
                    storage.sync();
                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully"));
                } else {
                    Aspect aspect = Aspect.getAspect(args[2]);
                    if (aspect == null) {
                        sender.sendMessage(new TextComponentString("§cInvalid aspect name"));
                        return;
                    }
                    IOldResStorage storage = OldResearchApi.oldResStorage(player);
                    storage.setAspectCount(aspect, parseInt(args[3]));
                    storage.sync();
                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully set " + parseInt(args[3]) + " aspect " + TextFormatting.RESET + aspect.getChatcolor() + aspect));
                }
                break;
            }
        }
    }

    public @NotNull List<String> getTabCompletions(@NotNull MinecraftServer server, @NotNull ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        switch (args.length) {
            case 1:
                return getListOfStringsMatchingLastWord(args, "researchaspect", "addaspect", "setaspect");
            case 2:
                return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            case 3:
                List<String> list = new ArrayList<>(Aspect.aspects.keySet()); list.add("all");
                return getListOfStringsMatchingLastWord(args, list.toArray(new String[0]));
            default:
                return Collections.emptyList();
        }
    }
}
