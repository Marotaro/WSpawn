package ch.wondercraft.wonderspawn.events;

import ch.wondercraft.wonderspawn.text.Text;
import ch.wondercraft.wonderspawn.utils.ConfigChecker;
import ch.wondercraft.wonderspawn.utils.SimpleBooleanDefaultPerm;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

public class RespawnEvent implements Listener {
    private final Plugin plugin;
    private final Location spawnLocation;
    private final Text text;
    private boolean defaultIfMissing = true;
    private boolean defaultOverRideBedRespawn = true;
    private boolean defaultOverRideAnchorRespawn;

    public RespawnEvent(Plugin plugin, Location spawnLocation, Text text){
        this.plugin = plugin;
        this.spawnLocation = spawnLocation;
        this.text = text;
        plugin.getLogger().info("loading RespawnEvent");
        read(plugin);
    }

    private void read(Plugin plugin){
        ConfigChecker.warnIfMissing(plugin,"events.respawn.if-missing");
        this.defaultIfMissing = plugin.getConfig().getBoolean("events.respawn.if-missing");
        ConfigChecker.warnIfMissing(plugin,"events.respawn.override-bed");
        this.defaultOverRideBedRespawn = plugin.getConfig().getBoolean("events.respawn.override-bed");
        ConfigChecker.warnIfMissing(plugin,"events.respawn.override-anchor");
        this.defaultOverRideAnchorRespawn = plugin.getConfig().getBoolean("events.respawn.override-anchor");
    }

    public void reload(){
        plugin.getLogger().info("reloading RespawnEvent");
        read(plugin);
    }

    @EventHandler
    void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        if (event.isMissingRespawnBlock() && SimpleBooleanDefaultPerm.getter(player,"wspawn.event.respawn.block-missing",defaultIfMissing)){
            event.setRespawnLocation(spawnLocation);
            player.sendMessage(text.onRespawnBlockMissing());
        }
        else if (event.isBedSpawn() && SimpleBooleanDefaultPerm.getter(player,"wspawn.event.respawn.override-bed",defaultOverRideBedRespawn)){
            event.setRespawnLocation(spawnLocation);
            player.sendMessage(text.onRespawnOverrideBed());
        }
        else if (event.isAnchorSpawn() && SimpleBooleanDefaultPerm.getter(player,"wspawn.event.respawn.override-anchor",defaultOverRideAnchorRespawn)) {
            event.setRespawnLocation(spawnLocation);
            player.sendMessage(text.onRespawnOverrideAnchor());
        }
    }


}
