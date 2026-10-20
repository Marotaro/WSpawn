package ch.wondercraft.wonderspawn.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigChecker {
    public static boolean warnIfMissing(FileConfiguration fileConfiguration,String configName ,Plugin plugin , String path) {
        if (fileConfiguration.isSet(path)) return false;
        if (fileConfiguration.contains(path)) {
            plugin.getLogger().warning("Missing config key '" + path + "' in "+configName+", using default: " + fileConfiguration.get(path));
            return true;
        }
        plugin.getLogger().severe("Missing config key '" + path + "'in both config file and default config, pls contact dev");
        Bukkit.getPluginManager().disablePlugin(plugin);
        return true;
    }

    public static boolean warnIfMissing(Plugin plugin, String path){
        if (plugin.getConfig().isSet(path)) return false;
        if (plugin.getConfig().contains(path)){
            plugin.getLogger().warning("Missing config key '"+path+"' in config.yml, using default: "+plugin.getConfig().get(path));
            return true;
        }
        plugin.getLogger().severe("Missing config key '" + path + "'in both config file and default config, pls contact dev");
        Bukkit.getPluginManager().disablePlugin(plugin);
        return true;
    }
}
