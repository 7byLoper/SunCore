package ru.loper.suncore;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.loper.suncore.api.gui.listener.MenuListener;
import ru.loper.suncore.api.hook.AntiRelogHook;
import ru.loper.suncore.commands.core.CoreCommand;
import ru.loper.suncore.database.BlockBreakDataBase;
import ru.loper.suncore.hook.CorePlaceholder;
import ru.loper.suncore.listeners.BreakBlocksDataListener;
import ru.loper.suncore.config.PluginConfigManager;
import ru.loper.suncore.utils.VersionHelper;

import java.util.Optional;
import java.util.logging.Level;

@Getter
public final class SunCore extends JavaPlugin {
    @Getter
    private static SunCore instance;
    private PluginConfigManager configManager;
    private BlockBreakDataBase blockBreakDataData;
    private ItemStack head;

    @Override
    public void onEnable() {
        instance = this;
        initBaseHead();

        new CorePlaceholder(getInstance()).register();

        configManager = new PluginConfigManager(this);
        blockBreakDataData = new BlockBreakDataBase(configManager.getDataBaseManager());

        AntiRelogHook.hook(this);

        registerCommand("suncore", new CoreCommand(configManager));
        registerListeners(new BreakBlocksDataListener(this), new MenuListener());
    }

    @Override
    public void onDisable() {

    }

    private void initBaseHead() {
        if (!VersionHelper.IS_ITEM_LEGACY) {
            this.head = new ItemStack(Material.PLAYER_HEAD, 1);
        } else {
            this.head = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
        }
    }

    public void registerListeners(Listener... listeners) {
        PluginManager manager = Bukkit.getPluginManager();
        for (Listener listener : listeners) {
            manager.registerEvents(listener, getInstance());
        }
    }

    public static void printStacktrace(String message, Throwable throwable) {
        getInstance().getLogger().log(Level.SEVERE, message, throwable);
    }

    private <T extends CommandExecutor> void registerCommand(String name, T executor) {
        Optional.ofNullable(getCommand(name)).orElseThrow().setExecutor(executor);
    }
}
