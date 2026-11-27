package ch.wondercraft.wonderspawn.events;

import ch.wondercraft.wonderspawn.Reloadable;
import ch.wondercraft.wonderspawn.teleportation.TeleportationOutCome;
import ch.wondercraft.wonderspawn.teleportation.TeleportationService;
import ch.wondercraft.wonderspawn.teleportation.TeleportationSound;
import ch.wondercraft.wonderspawn.text.ComponentCreator;
import ch.wondercraft.wonderspawn.utils.ConfigChecker;
import ch.wondercraft.wonderspawn.utils.SimpleBooleanDefaultPerm;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

public class VoidEvent implements Listener, Reloadable {
    Plugin plugin;
    ComponentCreator componentCreator;
    TeleportationService teleportationService;
    boolean isDefaultActive = false;
    boolean isDefaultKeepInventory = false;

    public VoidEvent(Plugin plugin, ComponentCreator componentCreator, TeleportationService teleportationService){
        this.plugin = plugin;
        this.componentCreator = componentCreator;
        this.teleportationService = teleportationService;
        load(plugin);
    }

    private void read(){
        String onVoidFallActive = "events.on-void-fall.active";
        ConfigChecker.warnIfMissing(plugin, onVoidFallActive);
        this.isDefaultActive = plugin.getConfig().getBoolean(onVoidFallActive);
        String onVoidFallKeepInventory = "events.on-void-fall.keep-inventory";
        ConfigChecker.warnIfMissing(plugin, onVoidFallKeepInventory);
        this.isDefaultKeepInventory = plugin.getConfig().getBoolean(onVoidFallKeepInventory);
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
    public void onVoidFall(EntityDamageEvent event){
        if (event.getEntity() instanceof Player player){
            if (event.getCause()== EntityDamageEvent.DamageCause.VOID && !SimpleBooleanDefaultPerm.getter(player,"wspawn.voidfall.exempt", !isDefaultActive)){
                event.setCancelled(true);
                boolean keepInventory = SimpleBooleanDefaultPerm.getter(player,"wspawn.voidfall.keep-inventory",isDefaultKeepInventory);
                if (!keepInventory)
                    player.getInventory().clear();
                teleportationService.request(player, TeleportationSound.teleportationSoundType.VOID).thenAccept(teleportationOutCome -> {
                    if (teleportationOutCome== TeleportationOutCome.SUCCESS){
                        componentCreator.send(player,(keepInventory)? componentCreator.onVoidFallKeep(player):componentCreator.onVoidFallClear(player));
                    }
                });

            }
        }
    }
}
