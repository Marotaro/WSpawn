package ch.wondercraft.wonderspawn.commands;

import ch.wondercraft.wonderspawn.teleportation.TeleportationOutCome;
import ch.wondercraft.wonderspawn.teleportation.TeleportationService;
import ch.wondercraft.wonderspawn.text.Text;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Spawn {

    public static LiteralArgumentBuilder<CommandSourceStack> create(Text text, TeleportationService teleportationService){
        return Commands.literal("spawn").requires(commandSourceStack ->commandSourceStack.getSender().hasPermission("wspawn.spawn"))
                .executes(context -> {
                    CommandSender sender = context.getSource().getSender();
                    if (sender instanceof Player player){
                        teleportationService.request(player, teleportationService.getApplicableDelay(player), teleportationService.getPlayerCanMove(player)).thenAccept(
                                teleportationOutCome -> {
                                    if (teleportationOutCome== TeleportationOutCome.SUCCESS)
                                        player.sendMessage(text.spawnConfirmation());
                                    else if (teleportationOutCome==TeleportationOutCome.MOVED) {
                                        player.sendMessage(text.spawnPlayerMoved());
                                    } else if (teleportationOutCome==TeleportationOutCome.ERROR) {
                                        player.sendMessage(text.spawnUnknownError());
                                    }
                                }
                        );
                        return Command.SINGLE_SUCCESS;
                    }
                    sender.sendMessage("need to be a player");
                    return Command.SINGLE_SUCCESS;
                }).then(
                        Commands.argument("player", ArgumentTypes.player()).requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("wspawn.spawn.other"))
                                .executes(
                                        context -> {
                                            Player target = context.getArgument("player", Player.class);
                                            CommandSender sender = context.getSource().getSender();
                                            if (sender instanceof Player playerSender){
                                                if (playerSender.equals(target)){
                                                    teleportationService.request(playerSender, teleportationService.getApplicableDelay(playerSender), teleportationService.getPlayerCanMove(playerSender)).thenAccept(
                                                            teleportationOutCome -> {
                                                                if (teleportationOutCome== TeleportationOutCome.SUCCESS)
                                                                    playerSender.sendMessage(text.spawnConfirmation());
                                                                else if (teleportationOutCome==TeleportationOutCome.MOVED) {
                                                                    playerSender.sendMessage(text.spawnPlayerMoved());
                                                                } else if (teleportationOutCome==TeleportationOutCome.ERROR) {
                                                                    playerSender.sendMessage(text.spawnUnknownError());
                                                                }
                                                            }
                                                    );
                                                } else {
                                                    teleportationService.request(target).thenAccept(teleportationOutCome -> {
                                                        if (teleportationOutCome==TeleportationOutCome.SUCCESS){
                                                            playerSender.sendMessage(text.forcedSpawnConfirmation(target));
                                                            target.sendMessage(text.forcedSpawnNotice(sender));
                                                        } else {
                                                            playerSender.sendMessage(text.spawnUnknownError());
                                                            target.sendMessage(text.spawnUnknownError());
                                                        }
                                                    });
                                                }
                                            } else {
                                                teleportationService.request(target).thenAccept(teleportationOutCome -> {
                                                    if (teleportationOutCome==TeleportationOutCome.SUCCESS){
                                                        target.sendMessage(text.forcedSpawnNotice(sender));
                                                        sender.sendMessage(text.forcedSpawnConfirmation(target));
                                                    } else {
                                                        target.sendMessage(text.spawnUnknownError());
                                                        sender.sendMessage(text.spawnUnknownError());
                                                    }
                                                });
                                            }
                                            return Command.SINGLE_SUCCESS;
                                        }
                                )
                );
    }
}
