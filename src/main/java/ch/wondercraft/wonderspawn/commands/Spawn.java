package ch.wondercraft.wonderspawn.commands;

import ch.wondercraft.wonderspawn.teleportation.TeleportationCooldown;
import ch.wondercraft.wonderspawn.teleportation.TeleportationOutCome;
import ch.wondercraft.wonderspawn.teleportation.TeleportationService;
import ch.wondercraft.wonderspawn.text.ComponentCreator;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Spawn {

    public static LiteralArgumentBuilder<CommandSourceStack> create(ComponentCreator componentCreator, TeleportationService teleportationService, TeleportationCooldown teleportationCooldown){
        return Commands.literal("spawn").requires(commandSourceStack ->commandSourceStack.getSender().hasPermission("wspawn.spawn"))
                .executes(context -> {
                    CommandSender sender = context.getSource().getSender();
                    if (sender instanceof Player player){
                        if (teleportationCooldown.canRequest(player)) {
                            teleportationCooldown.add(player);
                            teleportationService.request(player, teleportationService.getApplicableDelay(player), teleportationService.getPlayerCanMove(player)).thenAccept(
                                    teleportationOutCome -> {
                                        if (teleportationOutCome == TeleportationOutCome.SUCCESS)
                                            componentCreator.send(player, componentCreator.spawnConfirmation(player));
                                        else if (teleportationOutCome == TeleportationOutCome.MOVED) {
                                            componentCreator.send(player, componentCreator.spawnPlayerMoved(player));
                                        } else if (teleportationOutCome == TeleportationOutCome.ERROR) {
                                            componentCreator.send(player, componentCreator.spawnUnknownError(player));
                                        }
                                    }
                            );
                        } else {
                            componentCreator.send(player,componentCreator.cooldownDenied(player,teleportationCooldown.getRemainingTicks(player)/20));
                        }
                        return Command.SINGLE_SUCCESS;
                    }
                    sender.sendMessage("need to be a player");
                    return Command.SINGLE_SUCCESS;
                }).then(
                        Commands.argument("player", ArgumentTypes.player()).requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("wspawn.spawn.other"))
                                .executes(
                                        context -> {
                                            CommandSender sender = context.getSource().getSender();
                                            PlayerSelectorArgumentResolver resolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
                                            var res = resolver.resolve(context.getSource());
                                            if (res.size()!=1){
                                                sender.sendMessage(componentCreator.forcedSpawnTryMoreThanOne(sender));
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            Player target = res.getFirst();
                                            if (sender instanceof Player playerSender){
                                                if (playerSender.equals(target)){
                                                    if (teleportationCooldown.canRequest(playerSender))
                                                        teleportationService.request(playerSender, teleportationService.getApplicableDelay(playerSender), teleportationService.getPlayerCanMove(playerSender)).thenAccept(
                                                                teleportationOutCome -> {
                                                                    if (teleportationOutCome== TeleportationOutCome.SUCCESS)
                                                                        componentCreator.send(playerSender,componentCreator.spawnConfirmation(playerSender));
                                                                    else if (teleportationOutCome==TeleportationOutCome.MOVED) {
                                                                        componentCreator.send(playerSender,componentCreator.spawnPlayerMoved(playerSender));
                                                                    } else if (teleportationOutCome==TeleportationOutCome.ERROR) {
                                                                        componentCreator.send(playerSender,componentCreator.spawnUnknownError(playerSender));
                                                                    }
                                                                }
                                                        );
                                                    else
                                                        componentCreator.send(playerSender,componentCreator.cooldownDenied(playerSender,teleportationCooldown.getRemainingTicks(playerSender)/20));
                                                } else {
                                                    teleportationService.request(target).thenAccept(teleportationOutCome -> {
                                                        if (teleportationOutCome==TeleportationOutCome.SUCCESS){
                                                            componentCreator.send(playerSender,componentCreator.forcedSpawnConfirmation(playerSender,target));
                                                            componentCreator.send(target,componentCreator.forcedSpawnNotice(target,playerSender));
                                                        } else {
                                                            componentCreator.send(playerSender,componentCreator.spawnUnknownError(playerSender));
                                                            componentCreator.send(target,componentCreator.spawnUnknownError(target));
                                                        }
                                                    });
                                                }
                                            } else {
                                                teleportationService.request(target).thenAccept(teleportationOutCome -> {
                                                    if (teleportationOutCome==TeleportationOutCome.SUCCESS){
                                                        componentCreator.send(target,componentCreator.forcedSpawnNotice(sender, target));
                                                        sender.sendMessage(componentCreator.forcedSpawnConfirmation(sender,target));
                                                    } else {
                                                        componentCreator.send(target,componentCreator.spawnUnknownError(target));
                                                        sender.sendMessage(componentCreator.spawnUnknownError(sender));
                                                    }
                                                });
                                            }
                                            return Command.SINGLE_SUCCESS;
                                        }
                                )
                );
    }
}
