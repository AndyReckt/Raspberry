package me.andyreckt.raspberry.example;

import lombok.Getter;
import me.andyreckt.raspberry.Raspberry;
import me.andyreckt.raspberry.RaspberryBukkit;
import me.andyreckt.raspberry.RaspberryPaper;
import me.andyreckt.raspberry.example.ex1.CommandOne;
import me.andyreckt.raspberry.example.ex2.CommandTwo;
import me.andyreckt.raspberry.example.ex3.CommandThree;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@Getter
public class ExamplePlugin extends JavaPlugin {

    @Getter
    private static ExamplePlugin instance;

    private Raspberry raspberry;

    @Override
    public void onEnable() {
        instance = this;

        this.raspberry = new RaspberryPaper(this).debug();

        this.raspberry.registerCommandOfPlugin(this);
        this.raspberry.registerCommands(new CommandTwo());
        this.raspberry.registerCommands(new CommandThree());

        this.raspberry.registerAsyncCompletion("test", x -> Arrays.asList("test1", "test2", "test3"));
    }
}
