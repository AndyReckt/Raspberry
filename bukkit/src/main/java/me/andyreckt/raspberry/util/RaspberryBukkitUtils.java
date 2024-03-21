package me.andyreckt.raspberry.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

@UtilityClass
public class RaspberryBukkitUtils {
    public String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    public final int MINOR_VERSION = Integer.parseInt(VERSION.split("_")[1]);

    public boolean isNewMap() {
        return MINOR_VERSION >= 13;
    }
}
