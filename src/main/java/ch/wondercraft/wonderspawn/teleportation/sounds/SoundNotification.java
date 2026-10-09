package ch.wondercraft.wonderspawn.teleportation.sounds;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SoundNotification {
    public final SoundNotificationType INITIAL,INTERVAL,FINAL,ERROR;
    private final File config;

    private SoundNotification(File config, SoundNotificationType INITIAL, SoundNotificationType INTERVAL, SoundNotificationType FINAL, SoundNotificationType ERROR){
        this.config = config;
        this.INITIAL = INITIAL;
        this.INTERVAL = INTERVAL;
        this.FINAL = FINAL;
        this.ERROR = ERROR;
    }

    public static SoundNotification load(File config) throws NoSuchFileException,IllegalArgumentException {
        return new SoundNotification(
                config,
                SoundNotificationType.load(config,"initial"),
                SoundNotificationType.load(config,"interval"),
                SoundNotificationType.load(config,"final"),
                SoundNotificationType.load(config,"error")
                );
    }

    public void reload() throws NoSuchFileException, IllegalArgumentException {
        Map<SoundNotificationType, SoundNotificationType.Data> loaded = new LinkedHashMap<>();
        for (SoundNotificationType s : List.of(INITIAL, INTERVAL, FINAL, ERROR)) {
            loaded.put(s, SoundNotificationType.read(config, s.key));
        }

        loaded.forEach(SoundNotificationType::apply);
    }
}
