package ch.wondercraft.wonderspawn.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.*;

public class Spawn {


    public static LiteralCommandNode<CommandSourceStack> create(FileConfiguration config){
        return Commands.literal("spawn")
                .requires(source -> source.getSender().hasPermission("wonderspawn.spawn"))
                .executes(ctx->{
                    CommandSourceStack source = ctx.getSource();
                    CommandSender sender = source.getSender();

                    if (sender instanceof Player player){
                        Location loc = new Location(getWorld(config.getString("spawn.world")),
                                config.getDouble("spawn.x"),config.getDouble("spawn.y"),
                                config.getDouble("spawn.z"),(float) config.getDouble("spawn.yaw"),
                                (float) config.getDouble("spawn.pitch"));
                        player.teleport(loc);
                        return Command.SINGLE_SUCCESS;
                    }
                    sender.sendMessage("Only player can use this command");
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }
}