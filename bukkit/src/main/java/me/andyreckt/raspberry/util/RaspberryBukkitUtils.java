package me.andyreckt.raspberry.util;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public class RaspberryBukkitUtils {
    public String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
