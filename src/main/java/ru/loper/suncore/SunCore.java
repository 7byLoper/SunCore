package ru.loper.suncore;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.loper.suncore.config.ConfigsManager;
import ru.loper.suncore.data.BreakBlocks;
import ru.loper.suncore.hook.AntiRelog;
import ru.loper.suncore.listeners.BreakBlocksDataListener;
import ru.loper.suncore.utils.Placeholder;
import ru.loper.suncore.utils.VersionHelper;
import ru.loper.suncore.utils.gui.listener.MenuListener;

import java.util.logging.Level;

@Getter
public final class SunCore extends JavaPlugin {
    @Getter
    private static SunCore instance;
    public BreakBlocks breakBlocksData;
    private ItemStack head;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        if (!VersionHelper.IS_ITEM_LEGACY) {
            this.head = new ItemStack(Material.PLAYER_HEAD, 1);
        } else {
            this.head = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
        }
        breakBlocksData = new BreakBlocks(this);
        breakBlocksData.connectToDatabase();
        breakBlocksData.createTable();
        registerListeners(new BreakBlocksDataListener(), new MenuListener());
        new Placeholder(getInstance()).register();
        new ConfigsManager().init();
        AntiRelog.hook(this);
    }

    @Override
    public void onDisable() {
        breakBlocksData.closeDatabase();
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
}
