package ch.wondercraft.wonderspawn.events;

import ch.wondercraft.wonderspawn.teleportation.TeleportationOutCome;
import ch.wondercraft.wonderspawn.teleportation.TeleportationService;
import ch.wondercraft.wonderspawn.text.Text;
import ch.wondercraft.wonderspawn.utils.ConfigChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class JoinEvent implements Listener {
    private final Plugin plugin;
    private final TeleportationService teleportationService;
    private final Text text;
    boolean teleportOnFirstJoin;
    boolean teleportOnJoin;

    public JoinEvent(Plugin plugin, TeleportationService teleportationService, Text text){
        this.plugin = plugin;
        plugin.getLogger().info("loading JoinEvent");
        this.teleportationService = teleportationService;
        this.text = text;
        read(plugin);
    }
    private void read(Plugin plugin){
        String onFirstJoin = "events.on-first-join";
        ConfigChecker.warnIfMissing(plugin,onFirstJoin);
        this.teleportOnFirstJoin = plugin.getConfig().getBoolean(onFirstJoin);
        String onJoin = "events.on-join";
        ConfigChecker.warnIfMissing(plugin,onJoin);
        this.teleportOnJoin = plugin.getConfig().getBoolean(onJoin);
    }

    public void reload(){
        plugin.getLogger().info("reloading JoinEvent");
        read(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) {
            if (teleportOnJoin) {
                teleportationService.request(player).thenAccept(teleportationOutCome -> {
                    if (teleportationOutCome == TeleportationOutCome.SUCCESS)
                        text.send(player, text.onJoinTeleportation());
                });
            }
        } else {
            if (teleportOnFirstJoin) {
                teleportationService.request(player).thenAccept(teleportationOutCome -> {
                    if (teleportationOutCome == TeleportationOutCome.SUCCESS)
                        text.send(player, text.onFirstJoinTeleportation());
                });
            }
        }
    }
}
