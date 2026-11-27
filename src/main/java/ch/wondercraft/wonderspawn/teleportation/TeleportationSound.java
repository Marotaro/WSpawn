package ch.wondercraft.wonderspawn.teleportation;

import ch.wondercraft.wonderspawn.Reloadable;
import ch.wondercraft.wonderspawn.utils.ConfigChecker;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class TeleportationSound implements Reloadable {
    private static final String SOUND_NAME_SUB_PATH = ".name";
    private static final String SOUND_VOLUME_SUB_PATH = ".volume";
    private static final String SOUND_PITCH_SUB_PATH = ".pitch";
    public enum teleportationSoundType {INITIAL, INTERVAL, FINAL, ERROR, CANCELED, VOID};
    private final Plugin plugin;
    private final Map<teleportationSoundType, Sound> soundMap = new HashMap<>();

    public TeleportationSound(Plugin plugin){
        this.plugin = plugin;
        load(this.plugin);
    }

    private void read(){
        for (teleportationSoundType type : teleportationSoundType.values()){
            String path = "sounds."+type.toString().toLowerCase();
            boolean missing = ConfigChecker.warnIfMissing(this.plugin,path);
            String namePath = path+SOUND_NAME_SUB_PATH;
            String volumePath = path+SOUND_VOLUME_SUB_PATH;
            String pitchPath = path+SOUND_PITCH_SUB_PATH;
            if (!missing){
                ConfigChecker.warnIfMissing(plugin,namePath);
                ConfigChecker.warnIfMissing(plugin,volumePath);
                ConfigChecker.warnIfMissing(plugin,pitchPath);
            }
            Key key;
            try {
                String soundName = plugin.getConfig().getString(namePath);
                if (soundName==null) soundName="";
                key = Key.key(soundName);
            } catch (InvalidKeyException e){
                plugin.getLogger().warning("Sounds: invalid name for "+type.toString().toLowerCase()+" sound");
                key = Key.key("");
            }
            float volume = (float) plugin.getConfig().getDouble(volumePath);
            float pitch = (float) plugin.getConfig().getDouble(pitchPath);
            soundMap.put(type,Sound.sound(key, Sound.Source.MASTER,volume,pitch));
        }
    }

    @Override
    public void load(Plugin plugin) {
        Reloadable.super.load(plugin);
        read();
    }

    @Override
    public void reload(Plugin plugin) {
        Reloadable.super.reload(plugin);
        read();
    }

    public void play(Player player, teleportationSoundType type){
        player.playSound(soundMap.get(type));
    }
}
