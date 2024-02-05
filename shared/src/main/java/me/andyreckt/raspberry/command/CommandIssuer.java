package me.andyreckt.raspberry.command;

import me.andyreckt.raspberry.util.ClickablePart;

import java.util.List;
import java.util.UUID;

public interface CommandIssuer<T> {
    T getIssuer();

    boolean isPlayer();

    void sendMessage(String message);

    UUID getUniqueId();

    boolean hasPermission(String permission);

    void sendClickable(List<ClickablePart> parts);
}
