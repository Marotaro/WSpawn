package ch.wondercraft.wonderspawn.commands;

import ch.wondercraft.wonderspawn.ConfigManager;
import ch.wondercraft.wonderspawn.events.JoinEvent;
import ch.wondercraft.wonderspawn.events.RespawnEvent;
import ch.wondercraft.wonderspawn.teleportation.TeleportationQueue;
import ch.wondercraft.wonderspawn.teleportation.TeleportationService;
import ch.wondercraft.wonderspawn.teleportation.TeleportationSound;
import ch.wondercraft.wonderspawn.text.Text;
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
                                                                    JoinEvent joinEvent,
                                                                    RespawnEvent respawnEvent){
        return Commands.literal("reload").requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("wspawn.reload"))
                .executes(context -> {
                    CommandSender commandSender = context.getSource().getSender();
                    commandSender.sendMessage(configManager.text().reloadPromise());
                    try {
                        plugin.reloadConfig();
                        configManager.reload();
                        teleportationSound.reload();
                        teleportationService.reload();
                        teleportationQueue.reload();
                        joinEvent.reload();
                        respawnEvent.reload();
                        commandSender.sendMessage(configManager.text().reloadConfirmation());
                    } catch (ConfigManager.ConfigException e) {
                        commandSender.sendMessage(configManager.text().reloadFailed(e));
                    }
                    return Command.SINGLE_SUCCESS;
                });
    }
}
