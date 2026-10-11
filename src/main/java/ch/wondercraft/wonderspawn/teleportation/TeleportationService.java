package ch.wondercraft.wonderspawn.teleportation;

import ch.wondercraft.wonderspawn.teleportation.sounds.SoundNotification;
import ch.wondercraft.wonderspawn.text.Text;
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
    private final Text text;

    public TeleportationService(Plugin plugin, Location spawnLocation,
                                SoundNotification soundNotification, TimerDisplay timerDisplay,
                                Text text){
        this.plugin = plugin;
        this.teleportationAction = new TeleportationAction(spawnLocation);
        this.soundNotification = soundNotification;
        this.timerDisplay = timerDisplay;
        this.text = text;
        Bukkit.getScheduler().runTaskTimer(plugin,teleportationAction,1L,1L);
    }

    public CompletableFuture<TeleportationOutCome> requestTeleportation(Player player, int waitTicks, boolean canMove){
        CompletableFuture<TeleportationOutCome> future = teleportationAction.putPlayerTeleportation(player, canMove, waitTicks);
        if (waitTicks > 0) {
            soundNotification.INITIAL.play(player);
            player.sendMessage(text.spawnPromise(waitTicks));
            BossBar bar = timerDisplay.isActive()? timerDisplay.create(waitTicks/20) : null;
            if (bar != null) player.showBossBar(bar);
            BukkitTask textUpdate = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                TeleportationAction.TeleportationInfo teleportationInfo = teleportationAction.getPlayerTeleportationInfo(player);
                if (teleportationInfo != null) {
                    int remainingSecond = (int) Math.round((double) teleportationInfo.remainingTicks() / 20);
                    if (bar != null) {
                        bar.name(text.bar(remainingSecond));
                    }
                    player.sendMessage(text.chat(remainingSecond));
                }

            }, 0L, 20L);
            BukkitTask barUpdate = Bukkit.getScheduler().runTaskTimer(plugin,()->{
                TeleportationAction.TeleportationInfo teleportationInfo = teleportationAction.getPlayerTeleportationInfo(player);
                if (teleportationInfo != null){
                    if (bar != null){
                        bar.progress(Math.max(0f, (float) teleportationInfo.remainingTicks() / teleportationInfo.getBaseTicks()));
                    }
                }
            }, 0L, timerDisplay.isPerSecondUpdate()? 20L:1L);
            BukkitTask soundsInterval = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (teleportationAction.getPlayerTeleportationInfo(player).remainingTicks() > 5)
                    soundNotification.INTERVAL.play(player);
            }, 20L, 20L);
            future.thenAccept(teleportationOutCome -> {
                Bukkit.getScheduler().runTask(plugin,()->{
                    textUpdate.cancel();
                    barUpdate.cancel();
                    soundsInterval.cancel();
                    if (bar != null) player.hideBossBar(bar);
                    switch (teleportationOutCome){
                        case SUCCESS -> soundNotification.FINAL.play(player);
                        case MOVED, CANCELED, ERROR -> soundNotification.ERROR.play(player);
                    }
                });
            });
        } else
            future.thenAccept(teleportationOutCome -> {
               switch (teleportationOutCome){
                   case SUCCESS -> soundNotification.FINAL.play(player);
                   case MOVED, CANCELED, ERROR -> soundNotification.ERROR.play(player);
               }
            });

        return future;
    }
}
