package ch.wondercraft.wonderspawn;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class Text {
    private static MiniMessage mm = MiniMessage.builder().build();

    private static Component build (TextString textString, String messageKey,TagResolver ...tagResolvers){
        try {
            String text = textString.getText(messageKey);
            return Component.text(text);
        } catch (NullPointerException e){
            return Component.text("that message does not exist in file");
        }
    }
}
