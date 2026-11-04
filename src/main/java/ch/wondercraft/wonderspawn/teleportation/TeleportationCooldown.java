package ch.wondercraft.wonderspawn.teleportation;

import ch.wondercraft.wonderspawn.utils.ConfigChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportationCooldown {
    private final Plugin plugin;
    private int defaultCooldown;
    private boolean useMax;
    private final Map<UUID, Integer> playerCooldown = new ConcurrentHashMap<>();

    private TeleportationCooldown(Plugin plugin, Data data){
        this.plugin = plugin;
        plugin.getLogger().info("Loading TeleportationCooldown");
        apply(data);
        Bukkit.getScheduler().runTaskTimer(plugin,this::tickAll,0L,1L);
    }

    public static TeleportationCooldown load(Plugin plugin){
        return new TeleportationCooldown(plugin,read(plugin));
    }

    private static Data read(Plugin plugin){
        ConfigChecker.warnIfMissing(plugin,"teleportation.cooldown.default-tick");
        int defaultCooldown = plugin.getConfig().getInt("teleportation.cooldown.default-tick");
        ConfigChecker.warnIfMissing(plugin,"teleportation.cooldown.use-max");
        boolean useMax = plugin.getConfig().getBoolean("teleportation.cooldown.use-max");
        return new Data(defaultCooldown,useMax);
    }

    private void apply(Data data){
        this.defaultCooldown = data.defaultCooldown();
        this.useMax = data.useMax();
    }

    private record Data(int defaultCooldown, boolean useMax){};

    public void reload(){
        plugin.getLogger().info("reloading TeleportationCooldown");
        apply(read(plugin));
    }

    private void tickAll(){
        playerCooldown.keySet().forEach(uuid -> {
            if (playerCooldown.get(uuid)<=0)
                playerCooldown.remove(uuid);
            else playerCooldown.put(uuid,playerCooldown.get(uuid)-1);
        });
    }

    public void add(Player player){
        int applicableCooldown = getApplicableCooldown(player);
        if (applicableCooldown > 0)
            playerCooldown.put(player.getUniqueId(),applicableCooldown);
    }

    public boolean canRequest(Player player){
        return !playerCooldown.containsKey(player.getUniqueId());
    }

    public int getRemainingTicks(Player player){
        return playerCooldown.get(player.getUniqueId());
    }

    private int getApplicableCooldown(Player player) {
        final String prefix = "wspawn.teleportation.cooldown.";
        if (player.hasPermission(prefix+"bypass")) return 0;
        int best = -1;

        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            if (!info.getValue()) continue;

            String perm = info.getPermission().toLowerCase();
            if (!perm.startsWith(prefix)) continue;
            try {
                int value = Integer.parseInt(perm.substring(prefix.length()));
                if (best == -1 || (this.useMax ? value > best : value < best)) best = value;
            } catch (NumberFormatException ignored) {}
        }
        return (best==-1)? defaultCooldown : best;
    }
}
