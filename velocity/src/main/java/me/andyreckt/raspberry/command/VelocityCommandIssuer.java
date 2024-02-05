package me.andyreckt.raspberry.command;

import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import com.velocitypowered.api.command.CommandSource;
import me.andyreckt.raspberry.util.Clickable;
import me.andyreckt.raspberry.util.ClickablePart;
import me.andyreckt.raspberry.util.RaspberryVelocityUtils;

import java.util.List;
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

    @Override
    public void sendClickable(List<ClickablePart> parts) {
        Clickable clickable = new Clickable();

        for (ClickablePart part : parts) {
            clickable.add(part.getMessage(), part.getHover(), part.getCommand(), part.getSuggest());
        }

        clickable.sendToPlayer(getIssuer());
    }
}
