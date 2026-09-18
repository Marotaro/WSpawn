package ch.wondercraft.wonderspawn.waiters;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpawnWaiterRegistery implements Runnable{
    private final Plugin plugin;
    private final ConcurrentHashMap<UUID,Integer> waitingMap = new ConcurrentHashMap<>();

    public SpawnWaiterRegistery(Plugin plugin){
        this.plugin = plugin;
    }

    private void register(UUID uuid, Integer integer){
        waitingMap.put(uuid,integer);
    }

    public void of(Player player, int i){
        //SpawnWaiter
        register(player.getUniqueId(),i);
    }
    
    public void forPlayer(Player player){
        //todo add custom time with permission or config (do that in the spawnWaiter in there)
        register(player.getUniqueId(),1);
    }

    public void unregister(UUID uuid){
        waitingMap.remove(uuid);
    }

    @Override
    public void run(){
        for (UUID id : waitingMap.keySet()){
            //run check
        }
    }


}
