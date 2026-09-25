package ch.wondercraft.wonderspawn.waiters;


import ch.wondercraft.wonderspawn.utils.PermissionFinder;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SpawnWaiterRegistery implements Runnable{
    private final Plugin plugin;
    private final ConcurrentHashMap<UUID, SpawnDemand> spawnDemands = new ConcurrentHashMap<>();
    private final YamlConfiguration configuration;

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
        int time = configuration.getInt("teleportdelay")*20;
        int perm = PermissionFinder.upper("wspawn.teleportdelay.time", player, 100);
        if (perm > -1) {
            time = perm * 20;
        }
        if (time == 0)
            return CompletableFuture.completedFuture(DemandOutCome.ACCEPT);
        CompletableFuture<DemandOutCome> future = new CompletableFuture<>();
        int finalTime = time;
        spawnDemands.compute(player.getUniqueId(),(id, sd)->{
           if (sd!=null){
               sd.future.complete(DemandOutCome.DEMANDS);
           }
           SpawnDemand spawnDemand = new SpawnDemand(finalTime, player.getLocation(), future);
           player.showBossBar(spawnDemand.timerDisplay);
           return spawnDemand ;
        });
        return future;
    }

    @Override
    public void run() {
        for (UUID uuid : spawnDemands.keySet()){
            Player player = Bukkit.getPlayer(uuid);
            SpawnDemand spawnDemand = spawnDemands.get(uuid);
            if (player==null){
                spawnDemands.remove(uuid);
                spawnDemand.future.complete(DemandOutCome.QUIT);
                continue;
            } else {
                if (spawnDemand.time <= 0) {
                    spawnDemands.remove(uuid);
                    spawnDemand.timerDisplay.removeViewer(player);
                    player.hideBossBar(spawnDemand.timerDisplay);
                    spawnDemand.future.complete(DemandOutCome.ACCEPT);
                    continue;
                } else if (!player.getLocation().getBlock().equals(spawnDemand.location.getBlock())){
                    spawnDemands.remove(uuid);
                    spawnDemand.timerDisplay.removeViewer(player);
                    spawnDemand.future.complete(DemandOutCome.MOVED);
                } else {
                    spawnDemand.updateTime(-1);
                    spawnDemand.updateTimerDisplay();
                }
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
        private SpawnDemand(int time, Location location, CompletableFuture<DemandOutCome> future){
            this.time = time;
            this.baseTime = time;
            this.location = location;
            this.future = future;
            this.timerDisplay = BossBar.bossBar(this::makeComponent,1f, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);
        }

        private Component makeComponent() {
            TagResolver tagResolver = TagResolver.resolver(
                    Placeholder.unparsed("time",String.valueOf(time/20+1))
            );
            return miniMessage.deserialize("<dark_purple>Il reste</dark_purple> <gold><time></gold> <dark_purple>secondes</dark_purple>",tagResolver);
        }

        private void updateTime(int i){
            this.time += i;
        }

        private void updateTimerDisplay(){
            timerDisplay.name(this::makeComponent);
            timerDisplay.progress((float) (time)/baseTime);
        }

    };

    public enum DemandOutCome{
        ACCEPT, MOVED, DEMANDS, QUIT;
    }
}
