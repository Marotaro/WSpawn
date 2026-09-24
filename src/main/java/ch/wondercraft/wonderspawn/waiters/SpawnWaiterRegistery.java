package ch.wondercraft.wonderspawn.waiters;


import ch.wondercraft.wonderspawn.utils.PermissionFinder;
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
        if (player.hasPermission("wspawn.teleportdelay.time")){
            time = PermissionFinder.upper("wspawn.teleportdelay.time",player,100)*20;
        }
        CompletableFuture<DemandOutCome> future = new CompletableFuture<>();
        int finalTime = time;
        spawnDemands.compute(player.getUniqueId(),(id, sd)->{
           if (sd!=null){
               sd.future.complete(DemandOutCome.DEMANDS);
           }
           return new SpawnDemand(finalTime, player.getLocation(), future);
        });
        return future;
    }

    @Override
    public void run() {
        for (UUID uuid : spawnDemands.keySet()){
            SpawnDemand spawnDemand = spawnDemands.get(uuid);
            Bukkit.getLogger().info("reste:"+spawnDemand.time);
            if (spawnDemand.time <= 20){
                spawnDemands.remove(uuid);
                spawnDemand.future.complete(DemandOutCome.ACCEPT);
                continue;
            }
            else {
                Player player = Bukkit.getPlayer(uuid);
                if (player==null){
                    spawnDemands.remove(uuid);
                    spawnDemand.future.complete(DemandOutCome.QUIT);
                    continue;
                } else {
                    player.sendMessage("il rest :"+spawnDemand.time/20);
                    if (!player.getLocation().getBlock().equals(spawnDemand.location.getBlock())){
                        spawnDemands.remove(uuid);
                        spawnDemand.future.complete(DemandOutCome.MOVED);
                    } else {
                        spawnDemand.updateTime(-1);

                    }
                }
            }
        }
    }


    private final class SpawnDemand {
        private int time;
        private final Location location;
        private final CompletableFuture<DemandOutCome> future;
        private SpawnDemand(int time, Location location, CompletableFuture<DemandOutCome> future){
            this.time = time;
            this.location = location;
            this.future = future;
        }
        private void updateTime(int i){
            this.time += i;
        }

    };

    public enum DemandOutCome{
        ACCEPT, MOVED, DEMANDS, QUIT;
    }
}
