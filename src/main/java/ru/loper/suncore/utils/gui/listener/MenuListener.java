package ru.loper.suncore.utils.gui.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import ru.loper.suncore.utils.gui.Menu;

public class MenuListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory inventory = e.getClickedInventory();
        if (inventory == null || inventory.getHolder() == null) return;
        if (inventory.getHolder() instanceof Menu menu) menu.onClick(e);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Inventory inventory = e.getInventory();
        if (inventory.getHolder() == null) return;
        if (inventory.getHolder() instanceof Menu menu) menu.onClose(e);
    }
}
