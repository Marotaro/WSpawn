package ch.wondercraft.wonderspawn;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.util.Set;
import java.util.stream.Collectors;

public class TimerDisplay {
    private final File configFile;
    private final File langFile;
    private BossBar.Color barColor;
    private String text = "ahah"; //hardcoded one, waiting for text manager
    private boolean active;
    private Set<BossBar.Flag> flags;

    private TimerDisplay(File configFile, File langFile, Data data){
        this.configFile = configFile;
        this.langFile = langFile;
        apply(data);
    }

    private void apply(Data data){
        this.barColor = data.color();
        this.active = data.active();
    }

    private static Data read(File configFile, File langFile) throws NoSuchFileException,IllegalArgumentException{
        if (!configFile.exists()){
            throw new NoSuchFileException("config file does not exist");
        }
        if (!langFile.exists()){
            throw new NoSuchFileException("lang file does not exist");
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        YamlConfiguration lang = YamlConfiguration.loadConfiguration(langFile);
        BossBar.Color color = BossBar.Color.valueOf(config.getString("timer-display.color","RED"));
        boolean active = config.getBoolean("timer-display.active");
        Set<BossBar.Flag> flags = config.getStringList("timer-display.flag").stream().map(BossBar.Flag::valueOf).collect(Collectors.toSet());
        String a = "ahah";
        return new Data(color,active,flags);
    }

    public static TimerDisplay load(File configFile, File langFile) throws NoSuchFileException,IllegalArgumentException{
        return new TimerDisplay(configFile,langFile,read(configFile,langFile));
    }

    public void reload() throws NoSuchFileException{
        apply(read(this.configFile,this.langFile));
    }

    public BossBar create(){
        return BossBar.bossBar(Component.text(text),1f,barColor, BossBar.Overlay.PROGRESS,flags);
    }

    public boolean isActive(){
        return active;
    }

    private record Data(BossBar.Color color, boolean active, Set<BossBar.Flag> flags){}
}
