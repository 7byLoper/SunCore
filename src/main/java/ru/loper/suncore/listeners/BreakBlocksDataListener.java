package ru.loper.suncore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import ru.loper.suncore.SunCore;
import ru.loper.suncore.config.ConfigsManager;

public class BreakBlocksDataListener implements Listener {
    private final SunCore plugin = SunCore.getInstance();

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (!ConfigsManager.isBreakBlockStats()) return;
        Bukkit.getScheduler().runTaskAsynchronously(SunCore.getInstance(),
                () -> plugin.breakBlocksData.addBlocks(player.getName(), 1));
    }
}
