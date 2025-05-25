package ru.loper.suncore.api.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AsyncMenu extends Menu {
    @Override
    public void show(@NotNull Player player) {
        opener = player;
        inventory = createInventory(getTitle());
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            populateInventory();
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> player.openInventory(inventory), 1L);
        });
    }
}
