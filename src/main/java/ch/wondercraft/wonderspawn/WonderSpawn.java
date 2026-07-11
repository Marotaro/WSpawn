package ch.wondercraft.wonderspawn;

import ch.wondercraft.wonderspawn.commands.Spawn;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

public class WonderSpawn extends JavaPlugin implements Listener {
     @Override
     public void onEnable(){
         saveDefaultConfig();
         Bukkit.getPluginManager().registerEvents(this, this);
         this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
             final Commands commands = event.registrar();
             commands.register(Spawn.create(getConfig()), "Se téléporter au spawn");
         });
     }

     @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
         event.getPlayer().sendMessage(Component.text("Hello, "+ event.getPlayer().getName()+"!"));
     }
}
