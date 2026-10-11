package ch.wondercraft.wonderspawn.text;

import ch.wondercraft.wonderspawn.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

    private Component textWithRemainingSecond(int remainingSecond, String key){
        return build(key,Placeholder.unparsed("remaining-second",String.valueOf(remainingSecond)));
    }

    public Component spawnPromise(int remainingSecond){
        return textWithRemainingSecond(remainingSecond,"spawn.self.promise");
    }

    public Component spawnConfirmation(){
        return build("spawn.self.confirmation");
    }

    public Component forcedSpawnNotice(CommandSender sender){
        return build("spawn.forced.target",
                Placeholder.unparsed("sender",sender.getName())
                );
    }

    public Component forcedSpawnConfirmation(Player target){
        return build("spawn.forced.sender",
                Placeholder.unparsed("target",target.getName())
        );
    }

    public Component forcedSpawnTryMoreThanOne(){
        return build("spawn.forced.error.more-than-one");
    }


    public Component bar(int remainingSecond){
        return textWithRemainingSecond(remainingSecond,"timer.bar");
    }

    public Component chat(int remainingSecond){
        return textWithRemainingSecond(remainingSecond,"timer.chat");
    }

    public Component setSpawnConfirmation(Location oldLocation){
        return build("set-spawn.confirmation",
                Placeholder.unparsed("old-x",String.valueOf(Math.round(oldLocation.getX()))),
                Placeholder.unparsed("old-y",String.valueOf(Math.round(oldLocation.getY()))),
                Placeholder.unparsed("old-z",String.valueOf(Math.round(oldLocation.getZ()))),
                Placeholder.unparsed("old-world",String.valueOf(oldLocation.getWorld())),
                Placeholder.unparsed("old-yaw",String.valueOf(oldLocation.getYaw())),
                Placeholder.unparsed("old-pitch",String.valueOf(oldLocation.getPitch()))
                );
    }

    public Component setSpawnNeedsVerboseVersion(){
        return build("set-spawn.error.needs-verbose");
    }

    public Component reloadPromise(){
        return build("reload.promise");
    }

    public Component reloadConfirmation(){
        return build("reload.confirmation");
    }

    public Component reloadFailed(ConfigManager.ConfigException e){
        return build("reload.error.bad",
                Placeholder.unparsed("error-file",e.getFileName()),
                Placeholder.unparsed("error-message",e.getMessage())
                );
    }

}
