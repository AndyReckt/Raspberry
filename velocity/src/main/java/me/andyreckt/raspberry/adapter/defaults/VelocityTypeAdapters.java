package me.andyreckt.raspberry.adapter.defaults;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import lombok.experimental.UtilityClass;
import me.andyreckt.raspberry.RaspberryVelocity;
import me.andyreckt.raspberry.adapter.RaspberryTypeAdapter;
import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.exception.InvalidArgumentException;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
@SuppressWarnings({"rawtypes"})
public class VelocityTypeAdapters {
    private final RaspberryVelocity plugin = RaspberryVelocity.getVelocityInstance();

    public RaspberryTypeAdapter<Player> PLAYER = new RaspberryTypeAdapter<Player>() {
        @Override
        public Player transform(CommandIssuer sender, String source) throws InvalidArgumentException {
            if (sender.isPlayer() && (source.equalsIgnoreCase("self") || source.isEmpty())) {
                return (Player) sender.getIssuer();
            }

            return plugin.getProxy().getPlayer(source).orElseThrow(() -> new InvalidArgumentException("Player '" + source + "' not found."));
        }

        @Override
        public List<String> complete(CommandIssuer sender, String source) {
            List<String> completions = new ArrayList<>();

            for (Player player : plugin.getProxy().getAllPlayers()) {
                completions.add(player.getUsername());
            }

            return completions;
        }
    };

    public RaspberryTypeAdapter<ServerInfo> SERVER_INFO = new RaspberryTypeAdapter<ServerInfo>() {
        @Override
        public ServerInfo transform(CommandIssuer sender, String source) throws InvalidArgumentException {
            return plugin.getProxy().getServer(source).orElseThrow(() -> new InvalidArgumentException("Server '" + source + "' not found.")).getServerInfo();
        }

        @Override
        public List<String> complete(CommandIssuer sender, String source) {
            List<String> completions = new ArrayList<>();

            for (RegisteredServer server : plugin.getProxy().getAllServers()) {
                completions.add(server.getServerInfo().getName());
            }

            return completions;
        }
    };

    public RaspberryTypeAdapter<RegisteredServer> REGISTERED_SERVER = new RaspberryTypeAdapter<RegisteredServer>() {
        @Override
        public RegisteredServer transform(CommandIssuer sender, String source) throws InvalidArgumentException {
            return plugin.getProxy().getServer(source).orElseThrow(() -> new InvalidArgumentException("Server '" + source + "' not found."));
        }

        @Override
        public List<String> complete(CommandIssuer sender, String source) {
            List<String> completions = new ArrayList<>();

            for (RegisteredServer server : plugin.getProxy().getAllServers()) {
                completions.add(server.getServerInfo().getName());
            }

            return completions;
        }
    };
}
