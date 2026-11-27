package ch.wondercraft.wonderspawn.events;

import ch.wondercraft.wonderspawn.Reloadable;
import ch.wondercraft.wonderspawn.teleportation.TeleportationOutCome;
import ch.wondercraft.wonderspawn.teleportation.TeleportationService;
import ch.wondercraft.wonderspawn.text.ComponentCreator;
import ch.wondercraft.wonderspawn.utils.ConfigChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class JoinEvent implements Listener, Reloadable {
    private final Plugin plugin;
    private final TeleportationService teleportationService;
    private final ComponentCreator componentCreator;
    boolean teleportOnFirstJoin;
    boolean teleportOnJoin;

    public JoinEvent(Plugin plugin, TeleportationService teleportationService, ComponentCreator componentCreator){
        this.plugin = plugin;
        this.teleportationService = teleportationService;
        this.componentCreator = componentCreator;
        load(plugin);
    }
    private void read(){
        String onFirstJoin = "events.on-first-join";
        ConfigChecker.warnIfMissing(plugin,onFirstJoin);
        this.teleportOnFirstJoin = plugin.getConfig().getBoolean(onFirstJoin);
        String onJoin = "events.on-join";
        ConfigChecker.warnIfMissing(plugin,onJoin);
        this.teleportOnJoin = plugin.getConfig().getBoolean(onJoin);
    }

    @Override
    public void load(Plugin plugin) {
        Reloadable.super.load(plugin);
        read();
    }

    @Override
    public void reload(Plugin plugin){
        Reloadable.super.reload(plugin);
        read();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) {
            if (teleportOnJoin) {
                teleportationService.request(player).thenAccept(teleportationOutCome -> {
                    if (teleportationOutCome == TeleportationOutCome.SUCCESS)
                        componentCreator.send(player, componentCreator.onJoinTeleportation(player));
                });
            }
        } else {
            if (teleportOnFirstJoin) {
                teleportationService.request(player).thenAccept(teleportationOutCome -> {
                    if (teleportationOutCome == TeleportationOutCome.SUCCESS)
                        componentCreator.send(player, componentCreator.onFirstJoinTeleportation(player));
                });
            }
        }
    }
}
