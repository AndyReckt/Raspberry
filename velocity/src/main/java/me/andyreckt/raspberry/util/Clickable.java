package me.andyreckt.raspberry.util;


import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class Clickable {
    private TextComponent component = null;

    public Clickable() {
    }

    public Clickable(String msg, String hoverMsg, String clickString, String suggestString) {
        TextComponent message = RaspberryVelocityUtils.color(msg);

        if (hoverMsg != null && !hoverMsg.equalsIgnoreCase("")) {
            message.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, RaspberryVelocityUtils.color(hoverMsg)));
        }

        if (clickString != null && !clickString.equalsIgnoreCase("")) {
            message.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, clickString));
        }

        if (suggestString != null && !suggestString.equalsIgnoreCase("")) {
            message.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestString));
        }

        component = message;
    }

    public TextComponent add(String msg, String hoverMsg, String clickString, String suggestString) {
        TextComponent message = RaspberryVelocityUtils.color(msg);

        if (hoverMsg != null && !hoverMsg.equalsIgnoreCase("")) {
            message.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, RaspberryVelocityUtils.color(hoverMsg)));
        }

        if (clickString != null && !clickString.equalsIgnoreCase("")) {
            message.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, clickString));
        }

        if (suggestString != null && !suggestString.equalsIgnoreCase("")) {
            message.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestString));
        }

        if (component == null) {
            component = message;
        } else {
            component = component.append(message);
        }

        return component;
    }

    public void sendToPlayer(CommandSource player) {
        player.sendMessage(component);
    }
}
