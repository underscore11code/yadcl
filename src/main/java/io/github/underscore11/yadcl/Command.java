package io.github.underscore11.yadcl;

import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public abstract class Command {
    private final List<CommandRequirement> requirements;
    private final String name;
    private final Set<String> globalAliases;
    private final Logger logger;

    public Command(List<CommandRequirement> requirements, String name, Set<String> globalAliases) {
        this.requirements = requirements;
        this.name = name;
        this.globalAliases = globalAliases;
        logger = LoggerFactory.getLogger("Command-" + name);
    }

    /**
     * Called when a command is run & all requirements pass. Where the command's logic goes.
     * @param event The event
     */
    public abstract void run(CommandEvent event);

    /**
     * @return A ordered list of requirements to test before {@link Command#run(CommandEvent)} is called
     */
    public List<CommandRequirement> getRequirements() {
        return requirements;
    }

    /**
     * @param guild A nullable guild that the check is for
     * @return A set of string aliases for the command, applicable to the given guild
     */
    public Set<String> getAliases(@Nullable Guild guild) {
        return globalAliases;
    }

    @Override
    public String toString() {
        return "Command{" +
                "requirements=" + requirements +
                ", name='" + name + '\'' +
                ", globalAliases=" + globalAliases +
                '}';
    }

    public String getName() {
        return name;
    }
}
