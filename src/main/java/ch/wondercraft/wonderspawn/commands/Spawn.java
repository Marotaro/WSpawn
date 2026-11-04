package ch.wondercraft.wonderspawn.commands;

import ch.wondercraft.wonderspawn.teleportation.TeleportationCooldown;
import ch.wondercraft.wonderspawn.teleportation.TeleportationOutCome;
import ch.wondercraft.wonderspawn.teleportation.TeleportationService;
import ch.wondercraft.wonderspawn.text.Text;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Spawn {

    public static LiteralArgumentBuilder<CommandSourceStack> create(Text text, TeleportationService teleportationService, TeleportationCooldown teleportationCooldown){
        return Commands.literal("spawn").requires(commandSourceStack ->commandSourceStack.getSender().hasPermission("wspawn.spawn"))
                .executes(context -> {
                    CommandSender sender = context.getSource().getSender();
                    if (sender instanceof Player player){
                        if (teleportationCooldown.canRequest(player)) {
                            teleportationCooldown.add(player);
                            teleportationService.request(player, teleportationService.getApplicableDelay(player), teleportationService.getPlayerCanMove(player)).thenAccept(
                                    teleportationOutCome -> {
                                        if (teleportationOutCome == TeleportationOutCome.SUCCESS)
                                            text.send(player, text.spawnConfirmation());
                                        else if (teleportationOutCome == TeleportationOutCome.MOVED) {
                                            text.send(player, text.spawnPlayerMoved());
                                        } else if (teleportationOutCome == TeleportationOutCome.ERROR) {
                                            text.send(player, text.spawnUnknownError());
                                        }
                                    }
                            );
                        } else {
                            text.send(player,text.cooldownDenied(teleportationCooldown.getRemainingTicks(player)/20));
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
                                                sender.sendMessage(text.forcedSpawnTryMoreThanOne());
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            Player target = res.getFirst();
                                            if (sender instanceof Player playerSender){
                                                if (playerSender.equals(target)){
                                                    if (teleportationCooldown.canRequest(playerSender))
                                                        teleportationService.request(playerSender, teleportationService.getApplicableDelay(playerSender), teleportationService.getPlayerCanMove(playerSender)).thenAccept(
                                                                teleportationOutCome -> {
                                                                    if (teleportationOutCome== TeleportationOutCome.SUCCESS)
                                                                        text.send(playerSender,text.spawnConfirmation());
                                                                    else if (teleportationOutCome==TeleportationOutCome.MOVED) {
                                                                        text.send(playerSender,text.spawnPlayerMoved());
                                                                    } else if (teleportationOutCome==TeleportationOutCome.ERROR) {
                                                                        text.send(playerSender,text.spawnUnknownError());
                                                                    }
                                                                }
                                                        );
                                                    else
                                                        text.send(playerSender,text.cooldownDenied(teleportationCooldown.getRemainingTicks(playerSender)/20));
                                                } else {
                                                    teleportationService.request(target).thenAccept(teleportationOutCome -> {
                                                        if (teleportationOutCome==TeleportationOutCome.SUCCESS){
                                                            text.send(playerSender,text.forcedSpawnConfirmation(target));
                                                            text.send(target,text.forcedSpawnNotice(playerSender));
                                                        } else {
                                                            text.send(playerSender,text.spawnUnknownError());
                                                            text.send(target,text.spawnUnknownError());
                                                        }
                                                    });
                                                }
                                            } else {
                                                teleportationService.request(target).thenAccept(teleportationOutCome -> {
                                                    if (teleportationOutCome==TeleportationOutCome.SUCCESS){
                                                        text.send(target,text.forcedSpawnNotice(sender));
                                                        sender.sendMessage(text.forcedSpawnConfirmation(target));
                                                    } else {
                                                        text.send(target,text.spawnUnknownError());
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
