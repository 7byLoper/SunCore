package ru.loper.suncore.api.hook;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.leymooo.antirelog.Antirelog;
import ru.leymooo.antirelog.manager.PvPManager;
import ru.loper.suncore.SunCore;

import java.lang.reflect.Method;

public class AntiRelogHook {
    @Getter
    private static boolean hook = false;
    @Getter
    private static Antirelog antirelog;
    @Getter
    private static PvPManager manager;

    public static void hook(Plugin plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("AntiRelog") == null) {
            plugin.getLogger().warning("[SunCore] - AntiRelog не установлен, некоторые функции могут не работать!");
            return;
        }

        antirelog = SunCore.getPlugin(Antirelog.class);
        manager = antirelog.getPvpManager();
        hook = true;
    }

    public static void startPvp(Player attacker, Player attacked) {
        if (!hook) return;

        manager.playerDamagedByPlayer(attacker, attacked);
        manager.playerDamagedByPlayer(attacked, attacker);
    }

    public static void startPvp(Player player) {
        if (!hook) return;

        try {
            Class<? extends PvPManager> clazz = manager.getClass();
            Method method = clazz.getDeclaredMethod("startPvp", Player.class, boolean.class, boolean.class);
            method.setAccessible(true);
            method.invoke(manager, player, false, true);
        } catch (Exception ignored) {
        }
    }

    public static boolean isPvp(Player player) {
        if (!hook) return false;
        return manager.isInPvP(player);
    }

    public static void stopPvp(Player player) {
        if (!hook) return;
        manager.stopPvP(player);
    }
}
