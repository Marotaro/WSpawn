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
import org.bukkit.plugin.Plugin;

import java.io.File;

public class MessagesHandler {
    private final Plugin plugin;
    private final Location location;
    private final YamlConfiguration lang;
    private final MiniMessage mm;


    public MessagesHandler(LocationFiler locationFiler, Plugin plugin){
        this.plugin = plugin;
        this.location = locationFiler.getSpawnPoint();
        this.lang = loadLang("en");
        this.mm = MiniMessage.builder().strict(false).build();
    }

    private YamlConfiguration loadLang(String lang){
        File file = new File(plugin.getDataFolder(),"lang/"+lang+".yml");
        return YamlConfiguration.loadConfiguration(file);
    }


    public Component build(String path, TagResolver... resolvers){
        String raw = lang.getString(path);
        if(raw==null){
            return Component.text("Missing message: "+path, NamedTextColor.LIGHT_PURPLE);
        }
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
}
