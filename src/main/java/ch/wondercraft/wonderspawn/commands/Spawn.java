package ch.wondercraft.wonderspawn.commands;

import ch.wondercraft.wonderspawn.LocationFiler;
import ch.wondercraft.wonderspawn.message.Messages;
import ch.wondercraft.wonderspawn.message.MessagesHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Spawn {
    public static LiteralArgumentBuilder<CommandSourceStack> create(LocationFiler locationFiler, MessagesHandler messagesHandler){
        return Commands.literal("spawn").requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("wspawn.spawn")).executes(
                commandContext -> {
                    CommandSourceStack source = commandContext.getSource();
                    CommandSender sender = source.getSender();
                    Entity executor = source.getExecutor();
                    if (executor == null){
                        sender.sendMessage(Messages.errorExecutorNull(messagesHandler));
                        return Command.SINGLE_SUCCESS;
                    }
                    if (!(executor instanceof Player)){
                        sender.sendMessage(Messages.errorExecutorNotAPlayer(messagesHandler));
                        return Command.SINGLE_SUCCESS;
                    }
                    if (sender instanceof Player player){
                        if (player.getUniqueId().equals(executor.getUniqueId())){
                            player.teleportAsync(locationFiler.getSpawnPoint()).thenAccept( b -> {
                                player.sendMessage((b)?Messages.self(messagesHandler): Messages.errorTeleportation(messagesHandler));
                            });
                        } else {
                            executor.teleportAsync(locationFiler.getSpawnPoint()).thenAccept(b->{
                                if (b){
                                    player.sendMessage(Messages.otherCommanderSingle(messagesHandler,(Player) executor));
                                    executor.sendMessage(Messages.otherTargetByPlayer(messagesHandler,player));
                                } else
                                    player.sendMessage(Messages.errorTeleportation(messagesHandler));
                            });
                        }
                    } else {
                        executor.teleportAsync(locationFiler.getSpawnPoint()).thenAccept(b->{
                            if (b){
                                sender.sendMessage(Messages.otherCommanderSingle(messagesHandler,(Player) executor));
                                executor.sendMessage(Messages.otherTargetByConsole(messagesHandler));
                            } else
                                sender.sendMessage(Messages.errorTeleportation(messagesHandler));
                        });
                    }
                    return Command.SINGLE_SUCCESS;
                }
        ).then(Commands.argument("player", ArgumentTypes.player()).requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("wspawn.spawn.other")).executes(
                commandContext -> {
                    CommandSourceStack source = commandContext.getSource();
                    CommandSender sender = source.getSender();
                    PlayerSelectorArgumentResolver targetResolver = commandContext.getArgument("player", PlayerSelectorArgumentResolver.class);
                    Player target = targetResolver.resolve(source).getFirst();
                    if (sender instanceof Player player){
                        if (player.getUniqueId().equals(target.getUniqueId())){
                            player.teleportAsync(locationFiler.getSpawnPoint()).thenAccept(b->{
                                player.sendMessage((b)?Messages.self(messagesHandler):Messages.errorTeleportation(messagesHandler));
                            });
                        } else {
                            target.teleportAsync(locationFiler.getSpawnPoint()).thenAccept(b->{
                                if (b){
                                    player.sendMessage(Messages.otherCommanderSingle(messagesHandler,target));
                                    target.sendMessage(Messages.otherTargetByPlayer(messagesHandler,player));
                                } else
                                    player.sendMessage(Messages.errorTeleportation(messagesHandler));
                            });
                        }
                    } else {

                        target.teleportAsync(locationFiler.getSpawnPoint()).thenAccept(b->{
                            if (b){
                                sender.sendMessage(Messages.otherCommanderSingle(messagesHandler,target));
                                target.sendMessage(Messages.otherTargetByConsole(messagesHandler));
                            } else
                                sender.sendMessage(Messages.errorTeleportation(messagesHandler));
                        });
                    }
                    return Command.SINGLE_SUCCESS;
                }
                )
        ).then(
                Commands.literal("all").requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("wspawn.spawn.all")).executes(
                        context -> {
                            CommandSourceStack source = context.getSource();
                            CommandSender sender = source.getSender();
                            locationFiler.getSpawnPoint().getWorld().getChunkAtAsync(locationFiler.getSpawnPoint()).thenAccept(chunk -> {
                                sender.getServer().getOnlinePlayers().forEach(player -> {
                                    player.sendMessage((sender instanceof Player)? Messages.otherTargetByPlayer(messagesHandler, (Player) sender) : Messages.otherTargetByConsole(messagesHandler));
                                    player.teleport(locationFiler.getSpawnPoint());
                                });
                                sender.sendMessage(Messages.otherCommanderAll(messagesHandler,sender.getServer().getOnlinePlayers().size()));
                            });
                            return Command.SINGLE_SUCCESS;
                        }
                )
        );
    }
}
