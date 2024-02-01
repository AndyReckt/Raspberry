package me.andyreckt.raspberry.paper.completion;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.andyreckt.raspberry.RaspberryPaper;
import me.andyreckt.raspberry.arguments.Arguments;
import me.andyreckt.raspberry.bukkit.BukkitRaspberryCommand;
import me.andyreckt.raspberry.command.BukkitCommandIssuer;
import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.command.RaspberryCommand;
import me.andyreckt.raspberry.exception.CompletionFailedException;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class PaperCommandCompletionHandler implements Listener {

    private final RaspberryPaper raspberry;

    @SneakyThrows
    @EventHandler(ignoreCancelled = true)
    public void onTabComplete(AsyncTabCompleteEvent event) {
        String buffer = event.getBuffer();
        if ((!event.isCommand() && !buffer.startsWith("/")) || buffer.indexOf(' ') == -1) {
            return;
        }

        try {
            List<String> completions = getCompletions(buffer, event.getCompletions(), event.getSender());
            if (completions != null) {
                // if we have no completion data, client will display an error, lets just send a space instead (https://bugs.mojang.com/browse/MC-165562)
                if (completions.size() == 1 && completions.get(0).isEmpty()) {
                    completions.set(0, " ");
                }
                event.setCompletions(completions);
                event.setHandled(true);
            }
        } catch (Exception e) {
            if (raspberry.isDebugMode()) throw new CompletionFailedException();
        }
    }

    private List<String> getCompletions(String buffer, List<String> existingCompletions, CommandSender sender) {
        String[] args = buffer.split(" ");
        CommandIssuer<?> issuer = raspberry.getCommandIssuer(sender);

        String commandLabel = stripLeadingSlash(args[0]);
        args = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[]{""};

        RaspberryCommand command = raspberry.getRootCommand().getChild(commandLabel);

        List<String> completions = raspberry.getCommandHandler().getCompletions(command, issuer, args);

        return preformOnImmutable(existingCompletions, (list) -> list.addAll(completions));
    }

    private <T> List<T> preformOnImmutable(List<T> list, Consumer<List<T>> action) {
        try {
            action.accept(list);
        } catch (UnsupportedOperationException ex) {
            list = new ArrayList<>(list);
            action.accept(list);
        }

        return list;
    }

    private String stripLeadingSlash(String arg) {
        return arg.startsWith("/") ? arg.substring(1) : arg;
    }


}
