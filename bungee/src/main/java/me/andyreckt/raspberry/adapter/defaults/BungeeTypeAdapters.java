package me.andyreckt.raspberry.adapter.defaults;

import lombok.experimental.UtilityClass;
import me.andyreckt.raspberry.adapter.RaspberryTypeAdapter;
import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.exception.InvalidArgumentException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
@SuppressWarnings({"rawtypes"})
public class BungeeTypeAdapters {
    public RaspberryTypeAdapter<ProxiedPlayer> PROXIED_PLAYER = new RaspberryTypeAdapter<ProxiedPlayer>() {
        @Override
        public ProxiedPlayer transform(CommandIssuer sender, String source) throws InvalidArgumentException {
            if (sender.isPlayer() && (source.equalsIgnoreCase("self") || source.isEmpty())) {
                return (ProxiedPlayer) sender.getIssuer();
            }

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(source);

            if (player == null) {
                throw new InvalidArgumentException("Player '" + source + "' not found.");
            }

            return player;
        }

        @Override
        public List<String> complete(CommandIssuer sender, String source) {
            List<String> completions = new ArrayList<>();

            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                completions.add(player.getName());
            }

            return completions;
        }
    };

    public RaspberryTypeAdapter<ServerInfo> SERVER_INFO = new RaspberryTypeAdapter<ServerInfo>() {
        @Override
        public ServerInfo transform(CommandIssuer sender, String source) throws InvalidArgumentException {
            ServerInfo server = ProxyServer.getInstance().getServerInfo(source);

            if (server == null) {
                throw new InvalidArgumentException("Server '" + source + "' not found.");
            }

            return server;
        }

        @Override
        public List<String> complete(CommandIssuer sender, String source) {
            List<String> completions = new ArrayList<>();

            for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
                completions.add(server.getName());
            }

            return completions;
        }
    };
}
