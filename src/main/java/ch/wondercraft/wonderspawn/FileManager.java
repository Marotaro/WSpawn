package ch.wondercraft.wonderspawn;

import ch.wondercraft.wonderspawn.teleportation.sounds.SoundNotification;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.NoSuchFileException;

public class FileManager {
    public static File checkConfig(Plugin plugin){
        File configFile = new File(plugin.getDataFolder(),"config.yml");
        if (!configFile.exists()) plugin.saveDefaultConfig();
        return configFile;
    }
    public static File selectLangFile(File config, Plugin plugin){
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(config);
        String lang = configuration.getString("language","en");
        File langFile = new File(plugin.getDataFolder(),"lang/"+lang+".yml");
        if (!langFile.exists()){
            langFile = new File(plugin.getDataFolder(),"lang/en.yml");
            if (!langFile.exists()){
                plugin.saveResource("lang/en.yml",true);
                langFile = new File(plugin.getDataFolder(),"lang/en.yml");
            }
            return langFile;
        }
        return langFile;
    }



    public static SoundNotification createSoundsNotification(File configFile, Plugin plugin){
        try {
            SoundNotification soundNotification = SoundNotification.load(configFile);
            return soundNotification;
        } catch (NoSuchFileException e){
            Bukkit.getLogger().severe("Fatal error with sound creation");
        }
        return null;
    }
}
