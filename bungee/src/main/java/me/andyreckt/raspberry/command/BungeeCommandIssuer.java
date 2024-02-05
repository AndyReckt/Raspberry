package me.andyreckt.raspberry.command;

import lombok.RequiredArgsConstructor;
import me.andyreckt.raspberry.util.Clickable;
import me.andyreckt.raspberry.util.ClickablePart;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
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

    @Override
    public void sendClickable(List<ClickablePart> parts) {
        Clickable clickable = new Clickable();

        for (ClickablePart part : parts) {
            clickable.add(part.getMessage(), part.getHover(), part.getCommand(), part.getSuggest());
        }

        clickable.sendToPlayer(getIssuer());
    }
}
