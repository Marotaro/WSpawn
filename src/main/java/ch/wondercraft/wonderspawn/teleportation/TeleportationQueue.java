package ch.wondercraft.wonderspawn.teleportation;

import ch.wondercraft.wonderspawn.text.Text;
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

public class TeleportationQueue{
    private final Plugin plugin;
    private final Text text;
    private final TeleportationSound teleportationSound;
    private final Map<UUID,WaitInfo> uuidWaitInfoMap = new ConcurrentHashMap<>();
    private final Map<UUID, BarTimer> uuidBossBarMap = new ConcurrentHashMap<>();
    private boolean textTimerActive;
    private boolean bossBarTimerActive;
    private boolean bossBarTimerPerSecondUpdate;
    private BossBar.Color bossBarTimerColor;
    private Set<BossBar.Flag> bossBarTimerFlags;

    private TeleportationQueue(Plugin plugin, Text text,TeleportationSound teleportationSound,Data data){
        this.plugin = plugin;
        this.text = text;
        this.teleportationSound = teleportationSound;
        apply(data);
        Bukkit.getScheduler().runTaskTimer(plugin,this::tickAll,0L,1L);
    }

    private void tickAll(){
        for (UUID id : uuidWaitInfoMap.keySet()){
            WaitInfo waitInfo = uuidWaitInfoMap.get(id);
            WaitInfo.State state = waitInfo.tick();
            uuidBossBarMap.get(id).tick(waitInfo.remainingTick(), waitInfo.waitTicks);
            if (state== WaitInfo.State.DONE){
                uuidWaitInfoMap.remove(id);
                waitInfo.player.hideBossBar(uuidBossBarMap.remove(id).bossBar);
            }
        }
    }

    public CompletableFuture<WaitInfo.WaitOutCome> add(Player player, int waitTicks, boolean canMove){
        CompletableFuture<WaitInfo.WaitOutCome> waitOutCome = new CompletableFuture<>();
        if (uuidWaitInfoMap.containsKey(player.getUniqueId())){
            teleportationSound.play(player, TeleportationSound.Type.CANCELED);
            player.sendMessage(text.spawnCanceledPromise(waitTicks/20));
            uuidWaitInfoMap.get(player.getUniqueId()).update(waitTicks,canMove,waitOutCome);
            uuidBossBarMap.get(player.getUniqueId()).tick(waitTicks,waitTicks);
        } else {
            teleportationSound.play(player, TeleportationSound.Type.INITIAL);
            player.sendMessage(text.spawnPromise(waitTicks/20));
            uuidWaitInfoMap.put(player.getUniqueId(), new WaitInfo(player,waitTicks,canMove,waitOutCome));
            uuidBossBarMap.put(player.getUniqueId(),new BarTimer(player,waitTicks));
        }
        if (bossBarTimerActive) player.showBossBar(uuidBossBarMap.get(player.getUniqueId()).bossBar);
        return waitOutCome;
    }

    private void apply(Data data){
        this.textTimerActive = data.textTimerActive;
        this.bossBarTimerActive = data.bossBarTimerActive;
        this.bossBarTimerPerSecondUpdate = data.bossBarTimerPerSecondUpdate;
        this.bossBarTimerColor = data.bossBarTimerColor;
        this.bossBarTimerFlags = data.bossBarTimerFlags;
    }

    private static Data read(Plugin plugin){
        ConfigChecker.warnIfMissing(plugin,"timer.text.active");
        boolean textTimerActive = plugin.getConfig().getBoolean("timer.text.active");
        ConfigChecker.warnIfMissing(plugin,"timer.bossbar.active");
        boolean bossBarTimerActive = plugin.getConfig().getBoolean("timer.bossbar.active");
        ConfigChecker.warnIfMissing(plugin,"timer.bossbar.per-second-update");
        boolean bossBarTimerPerSecondUpdate = plugin.getConfig().getBoolean("timer.bossbar.per-second-update");
        ConfigChecker.warnIfMissing(plugin,"timer.bossbar.color");
        BossBar.Color bossBarTimerColor = BossBar.Color.valueOf(plugin.getConfig().getString("timer.bossbar.color"));
        ConfigChecker.warnIfMissing(plugin,"timer.bossbar.flags");
        Set<BossBar.Flag> bossBarTimerFlags = plugin.getConfig().getStringList("timer.bossbar.flags").stream().map(BossBar.Flag::valueOf).collect(Collectors.toSet());
        return new Data(textTimerActive,bossBarTimerActive,bossBarTimerPerSecondUpdate,bossBarTimerColor,bossBarTimerFlags);
    }

    public static TeleportationQueue load(Plugin plugin, Text text, TeleportationSound teleportationSound){
        return new TeleportationQueue(plugin,text,teleportationSound,read(plugin));
    }

    public void reload(){
        apply(read(plugin));
    }

    private record Data(boolean textTimerActive, boolean bossBarTimerActive,
                        boolean bossBarTimerPerSecondUpdate, BossBar.Color bossBarTimerColor,
                        Set<BossBar.Flag> bossBarTimerFlags){}
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
                    player.sendMessage(text.chat(remainingTick()/20));
                teleportationSound.play(player, TeleportationSound.Type.INTERVAL);
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

        private BarTimer(Player player, int waitTicks){
            this.player = player;
            this.bossBar = BossBar.bossBar(text.bar(waitTicks/20),1f,bossBarTimerColor, BossBar.Overlay.PROGRESS,bossBarTimerFlags);
        }

        private void tick(int remainingTicks, int waitTicks){
            if (!bossBarTimerPerSecondUpdate || remainingTicks % 20 == 0){
                bossBar.progress(Math.max(0,(float)remainingTicks/waitTicks));
                bossBar.name(text.bar(remainingTicks/20));
            }
            if (remainingTicks<=0)
                player.hideBossBar(bossBar);
        }
    }
}
