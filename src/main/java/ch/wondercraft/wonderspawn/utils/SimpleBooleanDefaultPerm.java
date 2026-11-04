package ch.wondercraft.wonderspawn.utils;

import org.bukkit.entity.Player;

public class SimpleBooleanDefaultPerm {
    public static boolean getter(Player player, String permName, boolean defaultValue){
        return (!player.isPermissionSet(permName))? defaultValue : player.hasPermission(permName);
    }
}
