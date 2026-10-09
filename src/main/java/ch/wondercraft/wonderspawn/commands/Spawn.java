package ch.wondercraft.wonderspawn.commands;

import ch.wondercraft.wonderspawn.teleportation.TeleportationOutCome;
import ch.wondercraft.wonderspawn.teleportation.TeleportationService;
import ch.wondercraft.wonderspawn.text.Text;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class Spawn {

    public static LiteralArgumentBuilder<CommandSourceStack> create(Text text, TeleportationService teleportationService){
        return Commands.literal("spawn").requires(commandSourceStack ->commandSourceStack.getSender().hasPermission("wspawn.spawn"))
                .executes(context -> {
                    CommandSender sender = context.getSource().getSender();
                    if (sender instanceof Player player){
                        teleportationService.requestTeleportation(player,10,true).thenAccept(
                                teleportationOutCome -> {
                                    if (Objects.requireNonNull(teleportationOutCome) == TeleportationOutCome.SUCCESS) {
                                        text.spawn();
                                    }
                                }
                        );
                        return Command.SINGLE_SUCCESS;
                    }
                    sender.sendMessage("need to be a player");
                    return Command.SINGLE_SUCCESS;
                });
    }
}
