package ru.loper.suncore.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import ru.loper.suncore.SunCore;

@RequiredArgsConstructor
public class BreakBlocksDataListener implements Listener {
    private final SunCore plugin;

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (!plugin.getConfigManager().isBreakBlockStats()) return;
        Bukkit.getScheduler().runTaskAsynchronously(SunCore.getInstance(),
                () -> plugin.getBlockBreakDataData().addBlocks(player.getName(), 1));
    }
}
