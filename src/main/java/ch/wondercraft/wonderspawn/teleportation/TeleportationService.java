package ch.wondercraft.wonderspawn.teleportation;

import ch.wondercraft.wonderspawn.text.Text;
import ch.wondercraft.wonderspawn.utils.ConfigChecker;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;

public class TeleportationService {
    private static enum REASON {COMMAND_SELF,EVENT}
    private Location spawnLocation;
    private Text text;
    private Plugin plugin;
    private TeleportationQueue teleportationQueue;
    private int defaultWaitTicks;
    private boolean defaultCanMove;

    public TeleportationService(Plugin plugin, Text text, Location spawnLocation, TeleportationQueue teleportationQueue){
        this.plugin = plugin;
        ConfigChecker.warnIfMissing(plugin,"teleportation.default-tick-delay");
        this.defaultWaitTicks = plugin.getConfig().getInt("teleportation.default-tick-delay");
        ConfigChecker.warnIfMissing(plugin,"teleportation.default-can-move");
        this.defaultCanMove = plugin.getConfig().getBoolean("teleportation.default-can-move");
        this.text = text;
        this.spawnLocation = spawnLocation;
        this.teleportationQueue = teleportationQueue;
    }

    public void reload(){
        ConfigChecker.warnIfMissing(plugin,"teleportation.default-tick-delay");
        this.defaultWaitTicks = plugin.getConfig().getInt("teleportation.default-tick-delay");
        ConfigChecker.warnIfMissing(plugin,"teleportation.default-can-move");
        this.defaultCanMove = plugin.getConfig().getBoolean("teleportation.default-can-move");
    }

    public CompletableFuture<Boolean> request(Player player){
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        player.teleportAsync(spawnLocation).thenAccept(b->{
            if (b){
                player.sendMessage(text.spawnConfirmation());
                future.complete(true);
            } else {
                //todo change that
                player.sendMessage("bof");
                future.complete(false);
            }
        });
        return future;
    }

    public CompletableFuture<Boolean> request(Player player, int waitTicks, boolean canMove){
        if (waitTicks == 0){
            return request(player);
        }
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        teleportationQueue.add(player,waitTicks,canMove).thenAccept(waitOutCome -> {
            if (waitOutCome== TeleportationQueue.WaitInfo.WaitOutCome.SUCCESS){
                player.teleportAsync(spawnLocation).thenAccept(b ->{
                    if (b){
                        player.sendMessage(text.spawnConfirmation());
                        future.complete(true);
                    } else {
                        player.sendMessage("bof");
                        future.complete(false);
                    }
                });
            } else if (waitOutCome == TeleportationQueue.WaitInfo.WaitOutCome.MOVED) {
                player.sendMessage(text.spawnPlayerMoved());
            }
            if (waitOutCome != TeleportationQueue.WaitInfo.WaitOutCome.SUCCESS)
                future.complete(false);
        });
        return future;
    }
}
