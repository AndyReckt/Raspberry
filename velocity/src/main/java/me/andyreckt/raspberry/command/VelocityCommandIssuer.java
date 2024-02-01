package me.andyreckt.raspberry.command;

import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import com.velocitypowered.api.command.CommandSource;
import me.andyreckt.raspberry.util.RaspberryVelocityUtils;

import java.util.UUID;

@RequiredArgsConstructor
public class VelocityCommandIssuer implements CommandIssuer<CommandSource> {

    private final CommandSource issuer;

    @Override
    public CommandSource getIssuer() {
        return issuer;
    }

    @Override
    public boolean isPlayer() {
        return issuer instanceof Player;
    }

    @Override
    public void sendMessage(String message) {
        issuer.sendMessage(RaspberryVelocityUtils.color(message));
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
