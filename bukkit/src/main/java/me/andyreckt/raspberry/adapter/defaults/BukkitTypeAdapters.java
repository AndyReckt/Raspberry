package me.andyreckt.raspberry.adapter.defaults;

import lombok.experimental.UtilityClass;
import me.andyreckt.raspberry.adapter.RaspberryTypeAdapter;
import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.exception.InvalidArgumentException;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
@SuppressWarnings({"rawtypes", "deprecation"})
public class BukkitTypeAdapters {
    public RaspberryTypeAdapter<OfflinePlayer> OFFLINE_PLAYER = new RaspberryTypeAdapter<OfflinePlayer>() {
        @Override
        public OfflinePlayer transform(CommandIssuer sender, String source, String... options) throws InvalidArgumentException {
            if (sender.isPlayer() && (source.equalsIgnoreCase("self") || source.isEmpty())) {
                return (Player) sender.getIssuer();
            }

            OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(source);

            if (player == null) {
                throw new InvalidArgumentException("Player '" + source + "' not found.");
            }

            return player;
        }

        @Override
        public List<String> complete(CommandIssuer sender, String source, String... options) {
            List<String> completions = new ArrayList<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }

            return completions;
        }
    };

    public RaspberryTypeAdapter<Player> PLAYER = new RaspberryTypeAdapter<Player>() {
        @Override
        public Player transform(CommandIssuer sender, String source, String... options) throws InvalidArgumentException {
            if (sender.isPlayer() && (source.equalsIgnoreCase("self") || source.isEmpty())) {
                return (Player) sender.getIssuer();
            }

            if (source.equalsIgnoreCase("self")) {
                throw new InvalidArgumentException("You must specify a player.");
            }

            Player player = Bukkit.getServer().getPlayer(source);

            if (player == null) {
                throw new InvalidArgumentException("Player '" + source + "' not found.");
            }

            return player;
        }

        @Override
        public List<String> complete(CommandIssuer sender, String source, String... options) {
            List<String> completions = new ArrayList<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }

            return completions;
        }
    };

    public RaspberryTypeAdapter<World> WORLD = new RaspberryTypeAdapter<World>() {
        @Override
        public World transform(CommandIssuer sender, String source, String... options) throws InvalidArgumentException {
            World world = Bukkit.getServer().getWorld(source);

            if (world == null) {
                throw new InvalidArgumentException("World '" + source + "' not found.");
            }

            return world;
        }

        @Override
        public List<String> complete(CommandIssuer sender, String source, String... options) {
            List<String> completions = new ArrayList<>();

            for (World world : Bukkit.getWorlds()) {
                completions.add(world.getName());
            }

            return completions;
        }
    };

    public RaspberryTypeAdapter<Material> MATERIAL = new RaspberryTypeAdapter<Material>() {
        @Override
        public Material transform(CommandIssuer sender, String source, String... options) throws InvalidArgumentException {
            Material material = Material.matchMaterial(source.toUpperCase());

            if (material == null) {
                throw new InvalidArgumentException("Material '" + source + "' not found.");
            }

            return material;
        }

        @Override
        public List<String> complete(CommandIssuer sender, String source, String... options) {
            List<String> completions = new ArrayList<>();

            for (Material material : Material.values()) {
                completions.add(material.name());
            }

            return completions;
        }
    };

    private final Map<String, GameMode> GAMEMODES = new HashMap<String, GameMode>() {{
        put("s", GameMode.SURVIVAL);
        put("survival", GameMode.SURVIVAL);
        put("c", GameMode.CREATIVE);
        put("creative", GameMode.CREATIVE);
        put("a", GameMode.ADVENTURE);
        put("adventure", GameMode.ADVENTURE);
        put("sp", GameMode.SPECTATOR);
        put("spectator", GameMode.SPECTATOR);
        put("0", GameMode.SURVIVAL);
        put("1", GameMode.CREATIVE);
        put("2", GameMode.ADVENTURE);
        put("3", GameMode.SPECTATOR);
    }};

    public RaspberryTypeAdapter<GameMode> GAMEMODE = new RaspberryTypeAdapter<GameMode>() {
        @Override
        public GameMode transform(CommandIssuer sender, String source, String... options) throws InvalidArgumentException {
            GameMode gameMode = GAMEMODES.get(source.toLowerCase());

            if (gameMode == null) {
                throw new InvalidArgumentException("GameMode '" + source + "' not found.");
            }

            return gameMode;
        }

        @Override
        public List<String> complete(CommandIssuer sender, String source, String... options) {
            return new ArrayList<>(GAMEMODES.keySet());
        }
    };
}
