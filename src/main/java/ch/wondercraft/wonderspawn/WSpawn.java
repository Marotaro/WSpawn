package ch.wondercraft.wonderspawn;


import ch.wondercraft.wonderspawn.commands.Reload;
import ch.wondercraft.wonderspawn.commands.SetSpawn;
import ch.wondercraft.wonderspawn.commands.Spawn;
import ch.wondercraft.wonderspawn.events.JoinEvent;
import ch.wondercraft.wonderspawn.events.RespawnEvent;
import ch.wondercraft.wonderspawn.events.VoidEvent;
import ch.wondercraft.wonderspawn.teleportation.TeleportationCooldown;
import ch.wondercraft.wonderspawn.teleportation.TeleportationQueue;
import ch.wondercraft.wonderspawn.teleportation.TeleportationService;
import ch.wondercraft.wonderspawn.teleportation.TeleportationSound;
import ch.wondercraft.wonderspawn.text.ComponentCreator;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Time;
import java.util.Timer;

public class WSpawn extends JavaPlugin implements Listener {
    private SpawnLocation spawnLocation;
    private TeleportationService teleportationService;
    private TeleportationQueue teleportationQueue;
    private TeleportationCooldown teleportationCooldown;
    private TeleportationSound teleportationSound;
    private JoinEvent joinEvent;
    private RespawnEvent respawnEvent;
    private VoidEvent voidEvent;
    private ComponentCreator componentCreator;

    @Override
    public void onEnable(){
        long start = System.currentTimeMillis();
        ConfigManager.create(this);
        this.spawnLocation = new SpawnLocation(this);
        this.componentCreator = new ComponentCreator(this,spawnLocation);
        this.teleportationSound = new TeleportationSound(this);
        this.teleportationQueue = new TeleportationQueue(this,componentCreator,teleportationSound);
        this.teleportationCooldown = new TeleportationCooldown(this);
        this.teleportationService = new TeleportationService(this,componentCreator,spawnLocation,teleportationQueue,teleportationSound);
        this.joinEvent = new JoinEvent(this,teleportationService, componentCreator);
        this.respawnEvent = new RespawnEvent(this,spawnLocation,componentCreator);
        this.voidEvent = new VoidEvent(this,componentCreator,teleportationService);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(Spawn.create(componentCreator,teleportationService,teleportationCooldown).build());
            commands.register(Commands.literal("wspawn")
                    .then(SetSpawn.create(spawnLocation,componentCreator))
                    .then(Reload.create(this,componentCreator,
                            spawnLocation,componentCreator,teleportationSound,
                            teleportationQueue,teleportationCooldown,teleportationService,
                            joinEvent,respawnEvent,voidEvent))
                    .build());
        });
        this.getServer().getPluginManager().registerEvents(joinEvent,this);
        this.getServer().getPluginManager().registerEvents(respawnEvent, this);
        this.getServer().getPluginManager().registerEvents(voidEvent,this);
        long finish = System.currentTimeMillis();
        this.getLogger().info("Done in "+Long.toString(finish-start,4)+"ms.");
    }

    @Override
    public void onDisable(){
        spawnLocation.save();
    }
}
