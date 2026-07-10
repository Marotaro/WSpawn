package ch.wondercraft.wonderspawn;

import ch.wondercraft.wonderspawn.commands.spawn;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class WonderSpawn extends JavaPlugin implements Listener, CommandExecutor {
     @Override
     public void onEnable(){
         Bukkit.getPluginManager().registerEvents(this, this);
         Objects.requireNonNull(getCommand("spawn")).setExecutor(new spawn());
     }

     @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
         event.getPlayer().sendMessage(Component.text("Hello, "+ event.getPlayer().getName()+"!"));
     }
}
