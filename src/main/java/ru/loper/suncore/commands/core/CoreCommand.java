package ru.loper.suncore.commands.core;

import org.bukkit.permissions.Permission;
import ru.loper.suncore.api.command.AdvancedSmartCommandExecutor;
import ru.loper.suncore.commands.core.impl.GiveSubCommand;
import ru.loper.suncore.commands.core.impl.ReloadSubCommand;
import ru.loper.suncore.utils.PluginConfigManager;

public class CoreCommand extends AdvancedSmartCommandExecutor {

    private final PluginConfigManager configManager;

    public CoreCommand(PluginConfigManager configManager) {
        this.configManager = configManager;
        addSubCommand(new GiveSubCommand(configManager), new Permission("suncore.give"), "give");
        addSubCommand(new ReloadSubCommand(configManager), new Permission("suncore.reload"), "reload");
    }

    @Override
    public String getDontPermissionMessage() {
        return configManager.getNoPermissionMessage();
    }
}