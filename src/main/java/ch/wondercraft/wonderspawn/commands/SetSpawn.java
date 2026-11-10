package ch.wondercraft.wonderspawn.commands;

import ch.wondercraft.wonderspawn.SpawnLocation;
import ch.wondercraft.wonderspawn.text.ComponentCreator;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.RotationResolver;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawn {
    public static LiteralArgumentBuilder<CommandSourceStack> create(SpawnLocation spawnLocation, ComponentCreator componentCreator){
        return Commands.literal("setspawn").requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("wspawn.setspawn"))
                .executes(context -> {
                    CommandSender commandSender = context.getSource().getSender();
                    if (commandSender instanceof Player player){
                        Location oldLocation = spawnLocation.clone();
                        spawnLocation.update(player.getLocation());
                        player.sendMessage(componentCreator.setSpawnConfirmation(player,oldLocation));
                    } else {
                        commandSender.sendMessage(componentCreator.setSpawnNeedsVerboseVersion(commandSender));
                    }
                    return Command.SINGLE_SUCCESS;
                }).then(
                        Commands.argument("world", ArgumentTypes.world()).then(
                                Commands.argument("position",ArgumentTypes.finePosition()).then(
                                        Commands.argument("rotation",ArgumentTypes.rotation()).executes(context -> {
                                            CommandSender commandSender = context.getSource().getSender();
                                            FinePositionResolver positionResolver = context.getArgument("position", FinePositionResolver.class);
                                            var finePosition = positionResolver.resolve(context.getSource());
                                            RotationResolver rotationResolver = context.getArgument("rotation", RotationResolver.class);
                                            var rotation = rotationResolver.resolve(context.getSource());
                                            Location newLocation = new Location(
                                                    context.getArgument("world", World.class),
                                                    finePosition.x(),
                                                    finePosition.y(),
                                                    finePosition.z(),
                                                    rotation.yaw(),
                                                    rotation.pitch()
                                                    );
                                            Location oldLocation = spawnLocation.clone();
                                            spawnLocation.update(newLocation);
                                            commandSender.sendMessage(componentCreator.setSpawnConfirmation(commandSender, oldLocation));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                );
    }
}
