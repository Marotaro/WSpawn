package ch.wondercraft.wonderspawn;


import ch.wondercraft.wonderspawn.teleportation.TeleportationAction;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class WSpawn extends JavaPlugin implements Listener {
    private ConfigManager configManager;
    private TeleportationAction teleportationAction;

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

    }

    @Override
    public void onDisable(){
        configManager.spawnLocation().save();
    }
}
