package ch.wondercraft.wonderspawn;

import ch.wondercraft.wonderspawn.commands.SetSpawn;
import ch.wondercraft.wonderspawn.commands.Spawn;
import ch.wondercraft.wonderspawn.message.MessagesHandler;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class WSpawn extends JavaPlugin implements Listener {

    private LocationFiler locationFiler;

    @Override
    public void onEnable(){
        saveDefaultConfig();
        locationFiler = new LocationFiler(this);
        MessagesHandler messagesHandler = new MessagesHandler(locationFiler, this);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(SetSpawn.create(locationFiler).build());
            commands.register(Spawn.create(locationFiler, messagesHandler).build());
        });
    }

    @Override
    public void onDisable(){
        locationFiler.save();
    }
}
