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
import ru.loper.suncore.api.config.ConfigsManager;
import ru.loper.suncore.database.BreakBlocks;
import ru.loper.suncore.listeners.BreakBlocksDataListener;
import ru.loper.suncore.hook.CorePlaceholder;
import ru.loper.suncore.utils.VersionHelper;

import java.util.Optional;
import java.util.logging.Level;

@Getter
public final class SunCore extends JavaPlugin {
    @Getter
    private static SunCore instance;
    private ConfigsManager configsManager;
    private BreakBlocks breakBlocksData;
    private ItemStack head;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        initBaseHead();

        breakBlocksData = new BreakBlocks(this);
        breakBlocksData.connectToDatabase();
        breakBlocksData.createTable();

        registerListeners(new BreakBlocksDataListener(this), new MenuListener());

        new CorePlaceholder(getInstance()).register();
        configsManager = new ConfigsManager(this);

        AntiRelogHook.hook(this);
    }

    @Override
    public void onDisable() {
        breakBlocksData.closeDatabase();
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
