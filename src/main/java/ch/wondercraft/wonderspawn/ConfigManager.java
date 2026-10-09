package ch.wondercraft.wonderspawn;

import ch.wondercraft.wonderspawn.commands.Spawn;
import ch.wondercraft.wonderspawn.teleportation.TimerDisplay;
import ch.wondercraft.wonderspawn.teleportation.sounds.SoundNotification;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

public class ConfigManager {
    public class ConfigException extends Exception{
        private final String fileName;

        public ConfigException(String fileName, String message, Throwable cause){
            super(fileName+" : "+message,cause);
            this.fileName = fileName;
        }
        public String getFileName(){return fileName;}
    }

    private final Plugin plugin;
    private SoundNotification sounds;
    private TimerDisplay timerDisplay;
    private SpawnLocation spawnLocation;

    public ConfigManager(Plugin plugin){
        this.plugin = plugin;
    }

    private File ensure(String name) throws ConfigException{
        File file = new File(plugin.getDataFolder(),name);
        if (!file.exists()){
            try {
                plugin.saveResource(name,false);
            } catch (IllegalArgumentException e){
                throw new ConfigException(name,"ressource absente in jar",e);
            }

        }
        return file;
    }

    private File ensure(String name, String defName) throws ConfigException{
        File file = new File(plugin.getDataFolder(),name);
        if (!file.exists()){
            try{
                file = ensure(defName);
            } catch (ConfigException e){
                throw new ConfigException(defName+" and "+e.fileName,"ressource absent",e);
            }
        }
        return file;
    }

    private File ensureSpawn(){
        File file = new File(plugin.getDataFolder(),"spawnLocation.yml");
        if (file.exists()){
            file.mkdirs();
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            yamlConfiguration.set("spawn-location",plugin.getServer().getRespawnWorld().getSpawnLocation());
            try {
                yamlConfiguration.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return file;
    }

    public void load() throws ConfigException {
        File config = ensure("config.yml");
        File lang = ensure("lang/"+YamlConfiguration.loadConfiguration(config).getString("lang")+".yml","lang/en.yml");
        File soundsFile = ensure("sounds.yml");
        File spawnFile = ensureSpawn();

        SoundNotification newSounds;
        TimerDisplay newTimer;
        SpawnLocation newSpawnLocation;
        try {
            newSounds = SoundNotification.load(soundsFile);
        } catch (NoSuchFileException | IllegalArgumentException e){
            throw new ConfigException("sounds.yml", e.getMessage(), e);
        }
        try {
            newTimer = TimerDisplay.load(config,lang);
        } catch (NoSuchFileException | IllegalArgumentException e){
            throw new ConfigException("config.yml", e.getMessage(), e);
        }
        try {
            newSpawnLocation = SpawnLocation.load(spawnFile);
        } catch (NoSuchFileException | IllegalArgumentException e){
            throw new ConfigException("spawnLocation.yml", e.getMessage(),e);
        }
        this.sounds = newSounds;
        this.timerDisplay = newTimer;
        this.spawnLocation = newSpawnLocation;
    }

    public void reload() throws ConfigException{
        File config = ensure("config.yml");
        ensure("lang/"+YamlConfiguration.loadConfiguration(config).getString("lang")+".yml","lang/en.yml");
        ensure("sounds.yml");
        ensureSpawn();

        try{
            sounds.reload();
        } catch (NoSuchFileException | IllegalArgumentException e){
            throw new ConfigException("sounds.yml", e.getMessage(), e);
        }
        try{
            timerDisplay.reload();
        } catch (NoSuchFileException | IllegalArgumentException e){
            throw new ConfigException("config.yml", e.getMessage(), e);
        }
        try{
            spawnLocation.reload();
        } catch (NoSuchFileException | IllegalArgumentException e){
            throw new ConfigException("spawnLocation.yml", e.getMessage(), e);
        }
    }

    public SoundNotification getSounds(){return sounds;}
    public TimerDisplay timerDisplay(){return timerDisplay;}
    public SpawnLocation spawnLocation(){return spawnLocation;}
}
