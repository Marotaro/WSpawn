package ch.wondercraft.wonderspawn.commands;

import ch.wondercraft.wonderspawn.teleportation.TeleportationOutCome;
import ch.wondercraft.wonderspawn.teleportation.TeleportationService;
import ch.wondercraft.wonderspawn.text.Text;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class Spawn {

    public static LiteralArgumentBuilder<CommandSourceStack> create(Text text, TeleportationService teleportationService){
        return Commands.literal("spawn").requires(commandSourceStack ->commandSourceStack.getSender().hasPermission("wspawn.spawn"))
                .executes(context -> {
                    CommandSender sender = context.getSource().getSender();
                    if (sender instanceof Player player){
                        teleportationService.requestTeleportation(player,100,true).thenAccept(
                                teleportationOutCome -> {
                                    if (Objects.requireNonNull(teleportationOutCome) == TeleportationOutCome.SUCCESS) {
                                        player.sendMessage(text.spawnConfirmation());
                                    }
                                }
                        );
                        return Command.SINGLE_SUCCESS;
                    }
                    sender.sendMessage("need to be a player");
                    return Command.SINGLE_SUCCESS;
                })
                .then(
                        Commands.argument("player", ArgumentTypes.player()).requires(
                                commandSourceStack -> commandSourceStack.getSender().hasPermission("wspawn.spawn.other")
                        ).executes(
                                context -> {
                                    CommandSender sender = context.getSource().getSender();
                                    PlayerSelectorArgumentResolver resolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
                                    var res = resolver.resolve(context.getSource());
                                    if (res.size()!=1){
                                        sender.sendMessage(text.forcedSpawnTryMoreThanOne());
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    Player player = res.getFirst();
                                    if (sender.equals(player))
                                        teleportationService.requestTeleportation(player,100,true).thenAccept( teleportationOutCome -> {
                                                if (Objects.requireNonNull(teleportationOutCome) == TeleportationOutCome.SUCCESS)
                                                    player.sendMessage(text.spawnConfirmation());
                                                }
                                        );
                                    else
                                        teleportationService.requestTeleportation(player,0,true).thenAccept(teleportationOutCome -> {
                                            if (Objects.requireNonNull(teleportationOutCome)== TeleportationOutCome.SUCCESS){
                                                player.sendMessage(text.forcedSpawnNotice(sender));
                                                sender.sendMessage(text.forcedSpawnConfirmation(player));
                                            }
                                        });
                                    return Command.SINGLE_SUCCESS;
                                }
                        )
                );
    }
}
