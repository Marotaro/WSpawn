package ch.wondercraft.wonderspawn;

import org.bukkit.plugin.Plugin;

public interface Reloadable {
    default void load(Plugin plugin){plugin.getLogger().info("Loading "+this.getClass().getSimpleName());}
    default void reload(Plugin plugin){plugin.getLogger().info("Reloading "+this.getClass().getSimpleName());
    }
}
