package me.andyreckt.raspberry.util;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class Clickable {
    private final List<TextComponent> components = new ArrayList<>();

    public Clickable() {
    }

    public Clickable(String msg, String hoverMsg, String clickString, String suggestString) {
        TextComponent message = new TextComponent(TextComponent.fromLegacyText(RaspberryBungeeUtils.color(msg)));

        if (hoverMsg != null && !hoverMsg.equalsIgnoreCase("")) {
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(RaspberryBungeeUtils.color(hoverMsg))));
        }

        if (clickString != null && !clickString.equalsIgnoreCase("")) {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickString));
        }

        if (suggestString != null && !suggestString.equalsIgnoreCase("")) {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestString));
        }

        this.components.add(message);
    }

    public TextComponent add(String msg, String hoverMsg, String clickString, String suggestString) {
        TextComponent message = new TextComponent(TextComponent.fromLegacyText(RaspberryBungeeUtils.color(msg)));

        if (hoverMsg != null && !hoverMsg.equalsIgnoreCase("")) {
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(RaspberryBungeeUtils.color(hoverMsg))));
        }

        if (clickString != null && !clickString.equalsIgnoreCase("")) {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickString));
        }

        if (suggestString != null && !suggestString.equalsIgnoreCase("")) {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestString));
        }

        this.components.add(message);

        return message;
    }

    public void sendToPlayer(CommandSender player) {
        player.sendMessage(asComponents());
    }

    public TextComponent[] asComponents() {
        return this.components.toArray(new TextComponent[0]);
    }
}
