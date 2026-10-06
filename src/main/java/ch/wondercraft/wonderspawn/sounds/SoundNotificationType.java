package ch.wondercraft.wonderspawn.sounds;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.NoSuchFileException;

public class SoundNotificationType {
    private final String key;
    private String name;
    private float volume;
    private float pitch;

    private SoundNotificationType(String key, Data data){
        this.key = key;
        apply(data);
    }

    private record Data(String name, float volume, float pitch){}

    private void apply(Data data){
        this.name = data.name();
        this.volume = data.volume();
        this.pitch = data.pitch();
    }

    private static Data read(File config, String key) throws NoSuchFileException,IllegalArgumentException{
        if (!config.exists()){
            throw new NoSuchFileException("sounds file does not exist");
        }
        ConfigurationSection section = YamlConfiguration.loadConfiguration(config).getConfigurationSection("sounds."+key);
        if (section == null || section.getString("name")==null){
            throw new IllegalArgumentException("sound '"+key+"' is missing or not formatted correctly");
        }

        return new Data(
                section.getString("name"),
                (float) section.getDouble("volume",1.0),
                (float) section.getDouble("pitch",1.0)
                );
    }

    public static SoundNotificationType load(File config, String key) throws NoSuchFileException,IllegalArgumentException{
        return new SoundNotificationType(key,read(config,key));
    }

    public void reload(File config) throws NoSuchFileException,IllegalArgumentException{
        apply(read(config,this.key));
    }

    public void play(Player player){
        Sound sound = Sound.sound(Key.key(name), Sound.Source.MASTER,volume,pitch);
        player.playSound(sound, Sound.Emitter.self());
    }
}
