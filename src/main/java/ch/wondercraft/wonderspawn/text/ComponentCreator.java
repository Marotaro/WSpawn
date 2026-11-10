package ch.wondercraft.wonderspawn.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Locale;

public class ComponentCreator {
    private final Plugin plugin;
    private final MiniMessage miniMessage = MiniMessage.builder().build();
    private final Location spawnLocation;
    private final MessageLanguage messageLanguage;

    private ComponentCreator(Plugin plugin, Location spawnLocation){
        this.plugin = plugin;
        this.spawnLocation = spawnLocation;
        messageLanguage = MessageLanguage.load(plugin);
    }

    public static ComponentCreator load(Plugin plugin, Location spawnLocation){
        return new ComponentCreator(plugin, spawnLocation);
    }

    public void reload(){
        messageLanguage.reload();
    }

    // ── Core ─────────────────────────────────────────────

    private TagResolver spawnResolver(){
        return TagResolver.resolver(
                Placeholder.unparsed("spawn-x", String.valueOf(spawnLocation.getBlockX())),
                Placeholder.unparsed("spawn-y", String.valueOf(spawnLocation.getBlockY())),
                Placeholder.unparsed("spawn-z", String.valueOf(spawnLocation.getBlockZ())),
                Placeholder.unparsed("spawn-world", spawnLocation.getWorld().getName()),
                Placeholder.unparsed("spawn-yaw", String.valueOf(spawnLocation.getYaw())),
                Placeholder.unparsed("spawn-pitch", String.valueOf(spawnLocation.getPitch()))
        );
    }

    private Component build(CommandSender receiver, String path, TagResolver... extra){
        Locale locale = receiver instanceof Player p ? p.locale() : null;
        return miniMessage.deserialize(
                messageLanguage.get(locale, path),
                TagResolver.builder()
                        .resolver(Placeholder.parsed("prefix", messageLanguage.get(locale, "prefix")))
                        .resolver(spawnResolver())
                        .resolvers(extra)
                        .build());
    }

    private Component timer(Player player, String path, int remainingSecond){
        return build(player, path, Placeholder.unparsed("remaining-second", String.valueOf(remainingSecond)));
    }

    private static TagResolver head(Player player){
        return Placeholder.component("head", Component.object(ObjectContents.playerHead(player.getUniqueId())));
    }

    public void send(CommandSender receiver, Component component){
        if (!PlainTextComponentSerializer.plainText().serialize(component).isBlank())
            receiver.sendMessage(component);
    }

    // ── /spawn ───────────────────────────────────────────

    public Component spawnPromise(Player player, int remainingSecond){
        return timer(player, "spawn.self.countdown-start", remainingSecond);
    }

    public Component spawnCanceledPromise(Player player, int remainingSecond){
        return timer(player, "spawn.self.countdown-restart", remainingSecond);
    }

    public Component spawnPlayerMoved(Player player){
        return build(player, "spawn.self.error.moved");
    }

    public Component spawnConfirmation(Player player){
        return build(player, "spawn.self.confirmation");
    }

    public Component spawnUnknownError(Player player){
        return build(player, "spawn.self.error.unknown");
    }

    public Component spawnUnknownError(CommandSender commandSender){
        return build(commandSender, "spawn.self.error.unknown");
    }

    public Component cooldownDenied(Player player, int remainingSecond){
        return timer(player, "spawn.cooldown", remainingSecond);
    }

    // ── /spawn <player> ──────────────────────────────────

    public Component forcedSpawnNotice(CommandSender sender, Player target){
        if (sender instanceof Player p)
            return build(target, "spawn.forced.target.player",
                    Placeholder.unparsed("sender", p.getName()), head(p));
        return build(target, "spawn.forced.target.console",
                Placeholder.unparsed("sender", sender.getName()));
    }

    public Component forcedSpawnConfirmation(CommandSender sender, Player target){
        return build(sender, "spawn.forced.sender",
                Placeholder.unparsed("target", target.getName()), head(target));
    }

    public Component forcedSpawnTryMoreThanOne(CommandSender sender){
        return build(sender, "spawn.forced.error.multiple-targets");
    }

    // ── /setspawn ────────────────────────────────────────

    public Component setSpawnConfirmation(CommandSender sender, Location oldLocation){
        return build(sender, "set-spawn.confirmation",
                Placeholder.unparsed("old-x", String.valueOf(Math.round(oldLocation.getX()))),
                Placeholder.unparsed("old-y", String.valueOf(Math.round(oldLocation.getY()))),
                Placeholder.unparsed("old-z", String.valueOf(Math.round(oldLocation.getZ()))),
                Placeholder.unparsed("old-world", oldLocation.getWorld().getName()),
                Placeholder.unparsed("old-yaw", String.valueOf(oldLocation.getYaw())),
                Placeholder.unparsed("old-pitch", String.valueOf(oldLocation.getPitch()))
        );
    }

    public Component setSpawnNeedsVerboseVersion(CommandSender sender){
        return build(sender, "set-spawn.error.console-needs-coordinates");
    }

    // ── /wspawn reload ───────────────────────────────────

    public Component reloadPromise(CommandSender sender){
        return build(sender, "reload.start");
    }

    public Component reloadConfirmation(CommandSender sender){
        return build(sender, "reload.confirmation");
    }

    public Component reloadFailed(CommandSender sender){
        return build(sender, "reload.error.failed",
                Placeholder.unparsed("error-file", ""),
                Placeholder.unparsed("error-message", "")
        );
    }

    // ── Countdown display ────────────────────────────────

    public Component bar(Player player, int remainingSecond, boolean canMove){
        return timer(player, canMove ? "timer.bossbar.can-move" : "timer.bossbar.cannot-move", remainingSecond);
    }

    public Component chat(Player player, int remainingSecond, boolean canMove){
        return timer(player, canMove ? "timer.chat.can-move" : "timer.chat.cannot-move", remainingSecond);
    }

    // ── Automatic teleports ──────────────────────────────

    public Component onJoinTeleportation(Player player){
        return build(player, "events.on-join");
    }

    public Component onFirstJoinTeleportation(Player player){
        return build(player, "events.on-first-join");
    }

    public Component onRespawnBlockMissing(Player player){
        return build(player, "events.on-respawn-block-missing");
    }

    public Component onRespawnOverrideBed(Player player){
        return build(player, "events.on-respawn-override-bed");
    }

    public Component onRespawnOverrideAnchor(Player player){
        return build(player, "events.on-respawn-override-anchor");
    }
}