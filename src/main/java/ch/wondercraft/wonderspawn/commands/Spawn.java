package ch.wondercraft.wonderspawn.commands;

import ch.wondercraft.wonderspawn.LocationFiler;
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
    public static LiteralArgumentBuilder<CommandSourceStack> create(LocationFiler locationFiler){
        return Commands.literal("spawn").requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("wspawn.spawn")).executes(
                commandContext -> {
                    CommandSourceStack source = commandContext.getSource();
                    CommandSender sender = source.getSender();
                    Entity executor = source.getExecutor();
                    if (executor == null){
                        //todo change to custom message with message file
                        sender.sendMessage("cannot use spawn without an executor");
                        return Command.SINGLE_SUCCESS;
                    }
                    if (!(executor instanceof Player)){
                        //todo change to custom message with message file
                        sender.sendMessage("executor needs to be a player");
                        return Command.SINGLE_SUCCESS;
                    }
                    if (sender instanceof Player player){
                        if (player.getUniqueId().equals(executor.getUniqueId())){
                            //todo change to custom message with message file
                            player.sendMessage("you teleported to spawn");
                            player.teleport(locationFiler.getSpawnPoint());
                        } else {
                            //todo change to custom message with message file
                            player.sendMessage("you teleported "+executor.getName()+" to spawn");
                            executor.sendMessage("you have been teleported to spawn by "+player.getName());
                            executor.teleport(locationFiler.getSpawnPoint());
                        }
                    } else {
                        sender.sendMessage("you teleported "+executor.getName()+" to spawn");
                        executor.sendMessage("you have been teleported to spawn by console");
                        executor.teleport(locationFiler.getSpawnPoint());
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
                            //todo change to custom message with message file
                            player.sendMessage("you teleported to spawn");
                            player.teleport(locationFiler.getSpawnPoint());
                        } else {
                            //todo change to custom message with message file
                            player.sendMessage("you teleported "+target.getName()+" to spawn");
                            target.sendMessage("you have been teleported to spawn by "+player.getName());
                            target.teleport(locationFiler.getSpawnPoint());
                        }
                    } else {
                        sender.sendMessage("you teleported "+target.getName()+" to spawn");
                        target.sendMessage("you have been teleported to spawn by console");
                        target.teleport(locationFiler.getSpawnPoint());
                    }
                    return Command.SINGLE_SUCCESS;
                }
                )
        ).then(
                Commands.literal("all").requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("wspawn.spawn.all")).executes(
                        context -> {
                            CommandSourceStack source = context.getSource();
                            CommandSender sender = source.getSender();
                            sender.getServer().getOnlinePlayers().forEach(player -> {
                                player.sendMessage((sender instanceof Player)? "you have been teleported to spawn by "+sender.getName() : "you have been teleported to spawn by console");
                                player.teleport(locationFiler.getSpawnPoint());
                            });
                            sender.sendMessage("you teleported "+sender.getServer().getOnlinePlayers().size()+" to spawn");
                            return Command.SINGLE_SUCCESS;
                        }
                )
        );
    }
}
