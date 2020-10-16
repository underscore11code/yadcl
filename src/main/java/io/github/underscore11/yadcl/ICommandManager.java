package io.github.underscore11.yadcl;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface ICommandManager extends EventListener {
    @Override
    default void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) onMessageReceived((MessageReceivedEvent) event);
    }

    /**
     * Called on Message Receive
     * @param event The event
     */
    void onMessageReceived(MessageReceivedEvent event);

    Command getCommand(String name);

    Set<Command> getCommands();

    void addCommand(Command command);

    void removeCommand(Command command);

    void removeCommand(String name);

    void setExceptionHandler(BiConsumer<CommandEvent, Exception> handler);

    void setPrefixProvider(Function<Guild, Set<String>> prefixProvider);

    default void setPrefixes(String... prefixes) {
        setPrefixProvider(guild -> new HashSet<>(Arrays.asList(prefixes)));
    }

    void shutdown();
}
