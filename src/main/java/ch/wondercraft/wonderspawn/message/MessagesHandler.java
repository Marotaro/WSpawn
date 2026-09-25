package ch.wondercraft.wonderspawn.message;

import ch.wondercraft.wonderspawn.LocationFiler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class MessagesHandler {
    private final Plugin plugin;
    private final LocationFiler locationFile;
    private final YamlConfiguration lang;
    private final MiniMessage mm;


    public MessagesHandler(LocationFiler locationFiler, Plugin plugin){
        this.plugin = plugin;
        this.locationFile = locationFiler;
        this.lang = loadLang("en");
        this.mm = MiniMessage.builder().strict(false).build();
    }

    private YamlConfiguration loadLang(String lang){
        File file = new File(plugin.getDataFolder(),"lang/"+lang+".yml");
        return YamlConfiguration.loadConfiguration(file);
    }


    private Component build(String path, TagResolver... resolvers){
        String raw = lang.getString(path);
        if(raw==null){
            return Component.text("Missing message: "+path, NamedTextColor.LIGHT_PURPLE);
        }
        Location location = locationFile.getSpawnPoint();
        TagResolver locationResolver = TagResolver.resolver(
                Placeholder.unparsed("spawn_world", location.getWorld().getName()),
                Placeholder.unparsed("spawn_x", String.valueOf(location.getBlockX())),
                Placeholder.unparsed("spawn_y", String.valueOf(location.getBlockY())),
                Placeholder.unparsed("spawn_z",String.valueOf(location.getBlockZ()))
        );
        String prefixString = lang.getString("prefix");
        if (prefixString ==null){
            return Component.text("Missing prefix entry in lang file", NamedTextColor.LIGHT_PURPLE);
        }
        Component prefix;
        try {
            prefix = mm.deserialize(prefixString, locationResolver);
        } catch (ParsingException e) {
            return Component.text("Malformed prefix", NamedTextColor.RED);
        }
        Component parsed;
        try{
            parsed = mm.deserialize(raw, TagResolver.resolver(locationResolver, TagResolver.resolver(resolvers)));
        } catch (ParsingException e){
            return Component.text("Malformed message:"+path, NamedTextColor.RED);
        }
        return prefix.append(parsed);
    }

    public Component spawnSelf(){
        return build("spawn.self");
    }

    public Component moved(){
        return build("spawn.moved");
    }

    public Component newDemand(){
        return build("spawn.newdemand");
    }

    public Component spawnOtherTargetByPlayer(Player commander){
        return build("spawn.other.target.by-player",
                Placeholder.unparsed("commander", commander.getName()));
    }

    public Component spawnOtherTargetByConsole(){
        return build("spawn.other.target.by-console");
    }

    public Component spawnOtherCommanderSingle(Player target){
        return build("spawn.other.commander.single",
                Placeholder.unparsed("target", target.getName()));
    }

    public Component spawnOtherCommanderAll(int number){
        return build("spawn.other.commander.all",
                Placeholder.unparsed("number", String.valueOf(number)));
    }

    public Component errorTeleportation(){
        return build("spawn.error.teleportation");
    }

    public Component errorExecutorNull(){
        return build("spawn.error.executor.null");
    }

    public Component errorExecutorNotAPlayer(){
        return build("spawn.error.executor.not_a_player");
    }

    public Component errorNotSelf(){return build("spawn.error.notself");}

    public Component setSpawn(Location location){
        return build("set.spawn",
                Placeholder.unparsed("location_x",String.valueOf(location.getX())),
                Placeholder.unparsed("location_y",String.valueOf(location.getY())),
                Placeholder.unparsed("location_z",String.valueOf(location.getZ())),
                Placeholder.unparsed("location_world",location.getWorld().getName()),
                Placeholder.unparsed("location_yaw", String.valueOf(location.getYaw())),
                Placeholder.unparsed("location_pitch",String.valueOf(location.getPitch()))
                );
    }
}

