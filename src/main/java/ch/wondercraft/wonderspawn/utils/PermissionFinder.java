package ch.wondercraft.wonderspawn.utils;

import org.bukkit.entity.Player;

public class PermissionFinder {
    public static int upper(String permissionName, Player player, int max){
        if (!player.isPermissionSet(permissionName)) throw new IllegalArgumentException("player has no permission named: "+ permissionName);
        else {
            int res = 0;
            for (int i = 0; i<=max; i++){
                if (player.hasPermission(permissionName+"."+i))
                    res = i;
            }
            return res;
        }
    }
}
