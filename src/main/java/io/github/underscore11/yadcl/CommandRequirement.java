package io.github.underscore11.yadcl;

import net.dv8tion.jda.api.Permission;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface CommandRequirement {

    /**
     * Tests if the requirement is met
     * @param event The event to test
     * @return {@code true} if the test passes (Command can run), {@code false} if not
     */
    boolean test(CommandEvent event);

    /**
     * Called if the test does not pass. Use for user-facing errors etc.
     * @param event The event triggering the test
     */
    default void handle(CommandEvent event) {}

    static CommandRequirement of(Predicate<CommandEvent> requirement) {
        return new CommandRequirement() {
            @Override
            public boolean test(CommandEvent event) {
                return requirement.test(event);
            }
        };
    }

    static CommandRequirement of(Predicate<CommandEvent> requirement, Consumer<CommandEvent> handler) {
        return new CommandRequirement() {
            @Override
            public boolean test(CommandEvent event) {
                return requirement.test(event);
            }

            @Override
            public void handle(CommandEvent event) {
                handler.accept(event);
            }
        };
    }

    /**
     * @return A predicate of if the triggering event is in a guild
     */
    static Predicate<CommandEvent> isFromGuildPredicate() {
        return CommandEvent::isFromGuild;
    }

    /**
     * @return A silently-failing requirement of {@link CommandRequirement#isFromGuildPredicate()}
     */
    static CommandRequirement isFromGuild() {
        return CommandRequirement.of(CommandRequirement.isFromGuildPredicate());
    }

    /**
     * @return A predicate of if the triggering {@link net.dv8tion.jda.api.entities.Member} is the guild owner
     * @throws NullPointerException If {@code member} is null
     */
    static Predicate<CommandEvent> isGuildOwnerPredicate() {
        return event -> Objects.requireNonNull(event.getMember()).isOwner();
    }

    /**
     * @return A silently-failing requirement of {@link CommandRequirement#isGuildOwnerPredicate()}
     * @throws NullPointerException If {@code member} is null
     */
    static CommandRequirement isGuildOwner() {
        return CommandRequirement.of(CommandRequirement.isGuildOwnerPredicate());
    }

    /**
     * @param permission The permission to check
     * @return A predicate of if the triggering {@link net.dv8tion.jda.api.entities.Member} has {@code permission}
     * @throws NullPointerException If {@code member} is null
     */
    static Predicate<CommandEvent> hasPermissionPredicate(Permission permission) {
        return event -> Objects.requireNonNull(event.getMember()).hasPermission(permission);
    }

    /**
     * @param permission The permission to check
     * @return A silently-failing requirement of {@link CommandRequirement#hasPermissionPredicate(Permission)}
     * @throws NullPointerException If {@code member} is null
     */
    static CommandRequirement hasPermission(Permission permission) {
        return CommandRequirement.of(CommandRequirement.hasPermissionPredicate(permission));
    }
}
