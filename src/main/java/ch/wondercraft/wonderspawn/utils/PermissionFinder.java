package ch.wondercraft.wonderspawn.utils;

import org.bukkit.entity.Player;

public class PermissionFinder {
    public static int upper(String permissionName, Player player, int max){
        int res = -1;
        for (int i = 0; i <= max; i++){
            if (player.hasPermission(permissionName + "." + i))
                res = i;
        }
        return res;
    }
}
