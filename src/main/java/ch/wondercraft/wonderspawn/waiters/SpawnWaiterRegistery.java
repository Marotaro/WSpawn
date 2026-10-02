package ch.wondercraft.wonderspawn.waiters;


import ch.wondercraft.wonderspawn.utils.PermissionFinder;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SpawnWaiterRegistery implements Runnable{
    private final Plugin plugin;
    private final ConcurrentHashMap<UUID, SpawnDemand> spawnDemands = new ConcurrentHashMap<>();
    private final YamlConfiguration configuration;
    private final SoundSetting soundSetting = new SoundSetting();

    private final int TICK_IN_SECOND = 20;

    public SpawnWaiterRegistery(Plugin plugin){
        this.plugin = plugin;
        this.configuration = loadConfig();
    }

    public YamlConfiguration loadConfig(){
        File file = new File(plugin.getDataFolder(),"config.yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    public CompletableFuture<DemandOutCome> addDemand(Player player){
        if (player.hasPermission("wspawn.teleportdelay.bypass")){
            return CompletableFuture.completedFuture(DemandOutCome.ACCEPT);
        }
        int time = configuration.getInt("teleportation.delay")*TICK_IN_SECOND;
        int perm = PermissionFinder.upper("wspawn.teleportdelay.time", player, 100);
        if (perm > -1) {
            time = perm * TICK_IN_SECOND;
        }
        if (time == 0) {
            return CompletableFuture.completedFuture(DemandOutCome.ACCEPT);
        }
        CompletableFuture<DemandOutCome> future = new CompletableFuture<>();
        int finalTime = time;
        spawnDemands.compute(player.getUniqueId(),(id, sd)->{
           if (sd!=null){
               sd.future.complete(DemandOutCome.DEMANDS);
           }
           boolean fog;
           if (!player.isPermissionSet("wspawn.teleport.fog"))
               fog = configuration.getBoolean("teleportation.fog");
           else {
               fog = player.hasPermission("wspawn.teleport.fog");
           }
           SpawnDemand spawnDemand = new SpawnDemand(finalTime, player.getLocation(), future, fog);
           if (configuration.getBoolean("teleportation.timer-display.active"))
               player.showBossBar(spawnDemand.timerDisplay);
           if (soundSetting.active("initial-sound"))
               player.playSound(player.getLocation(), soundSetting.name("initial-sound"),soundSetting.volume("initial-sound"), soundSetting.pitch("initial-sound"));
           return spawnDemand ;
        });
        return future;
    }

    @Override
    public void run() {
        boolean perSecondSound = soundSetting.active("per-second-sound");
        String perSecondSoundName = soundSetting.name("per-second-sound");
        float perSecondSoundVolume = soundSetting.volume("per-second-sound");
        float perSecondSoundPitch = soundSetting.pitch("per-second-sound");
        boolean movedSound = soundSetting.active("moved-sound");
        String movedSoundName = soundSetting.name("moved-sound");
        float movedSoundVolume = soundSetting.volume("moved-sound");
        float movedSoundPitch = soundSetting.pitch("moved-sound");
        for (UUID uuid : spawnDemands.keySet()){
            Player player = Bukkit.getPlayer(uuid);
            SpawnDemand spawnDemand = spawnDemands.get(uuid);
            if (player==null){
                spawnDemands.remove(uuid);
                spawnDemand.future.complete(DemandOutCome.QUIT);
            } else {
                String perm = "wspawn.teleportation.can-move";
                boolean canMove = player.isPermissionSet(perm)
                        ? player.hasPermission(perm)
                        : configuration.getBoolean("teleportation.can-move");
                if (spawnDemand.time <= 0) {
                    spawnDemand.future.complete(DemandOutCome.ACCEPT);
                } else if (!canMove&&!player.getLocation().getBlock().equals(spawnDemand.location.getBlock())){
                    if (movedSound)
                        player.playSound(player.getLocation(),movedSoundName,movedSoundVolume,movedSoundPitch);
                    spawnDemand.future.complete(DemandOutCome.MOVED);
                } else {
                    if (perSecondSound&&spawnDemand.time!=spawnDemand.baseTime&&spawnDemand.time%TICK_IN_SECOND==0)
                        player.playSound(player.getLocation(),perSecondSoundName,perSecondSoundVolume,perSecondSoundPitch);
                    spawnDemand.updateTime(-1);
                    spawnDemand.updateTimerDisplay();
                    continue;
                }
                spawnDemands.remove(uuid);
                player.hideBossBar(spawnDemand.timerDisplay);
            }
        }
    }


    private final class SpawnDemand {
        private static MiniMessage miniMessage = MiniMessage.builder().strict(false).build();
        private int time;
        private int baseTime;
        private final Location location;
        private final CompletableFuture<DemandOutCome> future;
        private final BossBar timerDisplay;
        private SpawnDemand(int time, Location location, CompletableFuture<DemandOutCome> future, boolean fog){
            this.time = time;
            this.baseTime = time;
            this.location = location;
            this.future = future;
            BossBar.Color color;
            try {
                color = BossBar.Color.valueOf(configuration.getString("teleportation.timer-display.color"));
            } catch (IllegalArgumentException e) {
                color = BossBar.Color.RED;
            }
            if (fog){
                this.timerDisplay = BossBar.bossBar(this::makeComponent, 1f, color, BossBar.Overlay.PROGRESS, Set.of(BossBar.Flag.CREATE_WORLD_FOG, BossBar.Flag.DARKEN_SCREEN));
            } else
                this.timerDisplay = BossBar.bossBar(this::makeComponent,1f, color, BossBar.Overlay.PROGRESS);
        }

        private Component makeComponent() {
            TagResolver tagResolver = TagResolver.resolver(
                    Placeholder.unparsed("time",String.valueOf(time/TICK_IN_SECOND+1))
            );
            String title = configuration.getString("teleportation.timer-display.text");
            if (title==null){
                title = "title not found";
            }
            return miniMessage.deserialize(title,tagResolver);
        }

        private void updateTime(int i){
            this.time += i;
        }

        private void updateTimerDisplay(){
            timerDisplay.name(this::makeComponent);
            timerDisplay.progress((float) (time)/baseTime);
        }
    };

    private final class SoundSetting {
        private boolean active(String name){
            return configuration.getBoolean("teleportation."+name+".active");
        }
        private String name(String name){
            return configuration.getString("teleportation."+name+".name");
        }
        private float volume(String name){
            return (float)configuration.getDouble("teleportation."+name+".volume");
        }
        private float pitch(String name){
            return (float)configuration.getDouble("teleportation."+name+".pitch");
        }
    }

    public enum DemandOutCome{
        ACCEPT, MOVED, DEMANDS, QUIT;
    }
}
