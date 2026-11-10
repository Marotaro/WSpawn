package ch.wondercraft.wonderspawn.text;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MessageLanguage {
    private static String BUILTIN = "en";
    //update as new languages are supported by default
    private static String[] BUNDLED = {"en.yml"};
    private static String FOLDER = "lang";
    final Plugin plugin;

    private MessageLanguage(Plugin plugin){
        this.plugin = plugin;

    }

    public static MessageLanguage load(Plugin plugin){
        return new MessageLanguage(plugin);
    }
}
