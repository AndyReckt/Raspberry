package me.andyreckt.raspberry.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
public class BukkitCommandIssuer implements CommandIssuer<CommandSender> {

    private final CommandSender issuer;

    @Override
    public CommandSender getIssuer() {
        return issuer;
    }

    @Override
    public boolean isPlayer() {
        return issuer instanceof Player;
    }

    @Override
    public void sendMessage(String message) {
        issuer.sendMessage(message);
    }

    @Override
    public UUID getUniqueId() {
        if (isPlayer())
            return ((Player) issuer).getUniqueId();

        return new UUID(0L, 0L);
    }

    @Override
    public boolean hasPermission(String permission) {
        return issuer.hasPermission(permission);
    }
}
