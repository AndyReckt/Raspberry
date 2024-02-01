package me.andyreckt.raspberry.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;


@UtilityClass
public class RaspberryVelocityUtils {
    public LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().character('&').hexColors().build();

    public TextComponent color(String message) {
        return serializer.deserialize(message);
    }

}