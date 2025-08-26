package ru.loper.suncore.api.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;


public abstract class AsyncMenu extends Menu {
    public void show(@NotNull Player player) {
        this.opener = player;
        this.inventory = this.createInventory(this.getTitle());
        this.async(() -> {
            this.populateInventory();
            Bukkit.getScheduler().runTaskLater(this.getPlugin(), () -> {
                player.openInventory(this.inventory);
            }, 1L);
        });
    }

    protected void populateInventory() {
        this.inventory.clear();
        this.buttons.clear();
        this.items.clear();
        this.getItemsAndButtons();
        this.sync(this::setInventoryItems);
    }

    private void async(Runnable runnable) {
        CompletableFuture.runAsync(runnable);
    }

    private void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(this.getPlugin(), runnable);
    }
}