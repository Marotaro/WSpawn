package ch.wondercraft.wonderspawn.teleportation;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportationAction implements Runnable {
    private final ConcurrentHashMap<Player,TeleportationInfo> playersTeleportationInfo = new ConcurrentHashMap<>();
    private final Location spawnLocation;
    private final TimerDisplay timerDisplay;

    public TeleportationAction(Location spawnLocation, TimerDisplay timerDisplay){
        this.spawnLocation = spawnLocation;
        this.timerDisplay = timerDisplay;
    }

    public CompletableFuture<TeleportationOutCome> putPlayerTeleportation(Player player,boolean acceptMove, int waitTicks){
        CompletableFuture<TeleportationOutCome> future = new CompletableFuture<>();
        if (hasAlreadyRequested(player)){
            playersTeleportationInfo.get(player).reset(player.getLocation(),acceptMove,waitTicks,future);
            return future;
        }
        playersTeleportationInfo.put(player,new TeleportationInfo(player,acceptMove,waitTicks,future));
        return future;
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
                teleportationInfo.updateTimer();
            });
        }
    }

    public TeleportationInfo getPlayerTeleportationInfo(Player player){
        return playersTeleportationInfo.get(player);
    }

    public boolean hasAlreadyRequested(Player player){
        return playersTeleportationInfo.containsKey(player);
    }

    public class TeleportationInfo{
        private Location playerLocation;
        private boolean acceptMove;
        private int baseTicks;
        private int elapsedTicks;
        private Bar timer;
        private CompletableFuture<TeleportationOutCome> outCome;

        private TeleportationInfo(Player player,boolean acceptMove, int baseTicks, CompletableFuture<TeleportationOutCome> outCome){
            this.playerLocation = player.getLocation();
            this.acceptMove = acceptMove;
            this.baseTicks = baseTicks;
            this.outCome = outCome;
            this.timer = new Bar(player);
        }

        private void reset(Location playerLocation, boolean acceptMove, int baseTicks, CompletableFuture<TeleportationOutCome> outCome){
            this.playerLocation = playerLocation;
            this.acceptMove = acceptMove;
            this.baseTicks = baseTicks;
            this.elapsedTicks = 0;
            timer.hide();
            timer = new Bar(timer.player);
            this.outCome.complete(TeleportationOutCome.CANCELED);
            this.outCome = outCome;
        }

        private void updateTimer(){
            elapsedTicks+=1;
        }

        public int remainingTicks(){
            return baseTicks-elapsedTicks;
        }

        public int getBaseTicks(){
            return baseTicks;
        }

        public Bar getTimer() {return timer;}

        public class Bar{
            private final Player player;
            private final BossBar bossBar;

            private Bar(Player player){
                this.player = player;
                this.bossBar = timerDisplay.create(baseTicks/20);
            }

            public void hide(){
                player.hideBossBar(bossBar);
            }

            public void show(){
                player.showBossBar(bossBar);
            }

            public void updateBarName(Component name){
                bossBar.name(name);
            }

            public void updateBarProgress(float progress){
                bossBar.progress(Math.max(0f,progress));
            }
        }
    }
}
