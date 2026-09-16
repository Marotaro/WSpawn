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

public class MessagesHandler {
    private final Location location;
    private final YamlConfiguration lang;
    private final MiniMessage mm;

    public MessagesHandler(LocationFiler locationFiler, YamlConfiguration lang){
        this.location = locationFiler.getSpawnPoint();
        this.lang = lang;
        this.mm = MiniMessage.builder().strict(false).build();
    }


    private Component build(String path, TagResolver... resolvers){
        String raw = lang.getString(path);
        if(raw==null){
            return Component.text("Missing message: "+path, NamedTextColor.LIGHT_PURPLE);
        }
        TagResolver locationResolver = TagResolver.resolver(
                Placeholder.unparsed("spawn.world", location.getWorld().getName()),
                Placeholder.unparsed("spawn.x", String.valueOf(location.getBlockX())),
                Placeholder.unparsed("spawn.y", String.valueOf(location.getBlockY())),
                Placeholder.unparsed("spawn.z",String.valueOf(location.getBlockZ()))
        );
        Component parsed;
        try{
            parsed = mm.deserialize(raw, TagResolver.resolver(resolvers));
        } catch (ParsingException e){
            return Component.text("Malformed message:"+path, NamedTextColor.RED);
        }
        return parsed;
    }

    final class spawn {
        public Component self(){
            return build("spawn.self");
        }
    }
}
