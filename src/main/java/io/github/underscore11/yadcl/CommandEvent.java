package io.github.underscore11.yadcl;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CommandEvent {
    private final MessageReceivedEvent causeEvent;
    private final List<String> args;
    private final Command command;

    public CommandEvent(MessageReceivedEvent causeEvent, Command command, List<String> args) {
        this.causeEvent = causeEvent;
        this.args = args;
        this.command = command;
    }

    /*
    * Getters
    */

    public MessageReceivedEvent getCauseEvent() {
        return causeEvent;
    }

    public List<String> getArgs() {
        return args;
    }

    public String getArg(int index) {
        return args.get(index);
    }

    public Command getCommand() {
        return command;
    }

    /*
    * Useful shortcuts
    */

    public MessageAction sendMessage(Message message) {
        return getChannel().sendMessage(message);
    }

    public MessageAction sendMessage(MessageEmbed embed) {
        return getChannel().sendMessage(embed);
    }

    public MessageAction sendMessage(CharSequence text) {
        return getChannel().sendMessage(text);
    }

    /*
    * Delegate Methods
    */

    @Nonnull
    public Message getMessage() {
        return causeEvent.getMessage();
    }

    @Nonnull
    public User getAuthor() {
        return causeEvent.getAuthor();
    }

    @Nullable
    public Member getMember() {
        return causeEvent.getMember();
    }

    @Nonnull
    public MessageChannel getChannel() {
        return causeEvent.getChannel();
    }

    public boolean isFromGuild() {
        return causeEvent.isFromGuild();
    }

    @Nonnull
    public ChannelType getChannelType() {
        return causeEvent.getChannelType();
    }

    @Nonnull
    public Guild getGuild() {
        return causeEvent.getGuild();
    }

    @Nonnull
    public JDA getJDA() {
        return causeEvent.getJDA();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommandEvent that = (CommandEvent) o;

        if (!causeEvent.equals(that.causeEvent)) return false;
        if (!args.equals(that.args)) return false;
        return command.equals(that.command);
    }

    @Override
    public int hashCode() {
        int result = causeEvent.hashCode();
        result = 31 * result + args.hashCode();
        result = 31 * result + command.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CommandEvent{" +
                "causeEventMessage=" + causeEvent.getMessage() +
                ", args=" + args +
                ", command=" + command +
                '}';
    }
}
