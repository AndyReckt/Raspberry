package me.andyreckt.raspberry.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

@UtilityClass
public class RaspberryBukkitConstant {
    public Class<?> SPIGOT_CONFIG_CLASS;
    public Field UNKNOWN_COMMAND_MESSAGE_FIELD;
    public String UNKNOWN_COMMAND_MESSAGE;

    static {
        try {
            SPIGOT_CONFIG_CLASS = Class.forName("org.spigotmc.SpigotConfig");
            UNKNOWN_COMMAND_MESSAGE_FIELD = SPIGOT_CONFIG_CLASS.getDeclaredField("unknownCommandMessage");
            UNKNOWN_COMMAND_MESSAGE_FIELD.setAccessible(true);
            UNKNOWN_COMMAND_MESSAGE = (String) UNKNOWN_COMMAND_MESSAGE_FIELD.get(null);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
