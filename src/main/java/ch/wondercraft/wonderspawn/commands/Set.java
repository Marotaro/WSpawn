package ch.wondercraft.wonderspawn.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class Set {

    private static void setter(Location location, FileConfiguration config, CommandSender sender){
        config.set("spawn.world",location.getWorld().getName());
        config.set("spawn.x", location.getX());
        config.set("spawn.y", location.getY());
        config.set("spawn.z", location.getZ());
        config.set("spawn.yaw", location.getYaw());
        config.set("spawn.pitch", location.getPitch());
        sender.sendMessage("New spawn point set at: "+location.toString());
    }

    public static LiteralCommandNode<CommandSourceStack> create(FileConfiguration config){
        return Commands.literal("setspawn")
                .requires(source -> source.getSender().hasPermission("wonderspawn.setspawn"))
                .executes(ctx->{
                    if (ctx.getSource().getSender() instanceof Player player){
                        Location loc = player.getLocation();
                        setter(loc, config, player);
                        return Command.SINGLE_SUCCESS;
                    }
                    ctx.getSource().getSender().sendMessage("Only player can use /setspawn");
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("coords")
                        .requires(source ->source.getSender().hasPermission("wonderspawn.setspawn.coords"))
                        .then(
                                Commands.argument("x",DoubleArgumentType.doubleArg())
                                        .suggests((ctx,builder)->{
                                            if (ctx.getSource().getSender() instanceof Player player){
                                                builder.suggest((int) player.getX(), () -> "Decimal value for x");
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(
                                                Commands.argument("y",DoubleArgumentType.doubleArg())
                                                        .suggests((ctx,builder)->{
                                                            if (ctx.getSource().getSender() instanceof Player player){
                                                                builder.suggest((int) player.getY(), () -> "Decimal value for y");
                                                            }
                                                            return builder.buildFuture();
                                                        })
                                                        .then(
                                                                Commands.argument("z",DoubleArgumentType.doubleArg())
                                                                        .suggests((ctx,builder)->{
                                                                            if (ctx.getSource().getSender() instanceof Player player){
                                                                                builder.suggest((int) player.getZ(), () -> "Decimal value for z");
                                                                            }
                                                                            return builder.buildFuture();
                                                                        })
                                                                        .executes(ctx->{
                                                                            if (ctx.getSource().getSender() instanceof Player player){
                                                                                Location loc = new Location(player.getWorld(),
                                                                                        DoubleArgumentType.getDouble(ctx,"x"),
                                                                                        DoubleArgumentType.getDouble(ctx,"y"),
                                                                                        DoubleArgumentType.getDouble(ctx,"z"),
                                                                                        0,0
                                                                                        );
                                                                                setter(loc,config, player);
                                                                                return Command.SINGLE_SUCCESS;
                                                                            }
                                                                            ctx.getSource().getSender().sendMessage("Console must give world name");
                                                                            return Command.SINGLE_SUCCESS;
                                                                        })
                                                                        .then(
                                                                                Commands.argument("world", ArgumentTypes.world())
                                                                                        .executes(ctx->{
                                                                                            Location loc = new Location(ctx.getArgument("world", World.class),
                                                                                                    DoubleArgumentType.getDouble(ctx,"x"),
                                                                                                    DoubleArgumentType.getDouble(ctx,"y"),
                                                                                                    DoubleArgumentType.getDouble(ctx,"z"),
                                                                                                    0,0
                                                                                            );
                                                                                            setter(loc,config, ctx.getSource().getSender());
                                                                                            return Command.SINGLE_SUCCESS;
                                                                                        })
                                                                        )
                                                        )
                                        )
                        )
                )
                .then(Commands.literal("full")
                        .requires(ctx -> ctx.getSender().hasPermission("wonderspawn.setspawn.full"))
                        .then(
                                Commands.argument("x",DoubleArgumentType.doubleArg())
                                        .suggests((ctx,builder)->{
                                            if (ctx.getSource().getSender() instanceof Player player){
                                                builder.suggest((int) player.getX(), () -> "Decimal value for x");
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(
                                                Commands.argument("y",DoubleArgumentType.doubleArg())
                                                        .suggests((ctx,builder)->{
                                                            if (ctx.getSource().getSender() instanceof Player player){
                                                                builder.suggest((int) player.getY(), () -> "Decimal value for y");
                                                            }
                                                            return builder.buildFuture();
                                                        })
                                                        .then(
                                                                Commands.argument("z",DoubleArgumentType.doubleArg())
                                                                        .suggests((ctx,builder)->{
                                                                            if (ctx.getSource().getSender() instanceof Player player){
                                                                                builder.suggest((int) player.getZ(), () -> "Decimal value for z");
                                                                            }
                                                                            return builder.buildFuture();
                                                                        })
                                                                        .then(
                                                                                Commands.argument("yaw",FloatArgumentType.floatArg())
                                                                                        .suggests((ctx,builder)->{
                                                                                            if (ctx.getSource().getSender() instanceof Player player){
                                                                                                builder.suggest((int) player.getYaw(), () -> "Decimal value for yaw");
                                                                                            }
                                                                                            return builder.buildFuture();
                                                                                        })
                                                                                        .then(
                                                                                        Commands.argument("pitch",FloatArgumentType.floatArg())
                                                                                                .suggests((ctx,builder)->{
                                                                                                    if (ctx.getSource().getSender() instanceof Player player){
                                                                                                        builder.suggest((int) player.getPitch(), () -> "Decimal value for pitch");
                                                                                                    }
                                                                                                    return builder.buildFuture();
                                                                                                })
                                                                                                .executes(ctx->{
                                                                                                    if (ctx.getSource().getSender() instanceof Player player){
                                                                                                        Location loc = new Location(player.getWorld(),
                                                                                                                DoubleArgumentType.getDouble(ctx,"x"),
                                                                                                                DoubleArgumentType.getDouble(ctx,"y"),
                                                                                                                DoubleArgumentType.getDouble(ctx,"z"),
                                                                                                                FloatArgumentType.getFloat(ctx,"yaw"),
                                                                                                                FloatArgumentType.getFloat(ctx,"pitch")
                                                                                                                );
                                                                                                        setter(loc,config, player);
                                                                                                        return Command.SINGLE_SUCCESS;
                                                                                                    }
                                                                                                    ctx.getSource().getSender().sendMessage("Console must give world name");
                                                                                                    return Command.SINGLE_SUCCESS;
                                                                                                })
                                                                                                .then(
                                                                                                        Commands.argument("world", ArgumentTypes.world())
                                                                                                                .executes(ctx ->{
                                                                                                                    Location loc = new Location(ctx.getArgument("world", World.class),
                                                                                                                            DoubleArgumentType.getDouble(ctx,"x"),
                                                                                                                            DoubleArgumentType.getDouble(ctx,"y"),
                                                                                                                            DoubleArgumentType.getDouble(ctx,"z"),
                                                                                                                            FloatArgumentType.getFloat(ctx,"yaw"),
                                                                                                                            FloatArgumentType.getFloat(ctx,"pitch")
                                                                                                                    );
                                                                                                                    setter(loc,config, ctx.getSource().getSender());
                                                                                                                    return Command.SINGLE_SUCCESS;
                                                                                                                })
                                                                                                )
                                                                                        )
                                                                        )
                                                        )
                                        )
                        )
                )
                .then(
                        Commands.literal("here")
                                .requires(source -> source.getSender().hasPermission("wonderspawn.setspawn.here"))
                                .executes(ctx ->{
                                    if (ctx.getSource().getSender() instanceof Player player){
                                        setter(player.getLocation(),config,player);
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    ctx.getSource().getSender().sendMessage("Only player can use");
                                    return Command.SINGLE_SUCCESS;
                                })
                )
                .build();
    }
}
