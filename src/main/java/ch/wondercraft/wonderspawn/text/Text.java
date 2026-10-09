package ch.wondercraft.wonderspawn.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;

public class Text {
    private  MiniMessage mm = MiniMessage.builder().build();
    private Location spawnLocation;
    private TextString textString;

    public Text(Location spawnLocation, TextString textString){
        this.spawnLocation = spawnLocation;
        this.textString = textString;
    }
    private Component build (String messageKey,TagResolver ...tagResolvers){
        try {
            String text = textString.getText(messageKey);
            if (text.contains("<prefix>")){
                text = text.replace("<prefix>",textString.getText("prefix"));
            }
            TagResolver locationReslover = TagResolver.resolver(
                    Placeholder.unparsed("Spawn-x",String.valueOf(spawnLocation.getBlockX())),
                    Placeholder.unparsed("Spawn-y",String.valueOf(spawnLocation.getBlockY())),
                    Placeholder.unparsed("Spawn-z",String.valueOf(spawnLocation.getBlockZ())),
                    Placeholder.unparsed("Spawn-world",spawnLocation.getWorld().getName()),
                    Placeholder.unparsed("Spawn-yaw",String.valueOf(spawnLocation.getYaw())),
                    Placeholder.unparsed("Spawn-pitch",String.valueOf(spawnLocation.getPitch()))
            );
            return mm.deserialize(text,TagResolver.builder().resolver(locationReslover).resolvers(tagResolvers).build());

        } catch (NullPointerException e){
            return Component.text(messageKey+" does not exist in lang file", NamedTextColor.DARK_PURPLE);
        }
    }

    public Component spawn(){
        return build("Spawn");
    }
}
