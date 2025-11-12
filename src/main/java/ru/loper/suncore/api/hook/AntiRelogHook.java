package ru.loper.suncore.api.hook;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.leymooo.antirelog.Antirelog;
import ru.leymooo.antirelog.config.Settings;
import ru.leymooo.antirelog.manager.BossbarManager;
import ru.leymooo.antirelog.manager.PowerUpsManager;
import ru.leymooo.antirelog.manager.PvPManager;
import ru.loper.suncore.SunCore;

import java.lang.reflect.Method;
import java.util.Set;

@UtilityClass
public class AntiRelogHook {
    private static boolean hook = false;
    private static Antirelog antirelog;
    private static PvPManager manager;
    private static Settings settings;

    public static void hook(Plugin plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("AntiRelog") == null) {
            plugin.getLogger().warning("AntiRelog не установлен, некоторые функции могут не работать!");
            return;
        }

        antirelog = SunCore.getPlugin(Antirelog.class);
        manager = antirelog.getPvpManager();
        settings = antirelog.getSettings();
        hook = true;
    }

    public static void startPvp(Player attacker, Player attacked) {
        if (!hook) return;
        manager.playerDamagedByPlayer(attacker, attacked);
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

    public static boolean isSilentPvp(Player player) {
        if (!hook) return false;
        return manager.isInSilentPvP(player);
    }

    public static void stopPvp(Player player) {
        if (!hook) return;
        manager.stopPvP(player);
    }

    public static void stopPvpSilent(Player player) {
        if (!hook) return;
        manager.stopPvPSilent(player);
    }

    public static int getTimeRemainingInPvP(Player player) {
        if (!hook) return 0;
        return manager.getTimeRemainingInPvP(player);
    }

    public static int getTimeRemainingInPvPSilent(Player player) {
        if (!hook) return 0;
        return manager.getTimeRemainingInPvPSilent(player);
    }

    public static boolean isPvPModeEnabled() {
        if (!hook) return false;
        return manager.isPvPModeEnabled();
    }

    public static boolean isBypassed(Player player) {
        if (!hook) return false;
        return manager.isBypassed(player);
    }

    public static boolean hasBypassPermission(Player player) {
        if (!hook) return false;
        return manager.isHasBypassPermission(player);
    }

    public static PowerUpsManager getPowerUpsManager() {
        if (!hook) return null;
        return manager.getPowerUpsManager();
    }

    public static void disablePowerUps(Player player) {
        if (!hook) return;
        manager.getPowerUpsManager().disablePowerUps(player);
    }

    public static void disablePowerUpsWithRunCommands(Player player) {
        if (!hook) return;
        manager.getPowerUpsManager().disablePowerUpsWithRunCommands(player);
    }

    public static BossbarManager getBossbarManager() {
        if (!hook) return null;
        return manager.getBossbarManager();
    }

    public static void setBossBar(Player player, int time) {
        if (!hook) return;
        manager.getBossbarManager().setBossBar(player, time);
    }

    public static void clearBossbar(Player player) {
        if (!hook) return;
        manager.getBossbarManager().clearBossbar(player);
    }

    public static boolean isCommandWhiteListed(String command) {
        if (!hook) return false;
        return manager.isCommandWhiteListed(command);
    }

    public static Set<String> getWhiteListedCommands() {
        if (!hook) return Set.of();
        try {
            Class<? extends PvPManager> clazz = manager.getClass();
            Method method = clazz.getDeclaredMethod("getWhiteListedCommands");
            method.setAccessible(true);
            return (Set<String>) method.invoke(manager);
        } catch (Exception e) {
            return Set.of();
        }
    }

    public static int getPvpTime() {
        if (!hook) return 0;
        return settings.getPvpTime();
    }

    public static boolean isDisablePowerups() {
        if (!hook) return false;
        return settings.isDisablePowerups();
    }

    public static boolean isDisableCommandsInPvp() {
        if (!hook) return false;
        return settings.isDisableCommandsInPvp();
    }

    public static boolean isDisablePvpInIgnoredRegion() {
        if (!hook) return false;
        return settings.isDisablePvpInIgnoredRegion();
    }

    public static void updatePvpTime(Player player, int newTime) {
        if (!hook) return;

        boolean isSilent = isSilentPvp(player);
        boolean bypassed = hasBypassPermission(player);

        try {
            Class<? extends PvPManager> clazz = manager.getClass();
            Method method = clazz.getDeclaredMethod("updatePvpMode", Player.class, boolean.class, int.class);
            method.setAccessible(true);
            method.invoke(manager, player, bypassed || isSilent, newTime);
        } catch (Exception ignored) {
        }
    }

    public static void clearAllPvp() {
        if (!hook) return;
        try {
            Class<? extends PvPManager> clazz = manager.getClass();
            Method onPluginDisable = clazz.getDeclaredMethod("onPluginDisable");
            onPluginDisable.setAccessible(true);
            onPluginDisable.invoke(manager);
        } catch (Exception ignored) {
        }
    }

    public static boolean isHooked() {
        return hook;
    }

    public static Antirelog getAntirelogInstance() {
        return antirelog;
    }
}