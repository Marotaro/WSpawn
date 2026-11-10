package ch.wondercraft.wonderspawn.commands;

import ch.wondercraft.wonderspawn.ConfigManager;
import ch.wondercraft.wonderspawn.events.JoinEvent;
import ch.wondercraft.wonderspawn.events.RespawnEvent;
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
    public static LiteralArgumentBuilder<CommandSourceStack> create(Plugin plugin,
                                                                    ConfigManager configManager,
                                                                    TeleportationSound teleportationSound,
                                                                    TeleportationService teleportationService,
                                                                    TeleportationQueue  teleportationQueue,
                                                                    TeleportationCooldown teleportationCooldown,
                                                                    ComponentCreator componentCreator,
                                                                    JoinEvent joinEvent,
                                                                    RespawnEvent respawnEvent){
        return Commands.literal("wspawn").then(Commands.literal("reload").requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("wspawn.reload"))
                .executes(context -> {
                    CommandSender commandSender = context.getSource().getSender();
                    commandSender.sendMessage(componentCreator.reloadPromise(commandSender));
                    try {
                        plugin.reloadConfig();
                        configManager.reload();
                        teleportationSound.reload();
                        teleportationService.reload();
                        teleportationCooldown.reload();
                        teleportationQueue.reload();
                        componentCreator.reload();
                        joinEvent.reload();
                        respawnEvent.reload();
                        commandSender.sendMessage(componentCreator.reloadConfirmation(commandSender));
                    } catch (ConfigManager.ConfigException e) {
                        commandSender.sendMessage(componentCreator.reloadFailed(commandSender));
                    }
                    return Command.SINGLE_SUCCESS;
                }));
    }
}
