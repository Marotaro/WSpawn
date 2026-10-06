package ch.wondercraft.wonderspawn;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class WSpawn extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this,this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
