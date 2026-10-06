package ch.wondercraft.wonderspawn;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportationAction implements Runnable {
    private final ConcurrentHashMap<Player,TeleportationInfo> playersTeleportationInfo = new ConcurrentHashMap<>();
    private final Location spawnLocation;

    public TeleportationAction(Location spawnLocation){
        this.spawnLocation = spawnLocation;
    }

    public void putPlayerTeleportation(Player player,boolean acceptMove, int waitTicks, CompletableFuture<TeleportationOutCome> outCome){
        if (playersTeleportationInfo.contains(player)){
            playersTeleportationInfo.get(player).outCome.complete(TeleportationOutCome.CANCELED);
        }
        playersTeleportationInfo.put(player,new TeleportationInfo(player.getLocation(),acceptMove,waitTicks,outCome));
    }

    @Override
    public void run() {
        if (!playersTeleportationInfo.isEmpty()){
            playersTeleportationInfo.forEach((player, teleportationInfo) -> {
                if (!teleportationInfo.acceptMove && !player.getLocation().getBlock().equals(teleportationInfo.playerLocation.getBlock())){
                    playersTeleportationInfo.remove(player);
                    teleportationInfo.outCome.complete(TeleportationOutCome.MOVED);
                }
                if (teleportationInfo.elapsedTicks>= teleportationInfo.baseTicks){
                    if (!spawnLocation.isChunkLoaded()){
                        player.teleportAsync(spawnLocation).thenAccept(success->{
                            if (success)
                                teleportationInfo.outCome.complete(TeleportationOutCome.SUCCESS);
                            else
                                teleportationInfo.outCome.complete(TeleportationOutCome.ERROR);
                        });
                    } else {
                        if (player.teleport(spawnLocation))
                            teleportationInfo.outCome.complete(TeleportationOutCome.SUCCESS);
                        else
                            teleportationInfo.outCome.complete(TeleportationOutCome.ERROR);
                    }
                    playersTeleportationInfo.remove(player);
                }
                teleportationInfo.update();
            });
        }
    }

    private class TeleportationInfo{
        private final Location playerLocation;
        private final boolean acceptMove;
        private int baseTicks;
        private int elapsedTicks;
        private CompletableFuture<TeleportationOutCome> outCome;

        private TeleportationInfo(Location playerLocation,boolean acceptMove, int baseTicks, CompletableFuture<TeleportationOutCome> outCome){
            this.playerLocation = playerLocation;
            this.acceptMove = acceptMove;
            this.baseTicks = baseTicks;
            this.outCome = outCome;
        }

        private void update(){
            elapsedTicks+=1;
        }
    }
}
