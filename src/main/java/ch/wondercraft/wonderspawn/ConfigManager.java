package ch.wondercraft.wonderspawn;

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
    private TextString textString;
    private Text text;
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
        File config = ensure("config.yml");
        File lang = ensure("lang/"+YamlConfiguration.loadConfiguration(config).getString("lang")+".yml","lang/en.yml");
        File spawnFile = ensureSpawn();

        TextString newTextString;
        Text newText;
        SpawnLocation newSpawnLocation;
        try {
            newSpawnLocation = SpawnLocation.load(spawnFile);
        } catch (NoSuchFileException | IllegalArgumentException e){
            throw new ConfigException("spawnLocation.yml", e.getMessage(),e);
        }
        this.spawnLocation = newSpawnLocation;
        try {
            newTextString =TextString.load(lang);
            newText = new Text(spawnLocation,newTextString);
        } catch (NoSuchFileException | IllegalArgumentException e){
            throw new ConfigException("lang/---.yml", e.getMessage(), e);
        }
            this.textString = newTextString;
            this.text=newText;
    }

    public void reload() throws ConfigException{
        File config = ensure("config.yml");
        File langFile = ensure("lang/"+YamlConfiguration.loadConfiguration(config).getString("lang")+".yml","lang/en.yml");
        ensureSpawn();

        try{
            spawnLocation.reload();
        } catch (NoSuchFileException | IllegalArgumentException e){
            throw new ConfigException("spawnLocation.yml", e.getMessage(), e);
        }
        try {
            textString.reload(langFile);
        } catch (IllegalArgumentException e){
            throw new ConfigException("config.yml", e.getMessage(), e);
        }
    }

    public SpawnLocation spawnLocation(){return spawnLocation;}
    public Text text(){return text;}
}
