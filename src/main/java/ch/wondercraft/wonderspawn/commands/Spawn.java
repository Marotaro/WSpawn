package ch.wondercraft.wonderspawn.commands;

import ch.wondercraft.wonderspawn.teleportation.TeleportationService;
import ch.wondercraft.wonderspawn.text.Text;
import ch.wondercraft.wonderspawn.utils.PlayerTeleportationParameter;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

//TODO put text output inside request to remove problem with text order

public class Spawn {

    public static LiteralArgumentBuilder<CommandSourceStack> create(Text text, TeleportationService teleportationService, PlayerTeleportationParameter playerTeleportationParameter){
        return Commands.literal("spawn").requires(commandSourceStack ->commandSourceStack.getSender().hasPermission("wspawn.spawn"))
                .executes(context -> {
                    CommandSender sender = context.getSource().getSender();
                    if (sender instanceof Player player){
                        teleportationService.request(player);
                        return Command.SINGLE_SUCCESS;
                    }
                    sender.sendMessage("need to be a player");
                    return Command.SINGLE_SUCCESS;
                });
    }
}
