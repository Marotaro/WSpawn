package ch.wondercraft.wonderspawn.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class spawn implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NonNull @NotNull String[] strings) {
        if (commandSender instanceof Player player){
            player.teleport(player.getWorld().getSpawnLocation());
            return true;
        }
        commandSender.sendMessage("Only players can use /spawn command");
        return true;
    }
}
