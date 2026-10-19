package ch.wondercraft.wonderspawn;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

public class SpawnLocation extends Location {
    private final File spawnFile;

    private SpawnLocation(Location location, File spawnFile){
        this.spawnFile = spawnFile;
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

    public static SpawnLocation load(@NotNull File spawnFile) throws NoSuchFileException,IllegalArgumentException{
        return new SpawnLocation(read(spawnFile),spawnFile);
    }

    public void update(@NotNull Location l){
        setWorld(l.getWorld());
        set(l.getX(),l.getY(),l.getZ());
        setYaw(l.getYaw());
        setPitch(l.getPitch());
    }

    public void save(){
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("spawn-location",new Location(getWorld(),getX(),getY(),getZ(),getYaw(),getPitch()));
        try {
            yamlConfiguration.save(spawnFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reload() throws NoSuchFileException,IllegalArgumentException{
        this.update(read(this.spawnFile));
    }
}
