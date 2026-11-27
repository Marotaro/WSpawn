package ch.wondercraft.wonderspawn.commands;

import ch.wondercraft.wonderspawn.Reloadable;
import ch.wondercraft.wonderspawn.events.JoinEvent;
import ch.wondercraft.wonderspawn.events.RespawnEvent;
import ch.wondercraft.wonderspawn.events.VoidEvent;
import ch.wondercraft.wonderspawn.teleportation.TeleportationCooldown;
import ch.wondercraft.wonderspawn.teleportation.TeleportationQueue;
import ch.wondercraft.wonderspawn.teleportation.TeleportationService;
import ch.wondercraft.wonderspawn.teleportation.TeleportationSound;
import ch.wondercraft.wonderspawn.text.ComponentCreator;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class Reload {
    public static LiteralArgumentBuilder<CommandSourceStack> create(Plugin plugin,ComponentCreator componentCreator,
                                                                    Reloadable... reloadables){
        return Commands.literal("reload").requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("wspawn.reload"))
                .executes(context -> {
                    CommandSender commandSender = context.getSource().getSender();
                    commandSender.sendMessage(componentCreator.reloadPromise(commandSender));
                    try {
                        plugin.reloadConfig();
                        for (Reloadable reloadable:reloadables){
                            reloadable.reload(plugin);
                        }
                        commandSender.sendMessage(componentCreator.reloadConfirmation(commandSender));
                    } catch (Exception e) {
                        commandSender.sendMessage(componentCreator.reloadFailed(commandSender, e));
                    }
                    return Command.SINGLE_SUCCESS;
                });
    }
}
