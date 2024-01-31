package me.andyreckt.raspberry;

import me.andyreckt.raspberry.paper.completion.PaperCommandCompletionHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class RaspberryPaper extends RaspberryBukkit {
    public RaspberryPaper(JavaPlugin plugin) {
        super(plugin);

        try {
            Class<?> asyncTabEvent = Class.forName("com.destroystokyo.paper.event.server.AsyncTabCompleteEvent");
            this.logger.info("Detected Paper, enabling async tab completion.");

            plugin.getServer().getPluginManager().registerEvents(new PaperCommandCompletionHandler(this), plugin);
        } catch (ClassNotFoundException ignored) {
        }
    }
}
