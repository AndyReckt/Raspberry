package me.andyreckt.raspberry.completions;

import java.util.Collection;

public interface CommandCompletionAction<T extends CommandCompletionContext> {
    Collection<String> get(CommandCompletionContext context);
}
