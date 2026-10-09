package ch.wondercraft.wonderspawn.text;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.util.concurrent.ConcurrentHashMap;

public class TextString {
    private final ConcurrentHashMap<String,String> textMap = new ConcurrentHashMap<>();
    private final File langFile;

    private TextString(File langFile){
        this.langFile = langFile;
    }

    private void read() throws IllegalArgumentException{
        YamlConfiguration lang = YamlConfiguration.loadConfiguration(this.langFile);
        for (String path : lang.getKeys(true)){
            if (lang.isConfigurationSection(path)) continue;
            textMap.put(path, lang.getString(path));
        }
    }

    public static TextString load(File langFile) throws NoSuchFileException,IllegalArgumentException{
        if (!langFile.exists()){
            throw new NoSuchFileException("the language file does not exist");
        }
        TextString textString = new TextString(langFile);
        textString.read();
        return textString;
    }

    public void reload(){
        read();
    }

    public String getText(String key) throws NullPointerException{
        return textMap.get(key);
    }
}
