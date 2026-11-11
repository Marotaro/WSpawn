package ch.wondercraft.wonderspawn;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

public class SpawnLocation extends Location {
    private final static String SPAWN_FILE_NAME = "spawnLocation.yml";
    private final Plugin plugin;
    private final File spawnFile;

    private SpawnLocation(Plugin plugin){
        this.plugin = plugin;
        this.spawnFile = new File(plugin.getDataFolder(),SPAWN_FILE_NAME);
        Location initLoc = plugin.getServer().getRespawnWorld().getSpawnLocation();
        super(initLoc.getWorld(), initLoc.x(), initLoc.y(), initLoc.z());
        update(read());
        plugin.getLogger().info("Loading SpawnLocation.");
    }

    private Location read(){
        if (!spawnFile.exists()){
            try {
                spawnFile.createNewFile();
                YamlConfiguration spawnLocationConfig = YamlConfiguration.loadConfiguration(spawnFile);
                spawnLocationConfig.set("spawn-location",plugin.getServer().getRespawnWorld().getSpawnLocation());
                spawnLocationConfig.save(spawnFile);
                plugin.getLogger().info("Creating spawn location file with default world as default.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Location location = YamlConfiguration.loadConfiguration(spawnFile).getLocation("spawn-location");
        if (location==null){
            throw new IllegalArgumentException("spawnLocation does not contain location or is not formated correctly");
        }
        return location;
    }

    public static SpawnLocation load(Plugin plugin){
        return new SpawnLocation(plugin);
    }

    public void update(@NotNull Location l){
        setWorld(l.getWorld());
        set(l.getX(),l.getY(),l.getZ());
        setYaw(l.getYaw());
        setPitch(l.getPitch());
    }

    public void save(){
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("spawn-location",new Location(getWorld(),getX(),getY(),getZ(),getYaw(),getPitch()));
        try {
            yamlConfiguration.save(spawnFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reload(){
        this.save();
        this.update(read());
        plugin.getLogger().info("reloading SpawnLocation.");
    }
}
