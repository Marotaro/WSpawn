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
                    Placeholder.unparsed("spawn-x",String.valueOf(spawnLocation.getBlockX())),
                    Placeholder.unparsed("spawn-y",String.valueOf(spawnLocation.getBlockY())),
                    Placeholder.unparsed("spawn-z",String.valueOf(spawnLocation.getBlockZ())),
                    Placeholder.unparsed("spawn-world",spawnLocation.getWorld().getName()),
                    Placeholder.unparsed("spawn-yaw",String.valueOf(spawnLocation.getYaw())),
                    Placeholder.unparsed("spawn-pitch",String.valueOf(spawnLocation.getPitch()))
            );
            return mm.deserialize(text,TagResolver.builder().resolver(locationReslover).resolvers(tagResolvers).build());

        } catch (NullPointerException e){
            return Component.text(messageKey+" does not exist in lang file "+e.getMessage(), NamedTextColor.DARK_PURPLE);
        }
    }

    public Component spawn(){
        return build("spawn.self");
    }

    private Component timer(int remainingSecond, String key){
        return build(key,Placeholder.unparsed("remaining-second",String.valueOf(remainingSecond)));
    }

    public Component bar(int remainingSecond){
        return timer(remainingSecond,"timer.bar");
    }
    public Component tchat(int remainingSecond){
        return timer(remainingSecond,"timer.tchat");
    }


}
