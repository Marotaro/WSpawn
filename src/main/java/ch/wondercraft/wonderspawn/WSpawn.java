package ch.wondercraft.wonderspawn;


import ch.wondercraft.wonderspawn.commands.Reload;
import ch.wondercraft.wonderspawn.commands.SetSpawn;
import ch.wondercraft.wonderspawn.commands.Spawn;
import ch.wondercraft.wonderspawn.teleportation.TeleportationAction;
import ch.wondercraft.wonderspawn.teleportation.TeleportationService;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class WSpawn extends JavaPlugin implements Listener {
    private ConfigManager configManager = new ConfigManager(this);
    private TeleportationAction teleportationAction;
    private TeleportationService teleportationService;

    @Override
    public void onEnable(){
        this.configManager = new ConfigManager(this);
        try {
            configManager.load();
        } catch (ConfigManager.ConfigException e){
            getLogger().severe("Config invalid -> "+e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.teleportationAction = new TeleportationAction(configManager.spawnLocation());
        Bukkit.getScheduler().runTaskTimer(this,teleportationAction,0L,1L);
        this.teleportationService = new TeleportationService(this,configManager.spawnLocation(),configManager.sounds(),configManager.timerDisplay(),configManager.text());
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(Spawn.create(configManager.text(),teleportationService,configManager.playerWaitTime()).build());
            commands.register(SetSpawn.create(configManager.spawnLocation(),configManager.text()).build());
            commands.register(Reload.create(configManager).build());
        });
    }

    @Override
    public void onDisable(){
        if (configManager.spawnLocation() !=null)
            configManager.spawnLocation().save();
    }
}
