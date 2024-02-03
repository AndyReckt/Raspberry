package me.andyreckt.raspberry.command;

import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

@RequiredArgsConstructor
public class BungeeCommandIssuer implements CommandIssuer<CommandSender> {

    private final CommandSender issuer;

    @Override
    public CommandSender getIssuer() {
        return issuer;
    }

    @Override
    public boolean isPlayer() {
        return issuer instanceof ProxiedPlayer;
    }

    @Override
    public void sendMessage(String message) {
        issuer.sendMessage(message);
    }

    @Override
    public UUID getUniqueId() {
        if (isPlayer())
            return ((ProxiedPlayer) issuer).getUniqueId();

        return new UUID(0L, 0L);
    }

    @Override
    public boolean hasPermission(String permission) {
        return issuer.hasPermission(permission);
    }
}
