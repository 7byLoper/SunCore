package ru.loper.suncore.api.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SunCommandExecutor implements CommandExecutor, TabCompleter {
    @Getter(AccessLevel.PROTECTED)
    protected List<SubCommandWrapper> subCommands = new ArrayList<>();

    @Nullable
    protected SubCommandWrapper getCommandByLabel(@NotNull String label) {
        String lowerLabel = label.toLowerCase();
        return subCommands.stream()
                .filter(cmd -> cmd.getAliases().stream()
                        .anyMatch(alias -> alias.equalsIgnoreCase(lowerLabel)))
                .findFirst()
                .orElse(null);
    }

    protected void addSubCommand(@NotNull SubCommand command, Permission permission, @NotNull List<String> aliases) {
        subCommands.add(new SubCommandWrapper(command, permission, aliases));
    }

    public void addSubCommand(@NotNull SubCommand command, @Nullable Permission permission, @NotNull String firstAlias, String... additionalAliases) {
        List<String> aliases = new ArrayList<>();
        aliases.add(firstAlias);
        aliases.addAll(Arrays.asList(additionalAliases));
        addSubCommand(command, permission, aliases);
    }


    @AllArgsConstructor
    @Getter
    public static class SubCommandWrapper {
        private SubCommand command;
        private Permission permission;
        private List<String> aliases;
    }
}
