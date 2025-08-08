package ru.loper.suncore;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.loper.suncore.api.gui.listener.MenuListener;
import ru.loper.suncore.api.hook.AntiRelogHook;
import ru.loper.suncore.api.redis.RedisManager;
import ru.loper.suncore.commands.core.CoreCommand;
import ru.loper.suncore.config.CoreConfigManager;
import ru.loper.suncore.listeners.ItemsListener;
import ru.loper.suncore.utils.VersionHelper;

import java.util.Optional;
import java.util.logging.Level;

@Getter
public final class SunCore extends JavaPlugin {
    @Getter
    private static NamespacedKey placeKey;
    @Getter
    private static SunCore instance;

    private CoreConfigManager configManager;
    private ItemStack head;

    @Override
    public void onEnable() {
        instance = this;
        placeKey = new NamespacedKey(this, "place");
        initBaseHead();

        configManager = new CoreConfigManager(this);

        AntiRelogHook.hook(this);

        registerCommand("suncore", new CoreCommand(configManager));
        registerListeners(new MenuListener(), new ItemsListener(configManager));

        getLogger().info("""
                
                §e ____               ____
                §e/ ___| _   _ _ __  / ___|___  _ __ ___
                §e\\___ \\| | | | '_ \\| |   / _ \\| '__/ _ \\
                §e ___) | |_| | | | | |__| (_) | | |  __/
                §e|____/ \\__,_|_| |_|\\____\\___/|_|  \\___|
                §fПлагин сделан при поддержке §eSunDev
                §fНовостной канал студии: §at.me/bySunDev
                §fВерсия плагина: §a%s
                §fАвтор плагина: §aLoper
                """.formatted(getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        RedisManager.disableCore();
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

    private <T extends CommandExecutor> void registerCommand(String name, T executor) {
        Optional.ofNullable(getCommand(name)).orElseThrow().setExecutor(executor);
    }

    public static void printStacktrace(String message, Throwable throwable) {
        getInstance().getLogger().log(Level.SEVERE, message, throwable);
    }

    public static int getCoreVersion() {
        String version = getInstance().getDescription().getVersion().replaceAll("[^0-9]", "");
        return Integer.parseInt(version);
    }
}
