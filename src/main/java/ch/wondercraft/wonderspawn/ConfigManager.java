package ch.wondercraft.wonderspawn;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

public class ConfigManager {
    public static void create(Plugin plugin){
        plugin.saveDefaultConfig();
    }
}
