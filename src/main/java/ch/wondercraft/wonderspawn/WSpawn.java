package ch.wondercraft.wonderspawn;


import ch.wondercraft.wonderspawn.commands.Reload;
import ch.wondercraft.wonderspawn.commands.SetSpawn;
import ch.wondercraft.wonderspawn.commands.Spawn;
import ch.wondercraft.wonderspawn.events.JoinEvent;
import ch.wondercraft.wonderspawn.events.RespawnEvent;
import ch.wondercraft.wonderspawn.teleportation.TeleportationCooldown;
import ch.wondercraft.wonderspawn.teleportation.TeleportationQueue;
import ch.wondercraft.wonderspawn.teleportation.TeleportationService;
import ch.wondercraft.wonderspawn.teleportation.TeleportationSound;
import ch.wondercraft.wonderspawn.text.ComponentCreator;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class WSpawn extends JavaPlugin implements Listener {
    private ConfigManager configManager = new ConfigManager(this);
    private TeleportationService teleportationService;
    private TeleportationQueue teleportationQueue;
    private TeleportationCooldown teleportationCooldown;
    private TeleportationSound teleportationSound;
    private JoinEvent joinEvent;
    private RespawnEvent respawnEvent;
    private ComponentCreator componentCreator;

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
        this.componentCreator = ComponentCreator.load(this,configManager.spawnLocation());
        this.teleportationSound = TeleportationSound.load(this);
        this.teleportationQueue = TeleportationQueue.load(this, componentCreator,teleportationSound);
        this.teleportationCooldown = TeleportationCooldown.load(this);
        this.teleportationService = new TeleportationService(this,componentCreator,configManager.spawnLocation(),teleportationQueue,teleportationSound);
        this.joinEvent = new JoinEvent(this,teleportationService, componentCreator);
        this.respawnEvent = new RespawnEvent(this,configManager.spawnLocation(),componentCreator);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(Spawn.create(componentCreator,teleportationService,teleportationCooldown).build());
            commands.register(SetSpawn.create(configManager.spawnLocation(),componentCreator).build());
            commands.register(Reload.create(this,configManager,teleportationSound,teleportationService,teleportationQueue,teleportationCooldown, componentCreator,joinEvent,respawnEvent).build());
        });

        this.getServer().getPluginManager().registerEvents(joinEvent,this);
        this.getServer().getPluginManager().registerEvents(respawnEvent, this);
    }

    @Override
    public void onDisable(){
        if (configManager.spawnLocation() !=null)
            configManager.spawnLocation().save();
    }
}
