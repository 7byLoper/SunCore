package ru.loper.suncore.commands.core;

import org.bukkit.permissions.Permission;
import ru.loper.suncore.api.command.AdvancedSmartCommandExecutor;
import ru.loper.suncore.commands.core.impl.GiveSubCommand;
import ru.loper.suncore.commands.core.impl.ReloadSubCommand;
import ru.loper.suncore.commands.core.impl.SaveSubCommand;
import ru.loper.suncore.config.CoreConfigManager;

public class CoreCommand extends AdvancedSmartCommandExecutor {

    private final CoreConfigManager configManager;

    public CoreCommand(CoreConfigManager configManager) {
        this.configManager = configManager;

        addSubCommand(new SaveSubCommand(configManager), new Permission("suncore.save"), "save");
        addSubCommand(new GiveSubCommand(configManager), new Permission("suncore.give"), "give");
        addSubCommand(new ReloadSubCommand(configManager), new Permission("suncore.reload"), "reload");
    }

    @Override
    public String getDontPermissionMessage() {
        return configManager.getNoPermissionMessage();
    }
}