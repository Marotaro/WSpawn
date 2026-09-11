package ch.wondercraft.wonderspawn;

import ch.wondercraft.wonderspawn.commands.SetSpawn;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class WSpawn extends JavaPlugin implements Listener {

    private LocationFiler locationFiler;

    @Override
     public void onEnable(){
         saveDefaultConfig();
         locationFiler = new LocationFiler(this);
         this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
                final Commands commands = event.registrar();
                commands.register(SetSpawn.create(locationFiler).build());
         });
     }
}
