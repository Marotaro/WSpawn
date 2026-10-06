package ch.wondercraft.wonderspawn;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.NoSuchFileException;

public class SpawnLocation extends Location {
    private SpawnLocation(Location location){
        super(location.getWorld(),location.getX(),location.getY(),location.getZ(),location.getYaw(),location.getPitch());
    }

    private static Location read(@NotNull File spawnFile) throws NoSuchFileException,IllegalArgumentException {
        if (!spawnFile.exists()){
            throw new NoSuchFileException("spawnLocation does not exist");
        }

        Location location = YamlConfiguration.loadConfiguration(spawnFile).getLocation("spawn-location");
        if (location==null){
            throw new IllegalArgumentException("spawnLocation does not contain location or is not formated correctly");
        }
        return location;
    }

    public static SpawnLocation load(@NotNull File spawnFile)throws NoSuchFileException,IllegalArgumentException{
        return new SpawnLocation(read(spawnFile));
    }

    public void update(@NotNull Location l){
        setWorld(l.getWorld());
        set(l.getX(),l.getY(),l.getZ());
        setYaw(l.getYaw());
        setPitch(l.getPitch());
    }

}
