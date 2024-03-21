package me.andyreckt.raspberry.bukkit.modern;

import lombok.Getter;
import me.andyreckt.raspberry.modern.ModernCommandMap;
import me.andyreckt.raspberry.modern.impl.*;
import me.andyreckt.raspberry.util.RaspberryBukkitUtils;

@Getter
public class ModernCommandMapHandler {
    private static final String VERSION = RaspberryBukkitUtils.VERSION;
    private final ModernCommandMap commandMap;

    public ModernCommandMapHandler() {
        commandMap = getCommandMap();
        commandMap.swap();
    }

    private ModernCommandMap getCommandMap() {
        switch (VERSION) {
            case "v1_13_R1":
                return new ModernCommandMapV1_13_R1();
            case "v1_13_R2":
                return new ModernCommandMapV1_13_R2();
            case "v1_14_R1":
                return new ModernCommandMapV1_14_R1();
            case "v1_15_R1":
                return new ModernCommandMapV1_15_R1();
            case "v1_16_R1":
                return new ModernCommandMapV1_16_R1();
            case "v1_16_R2":
                return new ModernCommandMapV1_16_R2();
            case "v1_16_R3":
                return new ModernCommandMapV1_16_R3();
            case "v1_17_R1":
                return new ModernCommandMapV1_17_R1();
            case "v1_18_R1":
                return new ModernCommandMapV1_18_R1();
            case "v1_18_R2":
                return new ModernCommandMapV1_18_R2();
            case "v1_19_R1":
                return new ModernCommandMapV1_19_R1();
            case "v1_19_R2":
                return new ModernCommandMapV1_19_R2();
            case "v1_19_R3":
                return new ModernCommandMapV1_19_R3();
            case "v1_20_R1":
                return new ModernCommandMapV1_20_R1();
            case "v1_20_R2":
                return new ModernCommandMapV1_20_R2();
            case "v1_20_R3":
                return new ModernCommandMapV1_20_R3();
            default:
                throw new IllegalStateException("Unsupported version: " + VERSION + ", please open an issue on GitHub. (https://github.com/AndyReckt/Raspberry/issues)");
        }
    }
}
