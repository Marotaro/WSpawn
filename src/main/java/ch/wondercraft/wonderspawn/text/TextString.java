package ch.wondercraft.wonderspawn.text;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.util.concurrent.ConcurrentHashMap;

public class TextString {
    private final ConcurrentHashMap<String,String> textMap = new ConcurrentHashMap<>();

    private TextString(){
    }

    private void read(File langFile) throws IllegalArgumentException{
        YamlConfiguration lang = YamlConfiguration.loadConfiguration(langFile);
        for (String path : lang.getKeys(true)){
            if (lang.isConfigurationSection(path)) continue;
            textMap.put(path, lang.getString(path));
        }
    }

    public static TextString load(File langFile) throws NoSuchFileException,IllegalArgumentException{
        if (!langFile.exists()){
            throw new NoSuchFileException("the language file does not exist");
        }
        TextString textString = new TextString();
        textString.read(langFile);
        return textString;
    }

    public void reload(File langFile){
        read(langFile);
    }

    public String getText(String key) throws NullPointerException{
        return textMap.get(key);
    }
}
