package ch.wondercraft.wonderspawn.commands;

import ch.wondercraft.wonderspawn.LocationFiler;
import ch.wondercraft.wonderspawn.message.MessagesHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.RotationResolver;
import io.papermc.paper.math.FinePosition;
import io.papermc.paper.math.Rotation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

public class SetSpawn {
    public static LiteralArgumentBuilder<CommandSourceStack> create(LocationFiler locationFiler, MessagesHandler messagesHandler){
        return Commands.literal("setspawn").requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("wspawn.set"))
                .executes(context -> {
                    CommandSourceStack source = context.getSource();
                    CommandSender sender = source.getSender();
                    Entity executor = source.getExecutor();
                    if (executor == null){
                        //todo change to custom message with message file
                        sender.sendMessage("need an executor");
                        return Command.SINGLE_SUCCESS;
                    }
                    locationFiler.setSpawnPoint(executor.getLocation());
                    sender.sendMessage(messagesHandler.setSpawn(executor.getLocation()));
                    return Command.SINGLE_SUCCESS;
                })
                .then(
                        Commands.argument("worldName", ArgumentTypes.world()).then(
                                Commands.argument("position",ArgumentTypes.finePosition()).then(
                                        Commands.argument("rotation",ArgumentTypes.rotation()).executes(context -> {
                                            CommandSourceStack source = context.getSource();
                                            CommandSender sender = source.getSender();
                                            Entity executor = source.getExecutor();
                                            World world = context.getArgument("worldName", World.class);
                                            FinePositionResolver finePositionResolver = context.getArgument("position", FinePositionResolver.class);
                                            FinePosition finePosition = finePositionResolver.resolve(source);
                                            Location location = finePosition.toLocation(world);
                                            RotationResolver rotationResolver = context.getArgument("rotation", RotationResolver.class);
                                            location.setRotation(rotationResolver.resolve(source));
                                            locationFiler.setSpawnPoint(location);
                                            sender.sendMessage(messagesHandler.setSpawn(location));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                )
                ;
    }
}
