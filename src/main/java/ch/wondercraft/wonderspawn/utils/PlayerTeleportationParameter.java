package ch.wondercraft.wonderspawn.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.File;
import java.nio.file.NoSuchFileException;

public class PlayerTeleportationParameter {
    private final File config;
    private int defaultTime;
    private boolean useMin;
    private boolean defaultCanMove;

    private PlayerTeleportationParameter(File config) throws NoSuchFileException{
        this.config = config;
        apply(read(config));
    }

    private Data read(File config) throws NoSuchFileException{
        if (!config.exists()){
            throw new NoSuchFileException("config file does not exist");
        }
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(config);
        return new Data(conf.getInt("teleportation.delay"),conf.getBoolean("teleportation.use-min"),conf.getBoolean("teleportation.default-can-move"));
    }

    private void apply(Data data){
        this.defaultTime = data.defaultTime();
        this.useMin = data.useMin();
        this.defaultCanMove = data.defaultCanMove();
    }

    public static PlayerTeleportationParameter load(File config) throws NoSuchFileException{
        return new PlayerTeleportationParameter(config);
    }

    public void reload() throws NoSuchFileException{
        apply(read(this.config));
    }

    private int getApplicableDelay(Player player) {
        final String prefix = "wspawn.teleportation.delay.";
        int best = -1;

        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            if (!info.getValue()) continue;

            String perm = info.getPermission().toLowerCase();
            if (!perm.startsWith(prefix)) continue;

            try {
                int value = Integer.parseInt(perm.substring(prefix.length()));
                if (best == -1 || (this.useMin)? (value<best): (value > best)) best = value;
            } catch (NumberFormatException ignored) {}
        }
        return (best==-1)? defaultTime : best;
    }

    public int getDelay(Player player){
        if (player.hasPermission("wspawn.teleportation.delay.bypass"))
            return 0;
        else
            return getApplicableDelay(player);
    }

    public boolean getMovePossible(Player player){
        if (!player.isPermissionSet("wspawn.teleportation.move"))
            return this.defaultCanMove;
        else
            return player.hasPermission("wspawn.teleportation.move");
    }

    private record Data(int defaultTime, boolean useMin, boolean defaultCanMove){}
}
