package ch.wondercraft.wonderspawn.teleportation;

import ch.wondercraft.wonderspawn.utils.ConfigChecker;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.EnumMap;
import java.util.Map;

public class TeleportationSound {
    public enum Type {INITIAL, INTERVAL, FINAL, ERROR,CANCELED;}
    private final Plugin plugin;
    private Map<Type, Sound> sounds;

    private TeleportationSound(Plugin plugin, Map<Type,Sound> sounds){
        this.plugin = plugin;
        this.sounds = sounds;
    }

    private static Map<Type, Sound> read(Plugin plugin){
        Map<Type,Sound> result = new EnumMap<>(Type.class);
        for (Type type : Type.values()){
            String path = "sounds."+type.toString().toLowerCase();
            ConfigChecker.warnIfMissing(plugin, path+".name");
            String name = plugin.getConfig().getString(path+".name");

            Key key;
            try {
                key = Key.key(name);
            } catch (InvalidKeyException e) {
                continue;
            }
            ConfigChecker.warnIfMissing(plugin,path+".volume");
            float volume = (float) plugin.getConfig().getDouble(path+".volume");
            ConfigChecker.warnIfMissing(plugin,path+".pitch");
            float pitch = (float) plugin.getConfig().getDouble(path+".pitch");
            result.put(type,Sound.sound(key, Sound.Source.MASTER,volume,pitch));
        }
        return result;
    }

    public static TeleportationSound load(Plugin plugin){
        plugin.getLogger().info("loading Sounds");
        return new TeleportationSound(plugin,read(plugin));
    }

    public void reload(){
        plugin.getLogger().info("reloading Sounds");
        this.sounds = read(plugin);
    }

    public void play(Player player, Type type){
        player.playSound(sounds.get(type));
    }
}
