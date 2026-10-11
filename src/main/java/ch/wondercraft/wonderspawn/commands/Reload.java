package ch.wondercraft.wonderspawn.commands;

import ch.wondercraft.wonderspawn.ConfigManager;
import ch.wondercraft.wonderspawn.text.Text;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;

public class Reload {
    public static LiteralArgumentBuilder<CommandSourceStack> create(ConfigManager configManager){
        return Commands.literal("reload").requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("wspawn.reload"))
                .executes(context -> {
                    CommandSender commandSender = context.getSource().getSender();
                    commandSender.sendMessage(configManager.text().reloadPromise());
                    try {
                        configManager.reload();
                        commandSender.sendMessage(configManager.text().reloadConfirmation());
                    } catch (ConfigManager.ConfigException e) {
                        commandSender.sendMessage(configManager.text().reloadFailed(e));
                    }
                    return Command.SINGLE_SUCCESS;
                });
    }
}
