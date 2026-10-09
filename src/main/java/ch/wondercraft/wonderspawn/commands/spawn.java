package ch.wondercraft.wonderspawn.commands;

import ch.wondercraft.wonderspawn.teleportation.TeleportationService;
import ch.wondercraft.wonderspawn.text.Text;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import javax.xml.stream.Location;

public class spawn {
    public static LiteralArgumentBuilder create(Text text, Location spawnLocation, TeleportationService teleportationService){
        return LiteralArgumentBuilder.literal("spawn");
    }
}
