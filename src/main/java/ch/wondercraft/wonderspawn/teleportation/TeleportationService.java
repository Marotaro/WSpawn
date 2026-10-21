package ch.wondercraft.wonderspawn.teleportation;

import ch.wondercraft.wonderspawn.text.Text;
import ch.wondercraft.wonderspawn.utils.ConfigChecker;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;

public class TeleportationService {
    private Location spawnLocation;
    private Text text;
    private Plugin plugin;
    private TeleportationQueue teleportationQueue;
    private int defaultWaitTicks;
    private boolean defaultCanMove;
    private TeleportationSound teleportationSound;

    public TeleportationService(Plugin plugin, Text text, Location spawnLocation, TeleportationQueue teleportationQueue,
                                TeleportationSound teleportationSound){
        this.plugin = plugin;
        ConfigChecker.warnIfMissing(plugin,"teleportation.default-tick-delay");
        this.defaultWaitTicks = plugin.getConfig().getInt("teleportation.default-tick-delay");
        ConfigChecker.warnIfMissing(plugin,"teleportation.default-can-move");
        this.defaultCanMove = plugin.getConfig().getBoolean("teleportation.default-can-move");
        this.text = text;
        this.spawnLocation = spawnLocation;
        this.teleportationQueue = teleportationQueue;
        this.teleportationSound = teleportationSound;
    }

    public void reload(){
        ConfigChecker.warnIfMissing(plugin,"teleportation.default-tick-delay");
        this.defaultWaitTicks = plugin.getConfig().getInt("teleportation.default-tick-delay");
        ConfigChecker.warnIfMissing(plugin,"teleportation.default-can-move");
        this.defaultCanMove = plugin.getConfig().getBoolean("teleportation.default-can-move");
    }

    public CompletableFuture<TeleportationOutCome> request(Player player){
        CompletableFuture<TeleportationOutCome> future = new CompletableFuture<>();
        player.teleportAsync(spawnLocation).thenAccept(b -> {
            if (b) {
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
                player.teleportAsync(spawnLocation).thenAccept(success -> {
                    if (success){
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
}
