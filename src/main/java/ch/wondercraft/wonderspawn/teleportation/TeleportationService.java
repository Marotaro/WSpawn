package ch.wondercraft.wonderspawn.teleportation;

import ch.wondercraft.wonderspawn.teleportation.sounds.SoundNotification;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CompletableFuture;

public class TeleportationService {
    private final Plugin plugin;
    private final TeleportationAction teleportationAction;
    private final SoundNotification soundNotification;
    private final TimerDisplay timerDisplay;
    private final boolean tickBarUpdate = true;

    public TeleportationService(Plugin plugin, Location spawnLocation,
                                SoundNotification soundNotification, TimerDisplay timerDisplay){
        this.plugin = plugin;
        this.teleportationAction = new TeleportationAction(spawnLocation);
        this.soundNotification = soundNotification;
        this.timerDisplay = timerDisplay;
        Bukkit.getScheduler().runTaskTimer(plugin,teleportationAction,1L,1L);
    }

    public CompletableFuture<TeleportationOutCome> requestTeleportation(Player player, int waitTicks, boolean canMove){
        if (waitTicks > 0){
            soundNotification.INITIAL.play(player);
        }
        BossBar bar = timerDisplay.isActive()? timerDisplay.create() : null;
        if (bar != null) player.showBossBar(bar);

        CompletableFuture<TeleportationOutCome> future = teleportationAction.putPlayerTeleportation(player, canMove, waitTicks);

        BukkitTask updateBar = Bukkit.getScheduler().runTaskTimer(plugin,()->{
            TeleportationAction.TeleportationInfo teleportationInfo = teleportationAction.getPlayerTeleportationInfo(player);
            if (teleportationInfo!=null&&bar!=null){
                bar.progress(Math.max(0f,(float) teleportationInfo.remainingTicks()/teleportationInfo.getBaseTicks()));
            }
        },0L, tickBarUpdate? 1L:20L);
        BukkitTask soundsInterval = Bukkit.getScheduler().runTaskTimer(plugin,()->{
            soundNotification.INTERVAL.play(player);
        },20L,20L);
        future.thenAccept(teleportationOutCome -> {
            Bukkit.getScheduler().runTask(plugin,()->{
                updateBar.cancel();
                soundsInterval.cancel();
                if (bar != null) player.hideBossBar(bar);
                switch (teleportationOutCome){
                    case SUCCESS -> soundNotification.FINAL.play(player);
                    case MOVED, CANCELED, ERROR -> soundNotification.ERROR.play(player);
                }
            });
        });
        return future;
    }
}
