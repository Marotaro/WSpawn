package ch.wondercraft.wonderspawn;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class LocationFiler {
    private final Plugin plugin;
    private Location spawnPoint;

    public LocationFiler(Plugin plugin){
        this.plugin = plugin;
        this.spawnPoint = load();
    }

    private Location load(){
        File file = new File(plugin.getDataFolder(), "spawnLocation.yml");
        try {
            if (!file.exists()) {
                plugin.getLogger().info("- setting up the location file");
                file.createNewFile();
                YamlConfiguration yml =YamlConfiguration.loadConfiguration(file);
                yml.set("location", plugin.getServer().getRespawnWorld().getSpawnLocation());
                yml.save(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml.getLocation("location");
    }

    public boolean save(){
        File file = new File(plugin.getDataFolder(), "spawnLocation.yml");
        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.set("location", spawnPoint);
        try {
            yml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void setSpawnPoint(Location location){spawnPoint = location;}

    public Location getSpawnPoint(){return spawnPoint;}
}
