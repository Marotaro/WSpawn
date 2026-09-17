package ch.wondercraft.wonderspawn.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

public class Messages {
    public static Component self(MessagesHandler messagesHandler){
        return  messagesHandler.build("spawn.self");
    }
    public static Component otherTargetByPlayer(MessagesHandler messagesHandler, Player commander){
        return messagesHandler.build("spawn.other.target.by-player", Placeholder.unparsed("commander", commander.getName()));
    }
    public static Component otherTargetByConsole(MessagesHandler messagesHandler){
        return messagesHandler.build("spawn.other.target.by-console");
    }
    public static Component otherCommanderSingle(MessagesHandler messagesHandler, Player target){
        return messagesHandler.build("spawn.other.commander.single", Placeholder.unparsed("target", target.getName()));
    }
    public static Component otherCommanderAll(MessagesHandler messagesHandler, int number){
        return messagesHandler.build("spawn.other.commander.all",Placeholder.unparsed("number",String.valueOf(number)));
    }
    public static Component errorTeleportation(MessagesHandler messagesHandler){
        return messagesHandler.build("spawn.error.teleportation");
    }
    public static Component errorExecutorNull(MessagesHandler messagesHandler){
        return messagesHandler.build("spawn.error.executor.null");
    }
    public static Component errorExecutorNotAPlayer(MessagesHandler messagesHandler){
        return messagesHandler.build("spawn.error.executor.not_a_player");
    }
}
