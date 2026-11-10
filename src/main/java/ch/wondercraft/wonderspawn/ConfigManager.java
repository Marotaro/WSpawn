package ch.wondercraft.wonderspawn;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

public class ConfigManager {
    public class ConfigException extends Exception{

        public ConfigException(String fileName, String message, Throwable cause){
            super(fileName+" : "+message,cause);
        }
    }

    private final Plugin plugin;
    private SpawnLocation spawnLocation;

    public ConfigManager(Plugin plugin){
        this.plugin = plugin;
    }

    private File ensureSpawn(){
        File file = new File(plugin.getDataFolder(),"spawnLocation.yml");
        if (!file.exists()){
            file.getParentFile().mkdirs();
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            yamlConfiguration.set("spawn-location",plugin.getServer().getRespawnWorld().getSpawnLocation());
            try {
                file.createNewFile();
                yamlConfiguration.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return file;
    }

    public void load() throws ConfigException {
        File spawnFile = ensureSpawn();


        SpawnLocation newSpawnLocation;
        try {
            newSpawnLocation = SpawnLocation.load(spawnFile);
        } catch (NoSuchFileException | IllegalArgumentException e){
            throw new ConfigException("spawnLocation.yml", e.getMessage(),e);
        }
        this.spawnLocation = newSpawnLocation;
    }

    public void reload() throws ConfigException{
        ensureSpawn();

        try{
            spawnLocation.reload();
        } catch (NoSuchFileException | IllegalArgumentException e){
            throw new ConfigException("spawnLocation.yml", e.getMessage(), e);
        }

    }

    public SpawnLocation spawnLocation(){return spawnLocation;}
}
