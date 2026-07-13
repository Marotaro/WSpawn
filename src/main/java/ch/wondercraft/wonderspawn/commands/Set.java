package ch.wondercraft.wonderspawn.commands;

import com.mojang.brigadier.Command;
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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class Set {

    private static void setter(Location location, FileConfiguration config){
        config.set("spawn.world",location.getWorld().getName());
        config.set("spawn.x", location.getX());
        config.set("spawn.y", location.getY());
        config.set("spawn.z", location.getZ());
        config.set("spawn.yaw", location.getYaw());
        config.set("spawn.pitch", location.getPitch());
    }

    public static LiteralCommandNode<CommandSourceStack> create(FileConfiguration config){
        return Commands.literal("setspawn")
                .requires(source -> source.getSender().hasPermission("wonderspawn.setspawn"))
                .executes(ctx->{
                    if (ctx.getSource().getSender() instanceof Player player){
                        Location loc = player.getLocation();
                        setter(loc, config);
                        return Command.SINGLE_SUCCESS;
                    }
                    ctx.getSource().getSender().sendMessage("Only player can use /setspawn please use /setspawn x y z [yaw pitch] world");
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                        .suggests((ctx,builder)->{
                            if (ctx.getSource().getSender() instanceof Player player){
                                builder.suggest((int)player.getLocation().getX());
                            }
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("y",DoubleArgumentType.doubleArg())
                                .suggests((ctx,builder)->{
                                    if (ctx.getSource().getSender() instanceof Player player){
                                        builder.suggest((int)player.getLocation().getY());
                                    }
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("z",DoubleArgumentType.doubleArg())
                                        .suggests((ctx,builder)->{
                                            if (ctx.getSource().getSender() instanceof Player player){
                                                builder.suggest((int)player.getLocation().getZ());
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx ->{
                                            if (ctx.getSource().getSender() instanceof Player player){
                                                World world = player.getWorld();
                                                Location loc = new Location(world,
                                                        DoubleArgumentType.getDouble(ctx,"x"),
                                                        DoubleArgumentType.getDouble(ctx,"y"),
                                                        DoubleArgumentType.getDouble(ctx,"z"),
                                                        0,
                                                        0
                                                );
                                                setter(loc,config);
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            ctx.getSource().getSender().sendMessage("Only player are allowed to not provide world");
                                            return Command.SINGLE_SUCCESS;
                                        })
                                        .then(Commands.argument("world", ArgumentTypes.world())
                                                .executes(ctx ->{
                                                    Location loc = new Location(ctx.getArgument("world",World.class),
                                                            DoubleArgumentType.getDouble(ctx,"x"),
                                                            DoubleArgumentType.getDouble(ctx,"y"),
                                                            DoubleArgumentType.getDouble(ctx,"z"),
                                                            0,
                                                            0
                                                    );
                                                    setter(loc, config);
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                        .then(Commands.argument("yaw", FloatArgumentType.floatArg())
                                                .suggests((ctx,builder)->{
                                                    if (ctx.getSource().getSender() instanceof Player player){
                                                        builder.suggest((int)player.getYaw());
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .then(Commands.argument("pitch",FloatArgumentType.floatArg())
                                                        .suggests((ctx, builder)->{
                                                            if (ctx.getSource().getSender() instanceof Player player){
                                                                builder.suggest((int)player.getPitch());
                                                            }
                                                            return builder.buildFuture();
                                                        })
                                                        .executes(ctx ->{
                                                            if (ctx.getSource().getSender() instanceof Player player){
                                                                World world = player.getWorld();
                                                                Location loc = new Location(world,
                                                                        DoubleArgumentType.getDouble(ctx,"x"),
                                                                        DoubleArgumentType.getDouble(ctx,"y"),
                                                                        DoubleArgumentType.getDouble(ctx,"z"),
                                                                        FloatArgumentType.getFloat(ctx,"yaw"),
                                                                        FloatArgumentType.getFloat(ctx,"pitch")
                                                                );
                                                                setter(loc,config);
                                                                return Command.SINGLE_SUCCESS;
                                                            }
                                                            ctx.getSource().getSender().sendMessage("Only player are allowed to not provide world");
                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                                        .then(Commands.argument("world",ArgumentTypes.world())
                                                                .executes(ctx -> {
                                                                    Location loc = new Location(ctx.getArgument("world",World.class),
                                                                            DoubleArgumentType.getDouble(ctx,"x"),
                                                                            DoubleArgumentType.getDouble(ctx,"y"),
                                                                            DoubleArgumentType.getDouble(ctx,"z"),
                                                                            FloatArgumentType.getFloat(ctx,"yaw"),
                                                                            FloatArgumentType.getFloat(ctx,"pitch")
                                                                    );
                                                                    setter(loc,config);
                                                                    return Command.SINGLE_SUCCESS;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .build();
    }
}
