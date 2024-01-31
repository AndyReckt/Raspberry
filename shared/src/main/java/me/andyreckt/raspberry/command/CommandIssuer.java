package me.andyreckt.raspberry.command;

import java.util.UUID;

public interface CommandIssuer<T> {
    T getIssuer();

    boolean isPlayer();

    void sendMessage(String message);

    UUID getUniqueId();

    boolean hasPermission(String permission);
}
