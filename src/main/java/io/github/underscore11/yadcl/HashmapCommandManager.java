package io.github.underscore11.yadcl;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class HashmapCommandManager implements ICommandManager {
    private static final Logger logger = LoggerFactory.getLogger("HashmapCommandManager");
    private final ExecutorService executorService = Executors.newCachedThreadPool(new CommandManagerThreadFactory());

    // Command#getName : Command
    private final Map<String, Command> backingMap = new HashMap<>();
    private BiConsumer<CommandEvent, Exception> exceptionHandler = (event, exception) ->
            logger.error("Uncaught exception executing command " + event.hashCode(), exception);
    private Function<Guild, Set<String>> prefixProvider;

    public HashmapCommandManager(Function<Guild, Set<String>> prefixProvider) {
        this.prefixProvider = prefixProvider;
    }

    public HashmapCommandManager(String... prefixes) {
        setPrefixes(prefixes);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        executorService.execute(() -> handleMessage(event));
    }

    private void handleMessage(MessageReceivedEvent event) {
        logger.debug("Message received: {}", event.getMessage().getContentRaw());
        Objects.requireNonNull(prefixProvider);
        Set<String> prefixes = prefixProvider.apply(event.getGuild());
        if (prefixes.size() == 0) throw new IllegalStateException("No prefixes returned!");
        Optional<String> prefix = prefixes.stream()
                .filter(possiblePrefix -> event.getMessage().getContentRaw().startsWith(possiblePrefix))
                .findFirst();
        if (prefix.isEmpty()) return;
        logger.debug("Prefix found: {}", prefix.get());
        // We have a prefix

        List<String> args = new LinkedList<>(Arrays.asList(  // Arrays.asList returns a list w/ unmodifiable length.
                event.getMessage().getContentRaw()           // duh.
                        .substring(prefix.get().length())    // trim the prefix off
                        .split(" ")));                 // split on space
        String commandName = args.remove(0);           // pop first "arg" (command name or alias)
        Command command = getCommand(commandName);
        if (command == null) {
            Optional<Command> possibleCommand = getCommands().stream().filter(command1 -> command1
                    .getAliases(event.getGuild())
                    .contains(prefix.get()))
                    .findFirst();
            if (possibleCommand.isEmpty()) return;
            command = possibleCommand.get();
        }
        logger.debug("Command found: {}", command.getName());
        // We have a command

        UUID commandId = UUID.randomUUID();
        CommandEvent commandEvent = new CommandEvent(event, command, args);
        for (CommandRequirement requirement : command.getRequirements()) {
            if (!requirement.test(commandEvent)) {
                requirement.handle(commandEvent);
                return;
            }
        }
        // All checks have passed, run the command!

        try {
            logger.info("Running {}: {}", commandEvent.hashCode(), commandEvent);
            command.run(commandEvent);
        } catch (Exception e) {
            exceptionHandler.accept(commandEvent, e);
        }
    }

    @Override
    public Command getCommand(String name) {
        return backingMap.get(name);
    }

    @Override
    public Set<Command> getCommands() {
        return new HashSet<>(backingMap.values());
    }

    @Override
    public void addCommand(Command command) {
        backingMap.put(command.getName(), command);
    }

    @Override
    public void removeCommand(Command command) {
        backingMap.remove(command.getName());
    }

    @Override
    public void removeCommand(String name) {
        backingMap.remove(name);
    }

    @Override
    public void setExceptionHandler(BiConsumer<CommandEvent, Exception> handler) {
        exceptionHandler = handler;
    }

    @Override
    public void setPrefixProvider(Function<Guild, Set<String>> prefixProvider) {
        this.prefixProvider = prefixProvider;
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }

    private static class CommandManagerThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        CommandManagerThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "CommandManager-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
