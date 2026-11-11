package ch.wondercraft.wonderspawn.text;

import ch.wondercraft.wonderspawn.utils.ConfigChecker;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.*;

public class MessageLanguage {
    private static final String BUILTIN = "en";
    //update as new languages are supported by default
    private static final String[] BUNDLED = {};
    private static final String LANGUAGE_FOLDER = "lang";
    private final Plugin plugin;
    private final Map<String, Map<String, String>> languages = new HashMap<>();
    private final Map<String,String> builtIn = new HashMap<>();
    private String defaultLang = BUILTIN;
    private boolean forceLang = false;
    private boolean generateBundled = false;


    private MessageLanguage(Plugin plugin){
        this.plugin = plugin;
        load();
    }

    private String normalize(String s){
        return s.toLowerCase(Locale.ROOT).replace('-','_');
    }

    private Map<String, String> flatten(YamlConfiguration languageConfig, Set<String> paths){
        Map<String, String> language = new HashMap<>();
        paths.forEach(path->{
            String value = languageConfig.getString(path);
            if (value != null)language.put(path,value);
        });
        return language;
    }

    private void read(){
        languages.clear();
        File languageFolder = new File(plugin.getDataFolder(),LANGUAGE_FOLDER);
        File[] languageFiles = Objects.requireNonNull(languageFolder.listFiles());
        for (File file: languageFiles){
            String name = file.getName();
            String key = normalize(name.substring(0,name.length()-4));
            YamlConfiguration languageConfig = YamlConfiguration.loadConfiguration(file);
            languages.put(key,flatten(languageConfig,builtIn.keySet()));
        }
        ConfigChecker.warnIfMissing(plugin,"language.default");
        this.defaultLang = normalize(plugin.getConfig().getString("language.default",BUILTIN));
        ConfigChecker.warnIfMissing(plugin,"language.force");
        this.forceLang = plugin.getConfig().getBoolean("language.force");
    }

    private void load() {
        //loading built-in messages
        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(plugin.getResource(LANGUAGE_FOLDER + "/" + BUILTIN + ".yml")), StandardCharsets.UTF_8);
        YamlConfiguration builtInLanguageConf = YamlConfiguration.loadConfiguration(reader);
        builtInLanguageConf.getKeys(true).stream().filter(path -> !builtInLanguageConf.isConfigurationSection(path)).forEach(path->builtIn.put(path,builtInLanguageConf.getString(path)));
        //generating bundled languages if needed
        File languageFolder = new File(plugin.getDataFolder(),LANGUAGE_FOLDER);
        if (languageFolder.mkdirs()) plugin.getLogger().info("Languages folder was created.");
        boolean created = false;
        if (generateBundled) {
            plugin.getLogger().info("Creating bundled language files if missing:");
            for (String name : BUNDLED) {
                File file = new File(languageFolder, name);
                if (!file.exists()) {
                    try {
                        plugin.saveResource(LANGUAGE_FOLDER + "/" + name, true);
                        plugin.getLogger().info("\t" + name + " created.");
                        created = true;
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().severe("Missing language in plugin resources but still in bundled... ignoring.");
                    }
                }
            }
        }
        plugin.getLogger().info((created)? "Bundled language file(s) created." : (generateBundled)? "No bundled language files were created." : "Bundled languages are disabled.");
        read();
    }

    public static MessageLanguage load(Plugin plugin){
        return new MessageLanguage(plugin);
    }

    public void reload(){
        read();
    }

    public String get(Locale locale, String path){
        String message = null;
        if (locale !=null && !forceLang){
            message = lookup(normalize(locale.toString()), path);
            if (message == null) message = lookup(normalize(locale.getLanguage()), path);
        }
        if (message == null) message = lookup(defaultLang, path);
        if (message == null) message = builtIn.get(path);
        return message != null ? message : path;
    }

    private String lookup(String lang, String path){
        Map<String, String> messages = languages.get(lang);
        return messages == null ? null : messages.get(path);
    }
}
