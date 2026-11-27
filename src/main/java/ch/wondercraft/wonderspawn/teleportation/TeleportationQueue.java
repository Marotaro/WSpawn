package ch.wondercraft.wonderspawn.teleportation;

import ch.wondercraft.wonderspawn.Reloadable;
import ch.wondercraft.wonderspawn.text.ComponentCreator;
import ch.wondercraft.wonderspawn.utils.ConfigChecker;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TeleportationQueue implements Reloadable {
    private final Plugin plugin;
    private final ComponentCreator componentCreator;
    private final TeleportationSound teleportationSound;
    private final Map<UUID,WaitInfo> uuidWaitInfoMap = new ConcurrentHashMap<>();
    private final Map<UUID, BarTimer> uuidBossBarMap = new ConcurrentHashMap<>();
    private boolean textTimerActive;
    private boolean bossBarTimerActive;
    private boolean bossBarTimerPerSecondUpdate;
    private BossBar.Color bossBarTimerColor;
    private Set<BossBar.Flag> bossBarTimerFlags;

    public TeleportationQueue(Plugin plugin, ComponentCreator componentCreator,TeleportationSound teleportationSound){
        this.plugin = plugin;
        this.componentCreator = componentCreator;
        this.teleportationSound = teleportationSound;
        load(plugin);
        Bukkit.getScheduler().runTaskTimer(plugin,this::tickAll,0L,1L);
    }

    @Override
    public void load(Plugin plugin) {
        Reloadable.super.load(plugin);
        read();
    }

    @Override
    public void reload(Plugin plugin) {
        Reloadable.super.reload(plugin);
        read();
    }

    private void tickAll(){
        for (UUID id : uuidWaitInfoMap.keySet()){
            WaitInfo waitInfo = uuidWaitInfoMap.get(id);
            WaitInfo.State state = waitInfo.tick();
            uuidBossBarMap.get(id).tick(waitInfo.remainingTick(), waitInfo.waitTicks, waitInfo.canMove);
            if (state== WaitInfo.State.DONE){
                uuidWaitInfoMap.remove(id);
                waitInfo.player.hideBossBar(uuidBossBarMap.remove(id).bossBar);
            }
        }
    }

    public CompletableFuture<WaitInfo.WaitOutCome> add(Player player, int waitTicks, boolean canMove){
        CompletableFuture<WaitInfo.WaitOutCome> waitOutCome = new CompletableFuture<>();
        if (uuidWaitInfoMap.containsKey(player.getUniqueId())){
            teleportationSound.play(player, TeleportationSound.teleportationSoundType.CANCELED);
            componentCreator.send(player,componentCreator.spawnCanceledPromise(player,waitTicks/20));
            uuidWaitInfoMap.get(player.getUniqueId()).update(waitTicks,canMove,waitOutCome);
            uuidBossBarMap.get(player.getUniqueId()).tick(waitTicks,waitTicks,canMove);
        } else {
            teleportationSound.play(player, TeleportationSound.teleportationSoundType.INITIAL);
            componentCreator.send(player,componentCreator.spawnPromise(player,waitTicks/20));
            uuidWaitInfoMap.put(player.getUniqueId(), new WaitInfo(player,waitTicks,canMove,waitOutCome));
            uuidBossBarMap.put(player.getUniqueId(),new BarTimer(player,waitTicks, canMove));
        }
        if (bossBarTimerActive) player.showBossBar(uuidBossBarMap.get(player.getUniqueId()).bossBar);
        return waitOutCome;
    }

    private void read(){
        ConfigChecker.warnIfMissing(plugin,"timer.chat.active");
        textTimerActive = plugin.getConfig().getBoolean("timer.chat.active");
        ConfigChecker.warnIfMissing(plugin,"timer.bossbar.active");
        bossBarTimerActive = plugin.getConfig().getBoolean("timer.bossbar.active");
        ConfigChecker.warnIfMissing(plugin,"timer.bossbar.per-second-update");
        bossBarTimerPerSecondUpdate = plugin.getConfig().getBoolean("timer.bossbar.per-second-update");
        ConfigChecker.warnIfMissing(plugin,"timer.bossbar.color");
        bossBarTimerColor = BossBar.Color.valueOf(plugin.getConfig().getString("timer.bossbar.color"));
        ConfigChecker.warnIfMissing(plugin,"timer.bossbar.flags");
        bossBarTimerFlags = plugin.getConfig().getStringList("timer.bossbar.flags").stream().map(BossBar.Flag::valueOf).collect(Collectors.toSet());
    }

    public final class WaitInfo{
        public enum WaitOutCome {SUCCESS, MOVED, CANCELED}
        public enum State {RUNNING, DONE}
        private final Player player;
        private Location baseLocation;
        private int waitTicks;
        private int elapsedTicks;
        private boolean canMove;
        private CompletableFuture<WaitOutCome> waitOutCome;

        public WaitInfo(Player player, int waitTicks, boolean canMove,
                        CompletableFuture<WaitOutCome> waitOutCome){
            this.player = player;
            this.baseLocation = player.getLocation();
            this.waitTicks = waitTicks;
            this.elapsedTicks = 0;
            this.canMove = canMove;
            this.waitOutCome = waitOutCome;
        }

        public State tick(){
            if (!player.isOnline()){
                waitOutCome.complete(WaitOutCome.CANCELED);
                return State.DONE;
            }
            if (elapsedTicks!=0 && remainingTick()%20==0 && remainingTick()!=0){
                if (textTimerActive)
                    componentCreator.send(player,componentCreator.chat(player,remainingTick()/20,canMove));
                teleportationSound.play(player, TeleportationSound.teleportationSoundType.INTERVAL);
            }
            if (remainingTick()==0){
                waitOutCome.complete(WaitOutCome.SUCCESS);
                return State.DONE;
            } else {
                if (!canMove && !baseLocation.getBlock().equals(player.getLocation().getBlock())){
                    waitOutCome.complete(WaitOutCome.MOVED);
                    return State.DONE;
                } else {
                    elapsedTicks++;
                    return State.RUNNING;
                }
            }
        }

        public void update(int waitTicks, boolean canMove, CompletableFuture<WaitOutCome> waitOutCome){
            this.waitOutCome.complete(WaitOutCome.CANCELED);
            this.baseLocation = player.getLocation();
            this.waitTicks = waitTicks;
            this.elapsedTicks = 0;
            this.canMove = canMove;
            this.waitOutCome = waitOutCome;
        }

        public int remainingTick(){
            return waitTicks-elapsedTicks;
        }
    }

    private final class BarTimer{
        private Player player;
        private BossBar bossBar;

        private BarTimer(Player player, int waitTicks, boolean canMove){
            this.player = player;
            this.bossBar = BossBar.bossBar(componentCreator.bar(player,waitTicks/20,canMove),1f,bossBarTimerColor, BossBar.Overlay.PROGRESS,bossBarTimerFlags);
        }

        private void tick(int remainingTicks, int waitTicks, boolean canMove){
            if (remainingTicks%20==0)
                bossBar.name(componentCreator.bar(player,remainingTicks/20,canMove));
            if (!bossBarTimerPerSecondUpdate || remainingTicks % 20 == 0){
                bossBar.progress(Math.max(0,(float)remainingTicks/waitTicks));
            }
            if (remainingTicks<=0)
                player.hideBossBar(bossBar);
        }
    }
}
