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
            case "1.13":
                return new ModernCommandMapV1_13_R1();
            case "1.13.1":
            case "1.13.2":
                return new ModernCommandMapV1_13_R2();
            case "1.14":
            case "1.14.1":
            case "1.14.2":
            case "1.14.3":
            case "1.14.4":
                return new ModernCommandMapV1_14_R1();
            case "1.15":
            case "1.15.1":
            case "1.15.2":
                return new ModernCommandMapV1_15_R1();
            case "1.16":
            case "1.16.1":
                return new ModernCommandMapV1_16_R1();
            case "1.16.2":
            case "1.16.3":
                return new ModernCommandMapV1_16_R2();
            case "1.16.4":
            case "1.16.5":
                return new ModernCommandMapV1_16_R3();
            case "1.17":
            case "1.17.1":
                return new ModernCommandMapV1_17_R1();
            case "1.18":
            case "1.18.1":
                return new ModernCommandMapV1_18_R1();
            case "1.18.2":
                return new ModernCommandMapV1_18_R2();
            case "1.19":
            case "1.19.1":
            case "1.19.2":
                return new ModernCommandMapV1_19_R1();
            case "1.19.3":
                return new ModernCommandMapV1_19_R2();
            case "1.19.4":
                return new ModernCommandMapV1_19_R3();
            case "1.20":
            case "1.20.1":
                return new ModernCommandMapV1_20_R1();
            case "1.20.2":
                return new ModernCommandMapV1_20_R2();
            case "1.20.3":
            case "1.20.4":
                return new ModernCommandMapV1_20_R3();
            default:
                throw new IllegalStateException("Unsupported version: " + VERSION + ", please open an issue on GitHub. (https://github.com/AndyReckt/Raspberry/issues)");
        }
    }
}
