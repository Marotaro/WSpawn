package ch.wondercraft.wonderspawn.teleportation;

import ch.wondercraft.wonderspawn.text.Text;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.util.Set;
import java.util.stream.Collectors;

public class TimerDisplay {
    private final File configFile;
    private final Text text;
    private BossBar.Color barColor;
    private boolean active;
    private boolean isPerSecondUpdate;
    private Set<BossBar.Flag> flags;

    private TimerDisplay(File configFile, Text text, Data data){
        this.configFile = configFile;
        this.text = text;
        apply(data);
    }

    private void apply(Data data){
        this.barColor = data.color();
        this.active = data.active();
        this.isPerSecondUpdate = data.isPerSecondUpdate();
        this.flags = data.flags;
    }

    private static Data read(File configFile, Text text) throws NoSuchFileException,IllegalArgumentException{
        if (!configFile.exists()){
            throw new NoSuchFileException("config file does not exist");
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        BossBar.Color color = BossBar.Color.valueOf(config.getString("timer-display.color","RED"));
        boolean active = config.getBoolean("timer-display.active");
        boolean isPerSecondUpdate = config.getBoolean("timer-display.per-second-update");
        Set<BossBar.Flag> flags = config.getStringList("timer-display.flag").stream().map(BossBar.Flag::valueOf).collect(Collectors.toSet());
        return new Data(color,active,isPerSecondUpdate,flags);
    }

    public static TimerDisplay load(File configFile, Text text) throws NoSuchFileException,IllegalArgumentException{
        return new TimerDisplay(configFile,text,read(configFile,text));
    }

    public void reload() throws NoSuchFileException{
        apply(read(this.configFile,this.text));
    }

    public BossBar create(int remainingSecond){
        return BossBar.bossBar(text.bar(remainingSecond),1f,barColor, BossBar.Overlay.PROGRESS,flags);
    }

    public boolean isActive(){
        return active;
    }

    public boolean isPerSecondUpdate(){
        return isPerSecondUpdate;
    }

    private record Data(BossBar.Color color, boolean active, boolean isPerSecondUpdate, Set<BossBar.Flag> flags){}
}
