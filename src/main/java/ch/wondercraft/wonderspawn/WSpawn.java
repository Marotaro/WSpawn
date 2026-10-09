package ch.wondercraft.wonderspawn;


import ch.wondercraft.wonderspawn.teleportation.TeleportationAction;
import ch.wondercraft.wonderspawn.teleportation.TeleportationService;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class WSpawn extends JavaPlugin implements Listener {
    private ConfigManager configManager;
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

    }

    @Override
    public void onDisable(){
        configManager.spawnLocation().save();
    }
}
