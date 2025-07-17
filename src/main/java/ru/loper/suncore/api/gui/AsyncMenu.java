package ru.loper.suncore.api.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public abstract class AsyncMenu extends Menu {
    @Override
    public void show(@NotNull Player player) {
        opener = player;
        async(() -> {
            String title = getTitle();
            sync(() -> inventory = createInventory(title));
            populateInventory();
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> player.openInventory(inventory), 1L);
        });
    }

    @Override
    protected void populateInventory() {
        inventory.clear();
        buttons.clear();
        items.clear();

        getItemsAndButtons();
        sync(this::setInventoryItems);
    }

    private void async(Runnable runnable) {
        CompletableFuture.runAsync(runnable);
    }

    private void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(getPlugin(), runnable);
    }
}
