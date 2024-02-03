package me.andyreckt.raspberry.util;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;

@UtilityClass
public class RaspberryBungeeUtils {
    public String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
