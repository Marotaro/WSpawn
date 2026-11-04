package ch.wondercraft.wonderspawn.teleportation;

import ch.wondercraft.wonderspawn.text.Text;
import ch.wondercraft.wonderspawn.utils.ConfigChecker;
import ch.wondercraft.wonderspawn.utils.SimpleBooleanDefaultPerm;
import io.papermc.paper.math.Rotation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;

public class TeleportationService {
    private Location spawnLocation;
    private Text text;
    private Plugin plugin;
    private TeleportationQueue teleportationQueue;
    private int defaultWaitTicks;
    private boolean defaultCanMove;
    private boolean useMaxDelay;
    private boolean defaultKeepFallDistance;
    private boolean defaultFaceSetSpawnOrientation;
    private TeleportationSound teleportationSound;

    public TeleportationService(Plugin plugin, Text text, Location spawnLocation, TeleportationQueue teleportationQueue,
                                TeleportationSound teleportationSound){
        this.plugin = plugin;
        plugin.getLogger().info("Loading TeleportationService");
        read();
        this.text = text;
        this.spawnLocation = spawnLocation;
        this.teleportationQueue = teleportationQueue;
        this.teleportationSound = teleportationSound;
    }

    private void read(){
        ConfigChecker.warnIfMissing(plugin,"teleportation.delay.default-tick");
        this.defaultWaitTicks = plugin.getConfig().getInt("teleportation.delay.default-tick");
        ConfigChecker.warnIfMissing(plugin,"teleportation.default-can-move");
        this.defaultCanMove = plugin.getConfig().getBoolean("teleportation.default-can-move");
        ConfigChecker.warnIfMissing(plugin,"teleportation.delay.use-max");
        this.useMaxDelay = plugin.getConfig().getBoolean("teleportation.delay.use-max");
        ConfigChecker.warnIfMissing(plugin,"teleportation.delay.use-max");
        this.defaultKeepFallDistance = plugin.getConfig().getBoolean("teleportation.default-keep-fall-distance");
        ConfigChecker.warnIfMissing(plugin,"teleportation.default-face-set-orientation");
        this.defaultFaceSetSpawnOrientation = plugin.getConfig().getBoolean("teleportation.default-face-set-orientation");
    }

    public void reload(){
        plugin.getLogger().info("reloading TeleportationService");
        read();
    }

    public CompletableFuture<TeleportationOutCome> request(Player player){
        CompletableFuture<TeleportationOutCome> future = new CompletableFuture<>();
        Rotation playerRotation = player.getLocation().getRotation();
        player.teleportAsync(spawnLocation).thenAccept(b -> {
            if (b) {
                if (!getPlayerFaceSetOrientation(player)) player.setRotation(playerRotation.yaw(),playerRotation.pitch());
                if (!getPlayerKeepFallDistance(player)) player.setFallDistance(0L);
                teleportationSound.play(player, TeleportationSound.Type.FINAL);
                future.complete(TeleportationOutCome.SUCCESS);
            } else {
                teleportationSound.play(player, TeleportationSound.Type.ERROR);
                future.complete(TeleportationOutCome.ERROR);
            }
        });
        return future;
    }

    public CompletableFuture<TeleportationOutCome> request(Player player, int waitTicks, boolean canMove){
        if (waitTicks == 0){
            return request(player);
        }
        CompletableFuture<TeleportationOutCome> future = new CompletableFuture<>();
        teleportationQueue.add(player,waitTicks,canMove).thenAccept(waitOutCome -> {
            if (waitOutCome == TeleportationQueue.WaitInfo.WaitOutCome.SUCCESS){
                Rotation playerRotation = player.getLocation().getRotation();
                player.teleportAsync(spawnLocation).thenAccept(success -> {
                    if (success){
                        if (!getPlayerFaceSetOrientation(player)) player.setRotation(playerRotation.yaw(),playerRotation.pitch());
                        if (!getPlayerKeepFallDistance(player)) player.setFallDistance(0L);
                        teleportationSound.play(player, TeleportationSound.Type.FINAL);
                        future.complete(TeleportationOutCome.SUCCESS);
                    } else {
                        teleportationSound.play(player, TeleportationSound.Type.ERROR);
                        future.complete(TeleportationOutCome.ERROR);
                    }
                });
            } else if (waitOutCome == TeleportationQueue.WaitInfo.WaitOutCome.MOVED) {
                teleportationSound.play(player, TeleportationSound.Type.ERROR);
                future.complete(TeleportationOutCome.MOVED);
            } else if (waitOutCome == TeleportationQueue.WaitInfo.WaitOutCome.CANCELED) {
                future.complete(TeleportationOutCome.CANCELED);
            }
        });
        return future;
    }

    public int getApplicableDelay(Player player) {
        final String prefix = "wspawn.teleportation.delay.";
        if (player.hasPermission(prefix+"bypass")) return 0;
        int best = -1;

        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            if (!info.getValue()) continue;

            String perm = info.getPermission().toLowerCase();
            if (!perm.startsWith(prefix)) continue;
            try {
                int value = Integer.parseInt(perm.substring(prefix.length()));
                if (best == -1 || (this.useMaxDelay ? value > best : value < best)) best = value;
            } catch (NumberFormatException ignored) {}
        }
        return (best==-1)? defaultWaitTicks : best;
    }

    public boolean getPlayerCanMove(Player player){
       return SimpleBooleanDefaultPerm.getter(player,"wspawn.teleportation.move", defaultCanMove);
    }

    private boolean getPlayerKeepFallDistance(Player player){
        return SimpleBooleanDefaultPerm.getter(player,"wspawn.teleportation.keep-fall-distance", defaultKeepFallDistance);
    }

    private boolean getPlayerFaceSetOrientation(Player player){
        return SimpleBooleanDefaultPerm.getter(player,"wspawn.teleportation.face-set-orientation", defaultFaceSetSpawnOrientation);
    }


}
