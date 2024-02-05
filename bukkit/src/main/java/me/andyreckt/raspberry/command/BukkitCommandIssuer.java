package me.andyreckt.raspberry.command;

import lombok.RequiredArgsConstructor;
import me.andyreckt.raspberry.util.Clickable;
import me.andyreckt.raspberry.util.ClickablePart;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
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

    @Override
    public void sendClickable(List<ClickablePart> parts) {
        Clickable clickable = new Clickable();

        for (ClickablePart part : parts) {
            clickable.add(part.getMessage(), part.getHover(), part.getCommand(), part.getSuggest());
        }

        clickable.sendToPlayer(getIssuer());
    }
}
